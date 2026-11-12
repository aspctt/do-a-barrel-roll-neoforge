package nl.enjarai.doabarrelroll.net;

import com.mojang.serialization.Codec;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.level.ServerPlayer;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigResponseC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;
import nl.enjarai.doabarrelroll.util.DelayedRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

public class HandshakeServer<P extends ConfigSyncS2CPacket> {
    public static final int PROTOCOL_VERSION = 4;

    private final PacketConstructor<P> packetConstructor;
    private final ServerConfigHolder configHolder;
    private final Map<ServerGamePacketListenerImpl, ClientInfo> syncStates = new WeakHashMap<>();
    private final Map<ServerGamePacketListenerImpl, DelayedRunnable> scheduledKicks = new WeakHashMap<>();
    private final Function<ServerGamePacketListenerImpl, Boolean> getsLimitedCheck;
    private final Codec<ModConfigServer> transferCodec = ModConfigServer.CODEC;
    private final Codec<LimitedModConfigServer> limitedTransferCodec = LimitedModConfigServer.getCodec();

    public HandshakeServer(PacketConstructor<P> packetConstructor, ServerConfigHolder configHolder, Function<ServerGamePacketListenerImpl, Boolean> getsLimitedCheck) {
        this.packetConstructor = packetConstructor;
        this.configHolder = configHolder;
        this.getsLimitedCheck = getsLimitedCheck;
    }

    public void tick(MinecraftServer server) {
        var it = scheduledKicks.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (entry.getValue().isDone()) {
                it.remove();
            } else {
                entry.getValue().tick();
            }
        }
    }

    public ClientInfo getHandshakeState(ServerPlayer player) {
        return getHandshakeState(player.connection);
    }

    public ClientInfo getHandshakeState(ServerGamePacketListenerImpl handler) {
        return syncStates.computeIfAbsent(handler, key -> new ClientInfo(HandshakeState.NOT_SENT, PROTOCOL_VERSION, true));
    }

    /**
     * Builds the config packet for one player.
     *
     * <p>The config the client acts on goes through getLimited, which swaps in the
     * operator config for anyone holding level 2 or the ignore_config permission.
     * Upstream sent the plain config here and never called getLimited at all, so
     * that documented bypass did nothing: operators were held to allowThrusting and
     * forceEnabled like everyone else.
     */
    public P initiateConfigSync(ServerGamePacketListenerImpl handler) {
        var isLimited = getsLimitedCheck.apply(handler);
        getHandshakeState(handler).isLimited = isLimited;
        ModConfigServer config = configHolder.instance;

        return packetConstructor.construct(PROTOCOL_VERSION, config.getLimited(handler), isLimited, isLimited ? ModConfigServer.DEFAULT : config);
    }

    public void configSentToClient(ServerGamePacketListenerImpl handler) {
        getHandshakeState(handler).state = HandshakeState.SENT;

        var config = configHolder.instance;
        if (config.getSyncTimeout() != null) {
            // A client that accepted once has the mod, so a slow reply to a later re-send is no
            // reason to kick it
            scheduledKicks.put(handler, new DelayedRunnable(config.getSyncTimeout(), () -> {
                if (!getHandshakeState(handler).hasAccepted) {
                    DoABarrelRoll.LOGGER.warn(
                            "{} did not accept config syncing, config indicates we kick them.",
                            handler.getPlayer().getName().getString()
                    );
                    handler.disconnect(config.getSyncTimeoutMessage());
                }
            }));
        }
    }

    public HandshakeState clientReplied(ServerGamePacketListenerImpl handler, ConfigResponseC2SPacket packet) {
        var info = getHandshakeState(handler);
        var player = handler.getPlayer();

        if (info.state == HandshakeState.SENT) {
            var protocolVersion = packet.protocolVersion();
            if (protocolVersion < 1 || protocolVersion > PROTOCOL_VERSION) {
                DoABarrelRoll.LOGGER.warn(
                        "Client of {} sent unknown protocol version, expected range 1-{}, got {}. Will attempt to proceed anyway.",
                        player.getName().getString(),
                        PROTOCOL_VERSION,
                        protocolVersion
                );
            }

            if (protocolVersion == 2 && info.protocolVersion != 2) {
                DoABarrelRoll.LOGGER.info("Client of {} is using an older protocol version, resending.", player.getName().getString());
                info.state = HandshakeState.RESEND;
            } else if (packet.success()) {
                DoABarrelRoll.LOGGER.info("Client of {} accepted server config.", player.getName().getString());
                info.state = HandshakeState.ACCEPTED;
                info.hasAccepted = true;
            } else {
                DoABarrelRoll.LOGGER.warn(
                        "Client of {} failed to process server config, check client logs find what went wrong.",
                        player.getName().getString());
                info.state = HandshakeState.FAILED;
                info.hasAccepted = false;
            }
            info.protocolVersion = protocolVersion;
        }

        return info.state;
    }

    /**
     * Forgets a player who left. The pending kick goes too: left in place it would
     * still fire, log that the player refused the config and disconnect a connection
     * that is already closed.
     */
    public void playerDisconnected(ServerGamePacketListenerImpl handler) {
        syncStates.remove(handler);
        scheduledKicks.remove(handler);
    }

    public static class ClientInfo {
        public HandshakeState state;
        public int protocolVersion;
        public boolean isLimited;

        /**
         * Whether this connection has accepted a config, as opposed to state, which drops back to SENT
         * every time the config is sent again. Whatever only needs to know the client runs the mod
         * checks this. Upstream registered its receivers once on acceptance, so they stayed open
         * through re-sends; checking state here instead dropped any packet that arrived while a
         * re-send was out, such as the second half of a Server tab save, since the first half is what
         * triggers the re-send.
         */
        public boolean hasAccepted;

        public ClientInfo(HandshakeState state, int protocolVersion, boolean isLimited) {
            this.state = state;
            this.protocolVersion = protocolVersion;
            this.isLimited = isLimited;
        }
    }

    public interface PacketConstructor<P extends ConfigSyncS2CPacket> {
        P construct(int protocolVersion, LimitedModConfigServer applicableConfig, boolean isLimited, ModConfigServer fullConfig);
    }

    public enum HandshakeState {
        NOT_SENT,
        SENT,
        ACCEPTED,
        FAILED,
        RESEND
    }
}

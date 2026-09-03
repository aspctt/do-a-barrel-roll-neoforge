package nl.enjarai.doabarrelroll.net;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.event.ServerEvents;
import nl.enjarai.doabarrelroll.config.ModConfigServer;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateAckS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.RollSyncS2CPacket;

public class ServerNetworking {
    public static final ServerConfigHolder<ConfigUpdateAckS2CPacket> CONFIG_HOLDER = new ServerConfigHolder<>(
            FMLPaths.CONFIGDIR.get().resolve(DoABarrelRoll.MODID + "-server.json"),
            ModConfigServer.CODEC, ConfigUpdateAckS2CPacket::new, ServerEvents::updateServerConfig
    );
    public static final HandshakeServer<ConfigSyncS2CPacket> HANDSHAKE_SERVER = new HandshakeServer<>(
            ConfigSyncS2CPacket::new, CONFIG_HOLDER, player -> !ModConfigServer.canModify(player));

    public static void init() {
        CONFIG_HOLDER.setHandshakeServer(HANDSHAKE_SERVER);

        ServerEvents.SERVER_CONFIG_UPDATE.register((server, config) -> {
            for (var player : server.getPlayerList().getPlayers()) {
                sendHandshake(player);
            }
        });
    }

    public static void sendHandshake(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, HANDSHAKE_SERVER.initiateConfigSync(player.connection));
        HANDSHAKE_SERVER.configSentToClient(player.connection);
    }

    /**
     * Broadcasts an entity's roll to everyone who can see it and has completed the
     * handshake.
     *
     * <p>NeoForge throws rather than dropping a payload aimed at a client that has
     * not registered the channel, so the receiver list is filtered here rather than
     * handed to {@code PacketDistributor.sendToPlayersTrackingEntity}. The
     * handshake state alone would be enough in practice; the channel check is the
     * belt to its braces, because being wrong about it disconnects a player.
     */
    public static void sendRollUpdates(Entity entity) {
        if (!(entity.level() instanceof ServerLevel level)) {
            return;
        }

        var rollEntity = (RollEntity) entity;
        var payload = new RollSyncS2CPacket(
                entity.getId(),
                rollEntity.doABarrelRoll$isRolling(),
                rollEntity.doABarrelRoll$getRoll()
        );

        // The same range vanilla tracks an entity at: its type's range in chunks,
        // capped by the server view distance. Squared, so the comparison below
        // needs no square root per player.
        int chunks = Math.min(entity.getType().clientTrackingRange(), level.getServer().getPlayerList().getViewDistance());
        double range = chunks * 16.0;
        double rangeSq = range * range;

        for (var player : level.players()) {
            if (player == entity) continue;
            if (player.distanceToSqr(entity) > rangeSq) continue;
            if (HANDSHAKE_SERVER.getHandshakeState(player).state != HandshakeServer.HandshakeState.ACCEPTED) continue;
            if (!NetworkRegistry.hasChannel(player.connection, RollSyncS2CPacket.PACKET_ID.id())) continue;

            PacketDistributor.sendToPlayer(player, payload);
        }
    }
}

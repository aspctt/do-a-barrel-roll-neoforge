package nl.enjarai.doabarrelroll.net;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.net.packet.ConfigResponseC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateAckS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.RollSyncC2SPacket;
import nl.enjarai.doabarrelroll.net.packet.RollSyncS2CPacket;

/**
 * Declares every payload and its handler.
 *
 * <p>All of them are optional, because a client or server without the mod is a
 * supported configuration on both sides: nothing here may make a connection fail
 * to negotiate.
 *
 * <p>Upstream registers the roll and acknowledgement receivers only once the
 * handshake has been accepted. NeoForge takes every registration during startup,
 * so the handlers check the handshake state themselves instead.
 */
public class ModNetworking {
    /**
     * Bumping this rejects NeoForge-to-NeoForge connections whose payload logic
     * differs. It is not the mod's own handshake protocol version, which lives in
     * {@link HandshakeServer#PROTOCOL_VERSION} and is what talks to Fabric servers
     * and to third-party plugins.
     */
    private static final String PAYLOAD_VERSION = "1";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(PAYLOAD_VERSION).optional();

        registrar.playToClient(
                ConfigSyncS2CPacket.PACKET_ID,
                ConfigSyncS2CPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPayloadHandlers.handleConfigSync(payload, context))
        );

        registrar.playToClient(
                RollSyncS2CPacket.PACKET_ID,
                RollSyncS2CPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPayloadHandlers.handleRollSync(payload))
        );

        registrar.playToClient(
                ConfigUpdateAckS2CPacket.PACKET_ID,
                ConfigUpdateAckS2CPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> ClientPayloadHandlers.handleConfigUpdateAck(payload))
        );

        registrar.playToServer(
                ConfigResponseC2SPacket.PACKET_ID,
                ConfigResponseC2SPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var player = (ServerPlayer) context.player();
                    var reply = ServerNetworking.HANDSHAKE_SERVER.clientReplied(player.connection, payload);

                    // Resending happens when the client turns out to speak a
                    // different protocol version than the one we assumed.
                    if (reply == HandshakeServer.HandshakeState.RESEND) {
                        ServerNetworking.sendHandshake(player);
                    }
                })
        );

        registrar.playToServer(
                RollSyncC2SPacket.PACKET_ID,
                RollSyncC2SPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var player = (ServerPlayer) context.player();
                    if (!hasAcceptedHandshake(player)) return;

                    var rollPlayer = (RollEntity) player;
                    var isRolling = payload.rolling();

                    rollPlayer.doABarrelRoll$setRolling(isRolling);
                    rollPlayer.doABarrelRoll$setRoll(isRolling ? Mth.wrapDegrees(payload.roll()) : 0);
                })
        );

        registrar.playToServer(
                ConfigUpdateC2SPacket.PACKET_ID,
                ConfigUpdateC2SPacket.PACKET_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    var player = (ServerPlayer) context.player();
                    if (!hasAcceptedHandshake(player)) return;

                    context.reply(ServerNetworking.CONFIG_HOLDER.clientSendsUpdate(player, payload));
                })
        );
    }

    private static boolean hasAcceptedHandshake(ServerPlayer player) {
        return ServerNetworking.HANDSHAKE_SERVER.getHandshakeState(player).state
                == HandshakeServer.HandshakeState.ACCEPTED;
    }
}

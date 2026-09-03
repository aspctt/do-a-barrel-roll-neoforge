package nl.enjarai.doabarrelroll.net;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.net.packet.ConfigSyncS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.ConfigUpdateAckS2CPacket;
import nl.enjarai.doabarrelroll.net.packet.RollSyncS2CPacket;

/**
 * Bodies of the clientbound payload handlers.
 *
 * <p>Kept apart from {@link ModNetworking} so the client-only types they touch
 * never enter that class. Payload registration is symmetric, so a dedicated
 * server runs the registration code too; it just never reaches these, and the
 * class is only loaded when one of them first runs.
 */
public class ClientPayloadHandlers {
    public static void handleConfigSync(ConfigSyncS2CPacket payload, IPayloadContext context) {
        context.reply(ClientNetworking.HANDSHAKE_CLIENT.handleConfigSync(payload));
    }

    public static void handleRollSync(RollSyncS2CPacket payload) {
        if (!ClientNetworking.HANDSHAKE_CLIENT.hasConnected()) return;

        var level = Minecraft.getInstance().level;
        if (level == null) return;

        var entity = level.getEntity(payload.entityId());
        if (entity == null) return;

        var rollEntity = (RollEntity) entity;
        rollEntity.doABarrelRoll$setRolling(payload.rolling());
        rollEntity.doABarrelRoll$setRoll(Mth.wrapDegrees(payload.roll()));
    }

    public static void handleConfigUpdateAck(ConfigUpdateAckS2CPacket payload) {
        if (!ClientNetworking.HANDSHAKE_CLIENT.hasConnected()) return;

        ClientNetworking.CONFIG_UPDATE_CLIENT.updateAcknowledged(payload);
    }
}

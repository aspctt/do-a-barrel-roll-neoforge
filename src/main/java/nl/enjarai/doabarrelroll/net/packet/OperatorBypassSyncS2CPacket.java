package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

/**
 * Tells a player who may edit the server config whether operatorBypass is on, so the Server tab can
 * show it. Sent right after the config sync, since that clears what the client knew about the server.
 */
public record OperatorBypassSyncS2CPacket(boolean operatorBypass) implements CustomPacketPayload {
    public static final Type<OperatorBypassSyncS2CPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("operator_bypass_sync"));
    public static final StreamCodec<ByteBuf, OperatorBypassSyncS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OperatorBypassSyncS2CPacket::operatorBypass,
            OperatorBypassSyncS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

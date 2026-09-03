package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record RollSyncS2CPacket(int entityId, boolean rolling, float roll) implements CustomPacketPayload {
    public static final Type<RollSyncS2CPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("roll_sync"));
    public static final StreamCodec<ByteBuf, RollSyncS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, RollSyncS2CPacket::entityId,
            ByteBufCodecs.BOOL, RollSyncS2CPacket::rolling,
            ByteBufCodecs.FLOAT, RollSyncS2CPacket::roll,
            RollSyncS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record ConfigUpdateAckS2CPacket(int protocolVersion, boolean success) implements CustomPacketPayload {
    public static final Type<ConfigUpdateAckS2CPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("config_update_ack"));
    public static final StreamCodec<ByteBuf, ConfigUpdateAckS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ConfigUpdateAckS2CPacket::protocolVersion,
            ByteBufCodecs.BOOL, ConfigUpdateAckS2CPacket::success,
            ConfigUpdateAckS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

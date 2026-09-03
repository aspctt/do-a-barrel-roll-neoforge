package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

public record ConfigResponseC2SPacket(int protocolVersion, boolean success) implements CustomPacketPayload {
    public static final Type<ConfigResponseC2SPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("config_response"));
    public static final StreamCodec<ByteBuf, ConfigResponseC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ConfigResponseC2SPacket::protocolVersion,
            ByteBufCodecs.BOOL, ConfigResponseC2SPacket::success,
            ConfigResponseC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

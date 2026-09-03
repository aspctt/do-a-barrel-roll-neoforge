package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigUpdateC2SPacket(int protocolVersion, ModConfigServer config) implements CustomPacketPayload {
    public static final Type<ConfigUpdateC2SPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("config_update"));
    public static final StreamCodec<ByteBuf, ConfigUpdateC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ConfigUpdateC2SPacket::protocolVersion,
            ModConfigServer.PACKET_CODEC, ConfigUpdateC2SPacket::config,
            ConfigUpdateC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

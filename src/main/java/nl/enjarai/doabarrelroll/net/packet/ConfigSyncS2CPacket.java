package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfigServer;

public record ConfigSyncS2CPacket(int protocolVersion, LimitedModConfigServer applicableConfig, boolean isLimited, ModConfigServer fullConfig) implements CustomPacketPayload {
    public static final Type<ConfigSyncS2CPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("config_sync"));
    public static final StreamCodec<ByteBuf, ConfigSyncS2CPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ConfigSyncS2CPacket::protocolVersion,
            LimitedModConfigServer.getPacketCodec(), ConfigSyncS2CPacket::applicableConfig,
            ByteBufCodecs.BOOL, ConfigSyncS2CPacket::isLimited,
            ModConfigServer.PACKET_CODEC, ConfigSyncS2CPacket::fullConfig,
            ConfigSyncS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

package nl.enjarai.doabarrelroll.net.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import nl.enjarai.doabarrelroll.DoABarrelRoll;

/**
 * Asks the server to change operatorBypass, from the Server tab. The server answers with the same
 * acknowledgement a config update gets.
 */
public record OperatorBypassUpdateC2SPacket(boolean operatorBypass) implements CustomPacketPayload {
    public static final Type<OperatorBypassUpdateC2SPacket> PACKET_ID = new Type<>(DoABarrelRoll.id("operator_bypass_update"));
    public static final StreamCodec<ByteBuf, OperatorBypassUpdateC2SPacket> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OperatorBypassUpdateC2SPacket::operatorBypass,
            OperatorBypassUpdateC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

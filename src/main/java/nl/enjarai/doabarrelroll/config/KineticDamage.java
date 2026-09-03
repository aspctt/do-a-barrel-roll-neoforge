package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public enum KineticDamage {
    VANILLA,
    HIGH_SPEED,
    NONE,
    INSTANT_KILL;

    public static final Codec<KineticDamage> CODEC = Codec.STRING.comapFlatMap(name -> {
        try {
            return DataResult.success(valueOf(name));
        } catch (IllegalArgumentException e) {
            return DataResult.error(() -> "Unknown kinetic damage type: " + name);
        }
    }, KineticDamage::name);
    public static final StreamCodec<ByteBuf, KineticDamage> PACKET_CODEC =
            ByteBufCodecs.STRING_UTF8.map(KineticDamage::valueOf, KineticDamage::name);
}

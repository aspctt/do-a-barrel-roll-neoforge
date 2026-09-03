package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;

public interface LimitedModConfigServer {
    LimitedModConfigServer OPERATOR = new Impl(true, false);

    static Codec<LimitedModConfigServer> getCodec() {
        return RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("allowThrusting", ModConfigServer.DEFAULT.allowThrusting()).forGetter(LimitedModConfigServer::allowThrusting),
                Codec.BOOL.optionalFieldOf("forceEnabled", ModConfigServer.DEFAULT.forceEnabled()).forGetter(LimitedModConfigServer::forceEnabled)
        ).apply(instance, Impl::new));
    }

    static StreamCodec<ByteBuf, LimitedModConfigServer> getPacketCodec() {
        return StreamCodec.composite(
                ByteBufCodecs.BOOL, LimitedModConfigServer::allowThrusting,
                ByteBufCodecs.BOOL, LimitedModConfigServer::forceEnabled,
                Impl::new
        );
    }

    boolean allowThrusting();

    boolean forceEnabled();

    record Impl(boolean allowThrusting, boolean forceEnabled) implements LimitedModConfigServer {
    }
}

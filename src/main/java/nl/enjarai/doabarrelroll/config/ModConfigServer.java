package nl.enjarai.doabarrelroll.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.chat.Component;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.net.SyncableConfig;
import nl.enjarai.doabarrelroll.net.ValidatableConfig;

/**
 * The server-wide settings.
 *
 * <p>operatorBypass is the one setting this port added. It is saved in the config file like the rest,
 * but it never goes through {@link #PACKET_CODEC}: changing that layout changes the config sync and
 * update payloads, and NeoForge refuses a connection outright when the two sides disagree on a
 * payload. It travels on channels of its own instead, which a client or server from before the setting
 * simply does not have, so the two still connect.
 */
public record ModConfigServer(boolean allowThrusting,
                              boolean forceEnabled,
                              boolean forceInstalled,
                              int installedTimeout,
                              KineticDamage kineticDamage,
                              boolean operatorBypass) implements SyncableConfig<ModConfigServer, LimitedModConfigServer>, LimitedModConfigServer, ValidatableConfig {
    public static final ModConfigServer DEFAULT = new ModConfigServer(
            false, false, false, 40, KineticDamage.VANILLA, false);

    public static final Codec<ModConfigServer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("allowThrusting", DEFAULT.allowThrusting()).forGetter(ModConfigServer::allowThrusting),
            Codec.BOOL.optionalFieldOf("forceEnabled", DEFAULT.forceEnabled()).forGetter(ModConfigServer::forceEnabled),
            Codec.BOOL.optionalFieldOf("forceInstalled", DEFAULT.forceInstalled()).forGetter(ModConfigServer::forceInstalled),
            Codec.INT.optionalFieldOf("installedTimeout", DEFAULT.installedTimeout()).forGetter(ModConfigServer::installedTimeout),
            KineticDamage.CODEC.optionalFieldOf("kineticDamage", DEFAULT.kineticDamage()).forGetter(ModConfigServer::kineticDamage),
            Codec.BOOL.optionalFieldOf("operatorBypass", DEFAULT.operatorBypass()).forGetter(ModConfigServer::operatorBypass)
    ).apply(instance, ModConfigServer::new));

    // Upstream's layout, unchanged. A config read from it carries the default operatorBypass, which only
    // stands in for "not sent": the server keeps its own value when an update arrives this way.
    public static final StreamCodec<ByteBuf, ModConfigServer> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ModConfigServer::allowThrusting,
            ByteBufCodecs.BOOL, ModConfigServer::forceEnabled,
            ByteBufCodecs.BOOL, ModConfigServer::forceInstalled,
            ByteBufCodecs.INT, ModConfigServer::installedTimeout,
            KineticDamage.PACKET_CODEC, ModConfigServer::kineticDamage,
            (allowThrusting, forceEnabled, forceInstalled, installedTimeout, kineticDamage) -> new ModConfigServer(
                    allowThrusting, forceEnabled, forceInstalled, installedTimeout, kineticDamage, DEFAULT.operatorBypass())
    );

    public ModConfigServer withOperatorBypass(boolean operatorBypass) {
        return new ModConfigServer(allowThrusting, forceEnabled, forceInstalled, installedTimeout, kineticDamage, operatorBypass);
    }

    @Override
    public Integer getSyncTimeout() {
        return forceInstalled ? installedTimeout : null;
    }

    // A literal rather than a translation, since the player being kicked does not have the mod's lang
    // files. Upstream named its own Fabric release here, which a NeoForge player cannot use.
    @Override
    public Component getSyncTimeoutMessage() {
        return Component.nullToEmpty("Please install Do Another Barrel Roll to play on this server.");
    }

    /**
     * The config a player's client acts on. With operatorBypass on, level 2 operators and holders of the
     * ignore_config permission get the operator config, which allows thrusting and does not force the
     * mod on. Off by default, since upstream documented the bypass but never applied it, so servers
     * have only ever run without it.
     */
    @Override
    public LimitedModConfigServer getLimited(ServerGamePacketListenerImpl handler) {
        if (!operatorBypass) return this;

        return DoABarrelRoll.checkPermission(handler, DoABarrelRoll.MODID + ".ignore_config", 2)
                ? LimitedModConfigServer.OPERATOR : this;
    }

    public static boolean canModify(ServerGamePacketListenerImpl net) {
        return DoABarrelRoll.checkPermission(net, DoABarrelRoll.MODID + ".configure", 3);
    }

    @Override
    public boolean isValid() {
        return installedTimeout > 0;
    }
}

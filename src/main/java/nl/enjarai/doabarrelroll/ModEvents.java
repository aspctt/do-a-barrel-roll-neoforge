package nl.enjarai.doabarrelroll;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import nl.enjarai.doabarrelroll.net.ServerNetworking;

/**
 * Server-side and side-agnostic game bus subscribers.
 */
@EventBusSubscriber(modid = DoABarrelRoll.MODID)
public class ModEvents {
    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        ServerNetworking.HANDSHAKE_SERVER.tick(event.getServer());
    }

    @SubscribeEvent
    public static void playerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerNetworking.HANDSHAKE_SERVER.playerDisconnected(player.connection);
        }
    }

    /**
     * Applies the server's kinetic damage setting to elytra wall impacts.
     *
     * <p>Upstream reaches into a local of {@code LivingEntity.travel} to do this.
     * NeoForge fires an event for incoming damage carrying its own damage type, so
     * the same rule is expressed against {@link DamageTypes#FLY_INTO_WALL} instead
     * of against a local variable index that shifts whenever the method is
     * recompiled.
     */
    @SubscribeEvent
    public static void kineticDamage(LivingIncomingDamageEvent event) {
        if (!event.getSource().is(DamageTypes.FLY_INTO_WALL)) return;

        switch (ServerNetworking.CONFIG_HOLDER.instance.kineticDamage()) {
            case VANILLA -> {
                // Left alone, so the damage container is not touched at all.
            }
            // Vanilla only hurts when the computed damage is positive, so
            // subtracting past zero cancels rather than passing a negative on.
            case HIGH_SPEED -> {
                float reduced = event.getAmount() - 2.0f;
                if (reduced <= 0.0f) {
                    event.setCanceled(true);
                } else {
                    event.setAmount(reduced);
                }
            }
            case NONE -> event.setCanceled(true);
            case INSTANT_KILL -> event.setAmount(Float.MAX_VALUE);
        }
    }
}

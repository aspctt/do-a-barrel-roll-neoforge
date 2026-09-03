package nl.enjarai.doabarrelroll.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import nl.enjarai.doabarrelroll.ModKeybindings;
import nl.enjarai.doabarrelroll.api.event.ThrustEvents;
import nl.enjarai.doabarrelroll.config.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// High priority to ensure compat with mods like Elytra Aeronautics.
@Mixin(value = LivingEntity.class, priority = 1200)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyArg(
            method = "travel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V",
                    ordinal = 6
            )
    )
    private Vec3 doABarrelRoll$wrapElytraVelocity(Vec3 original) {
        if (!((Object) this instanceof LocalPlayer) || !ModConfig.INSTANCE.getEnableThrust()) return original;

        Vec3 rotation = getLookAngle();
        Vec3 velocity = getDeltaMovement();

        double throttleSign = ModKeybindings.THRUST_FORWARD.isDown() ? 1 : ModKeybindings.THRUST_BACKWARD.isDown() ? -1 : 0;
        throttleSign = ThrustEvents.modifyThrustInput(throttleSign);

        if (ModConfig.INSTANCE.getThrustParticles()) {
            int particleDensity = (int) Mth.clamp(throttleSign * 10, 0, 10);
            if (throttleSign > 0.1 && level().getGameTime() % (11 - particleDensity) == 0) {
                var pPos = position().add(velocity.scale(0.5).reverse());
                level().addParticle(
                        ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        pPos.x(), pPos.y(), pPos.z(),
                        0, 0, 0
                );
            }
        }

        double maxSpeed = ModConfig.INSTANCE.getMaxThrust();
        double speedIncrease = Math.max(maxSpeed - velocity.length(), 0) / maxSpeed * throttleSign;
        double acceleration = ModConfig.INSTANCE.getThrustAcceleration() * speedIncrease;

        return original.add(
                rotation.x * acceleration,
                rotation.y * acceleration,
                rotation.z * acceleration
        );
    }
}

package nl.enjarai.doabarrelroll.mixin.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import nl.enjarai.doabarrelroll.config.ActivationBehaviour;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.util.MixinHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(
            method = "tryToStartFallFlying()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void doABarrelRoll$interceptFallFlyingStart(CallbackInfoReturnable<Boolean> cir) {
        // We do the same checks the original method does, but leave out the one about already fallFlying.
        // This is needed for the hybrid mode.
        if (this.onGround() || this.isInWater() || this.hasEffect(MobEffects.LEVITATION)) {
            return;
        }

        var behaviour = ModConfig.INSTANCE.getActivationBehaviour();

        if ((((Player) (Object) this) instanceof LocalPlayer)
                && (behaviour == ActivationBehaviour.TRIPLE_JUMP
                || behaviour == ActivationBehaviour.HYBRID
                || behaviour == ActivationBehaviour.HYBRID_TOGGLE)) {

            var shouldCancel = behaviour == ActivationBehaviour.TRIPLE_JUMP;

            // This code is only reached if the player is currently jumping,
            // so by checking if they were jumping last tick, we know that this is the start of a jump.
            if (!MixinHooks.wasJumping) {
                MixinHooks.wasJumping = true;
                if (!MixinHooks.secondJump) {
                    MixinHooks.secondJump = true;
                    if (shouldCancel) cir.setReturnValue(false);
                } else {
                    // Set thirdJump to true if we're in HYBRID mode, but toggle it in HYBRID_TOGGLE mode.
                    MixinHooks.thirdJump = behaviour != ActivationBehaviour.HYBRID_TOGGLE || !MixinHooks.thirdJump;
                }
                // Reaching this point is the only way for the function to progress, activating the Elytra.
            } else {
                if (shouldCancel) cir.setReturnValue(false);
            }
        }
    }
}

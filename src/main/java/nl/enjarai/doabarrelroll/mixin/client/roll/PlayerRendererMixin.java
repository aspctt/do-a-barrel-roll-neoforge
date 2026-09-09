package nl.enjarai.doabarrelroll.mixin.client.roll;

//? if <1.21.11 {
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.injection.ModifyArg;
//?} else {
/*import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if <1.21.11 {
@Mixin(PlayerRenderer.class)
//?} else
/*@Mixin(AvatarRenderer.class)*/
public abstract class PlayerRendererMixin {
    //? if <1.21.11 {
    /**
     * Replaces the bank vanilla applies to a flying player model, which is derived
     * from the angle between look and velocity, with the player's actual roll.
     * Ordinal 1 is that rotation; ordinal 0 is the pitch-down the elytra pose uses.
     */
    @ModifyArg(
            method = "setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    ordinal = 1
            ),
            index = 0
    )
    private Quaternionf doABarrelRoll$modifyRoll(Quaternionf original,
                                                 @Local(argsOnly = true) AbstractClientPlayer player,
                                                 @Local(argsOnly = true, ordinal = 2) float tickDelta) {
        var rollEntity = (RollEntity) player;

        if (rollEntity.doABarrelRoll$isRolling()) {
            var roll = rollEntity.doABarrelRoll$getRoll(tickDelta);
            return Axis.YP.rotationDegrees(roll);
        }

        return original;
    }
    //?} else {
    /*// Replaces the bank vanilla applies to a flying player model, which is derived from the angle
    // between look and velocity, with the player's actual roll.
    //
    // 1.21.11 moved rendering onto render states, and the bank is one of them: setupRotations only
    // reads flyingYRot back out. Overwriting it as the state is extracted leaves vanilla to apply the
    // rotation, so there is nothing left to intercept in the renderer itself. The field is in radians,
    // where the rotation this replaces was in degrees.
    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("RETURN"))
    private void doABarrelRoll$modifyRoll(Avatar entity, AvatarRenderState state, float tickDelta, CallbackInfo ci) {
        var rollEntity = (RollEntity) entity;

        if (rollEntity.doABarrelRoll$isRolling()) {
            state.shouldApplyFlyingYRot = true;
            state.flyingYRot = rollEntity.doABarrelRoll$getRoll(tickDelta) * ((float) Math.PI / 180.0F);
        }
    }
    *///?}
}

package nl.enjarai.doabarrelroll.mixin.client.roll;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
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
}

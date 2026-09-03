package nl.enjarai.doabarrelroll.mixin.client.roll;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.player.LocalPlayer;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.RollMouse;
import nl.enjarai.doabarrelroll.config.ModConfig;
import org.joml.Vector2d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin implements RollMouse {
    @Shadow @Final private Minecraft minecraft;

    @Unique
    private final Vector2d mouseTurnVec = new Vector2d();

    /**
     * Momentum has to keep turning the camera on frames where the mouse did not
     * move, so this runs every frame rather than only when there is movement to
     * hand to the vanilla path below.
     */
    @Inject(
            method = "handleAccumulatedMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MouseHandler;isMouseGrabbed()Z",
                    ordinal = 0
            )
    )
    private void doABarrelRoll$maintainMouseMomentum(CallbackInfo ci, @Local(ordinal = 1) double e) {
        if (minecraft.player != null && !minecraft.isPaused()) {
            doABarrelRoll$updateMouse(minecraft.player, 0, 0, e);
        }
    }

    @WrapWithCondition(
            method = "turnPlayer",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;turn(DD)V"
            )
    )
    private boolean doABarrelRoll$changeLookDirection(LocalPlayer player, double cursorDeltaX, double cursorDeltaY, @Local(argsOnly = true) double timeDelta) {
        return !doABarrelRoll$updateMouse(player, cursorDeltaX, cursorDeltaY, timeDelta);
    }

    @Override
    public boolean doABarrelRoll$updateMouse(LocalPlayer player, double cursorDeltaX, double cursorDeltaY, double mouseDelta) {
        var rollPlayer = (RollEntity) player;

        if (rollPlayer.doABarrelRoll$isRolling()) {

            if (ModConfig.INSTANCE.getMomentumBasedMouse()) {

                // add the mouse movement to the current vector and normalize if needed
                mouseTurnVec.add(new Vector2d(cursorDeltaX, cursorDeltaY).mul(1f / 300));
                if (mouseTurnVec.lengthSquared() > 1.0) {
                    mouseTurnVec.normalize();
                }
                var readyTurnVec = new Vector2d(mouseTurnVec);

                // check if the vector is within the deadzone
                double deadzone = ModConfig.INSTANCE.getMomentumMouseDeadzone();
                if (readyTurnVec.lengthSquared() < deadzone * deadzone) readyTurnVec.zero();

                // enlarge the vector and apply it to the camera
                readyTurnVec.mul(1200 * (float) mouseDelta);
                rollPlayer.doABarrelRoll$changeElytraLook(readyTurnVec.y, readyTurnVec.x, 0, ModConfig.INSTANCE.getDesktopSensitivity(), mouseDelta);

            } else {

                // if we are not using a momentum based mouse, we can reset it and apply the values directly
                mouseTurnVec.zero();
                rollPlayer.doABarrelRoll$changeElytraLook(cursorDeltaY, cursorDeltaX, 0, ModConfig.INSTANCE.getDesktopSensitivity(), mouseDelta);
            }

            return true;
        }

        mouseTurnVec.zero();
        return false;
    }

    @Override
    public Vector2d doABarrelRoll$getMouseTurnVec() {
        return mouseTurnVec;
    }
}

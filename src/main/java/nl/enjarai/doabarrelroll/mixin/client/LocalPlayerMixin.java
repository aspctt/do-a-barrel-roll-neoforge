package nl.enjarai.doabarrelroll.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import nl.enjarai.doabarrelroll.util.MixinHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow public Input input;

    public LocalPlayerMixin(ClientLevel world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(
            method = "aiStep",
            at = @At("RETURN")
    )
    public void doABarrelRoll$resetJump(CallbackInfo ci) {
        if (onGround()) {
            MixinHooks.secondJump = false;
            MixinHooks.thirdJump = false;
        }

        MixinHooks.wasJumping = input.jumping;
    }
}

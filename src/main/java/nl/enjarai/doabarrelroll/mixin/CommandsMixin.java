package nl.enjarai.doabarrelroll.mixin;

import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import nl.enjarai.doabarrelroll.net.ServerNetworking;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public abstract class CommandsMixin {
    @Inject(
            method = "sendCommands",
            at = @At(value = "RETURN")
    )
    private void doABarrelRoll$doHandshake(ServerPlayer player, CallbackInfo ci) {
        // We do the handshake here since, aside from on join, this method will most likely also trigger
        // in any situation where the player's permissions change
        ServerNetworking.sendHandshake(player);
    }
}

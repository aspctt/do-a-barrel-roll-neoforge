package nl.enjarai.doabarrelroll.mixin.client.key;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Stops the controls screen marking a flight binding as conflicting with a
 * vanilla binding on the same key.
 *
 * <p>NeoForge already models this with key conflict contexts, but
 * {@code KeyMapping.same} only consults them to decide whether to run its own
 * modifier checks: when the two contexts do not conflict it falls through to
 * vanilla's plain key comparison and answers true anyway. Two bindings that can
 * never be active at once are not in conflict, so the answer is corrected here,
 * for every mod's contexts and not just this one's.
 */
@Mixin(KeyBindsList.KeyEntry.class)
public abstract class KeyBindsListEntryMixin {
    @Shadow @Final private KeyMapping key;

    @ModifyExpressionValue(
            method = "refreshEntry",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;same(Lnet/minecraft/client/KeyMapping;)Z"
            )
    )
    private boolean doABarrelRoll$ignoreCertainKeyBindingConflicts(boolean original, @Local KeyMapping otherBinding) {
        if (!original) return false;

        var first = key.getKeyConflictContext();
        var second = otherBinding.getKeyConflictContext();
        return first.conflicts(second) || second.conflicts(first);
    }
}

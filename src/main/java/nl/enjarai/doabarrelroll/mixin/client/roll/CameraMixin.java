package nl.enjarai.doabarrelroll.mixin.client.roll;

import net.minecraft.client.Camera;
import nl.enjarai.doabarrelroll.api.RollCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * Only exists to keep the {@link RollCamera} API working.
 *
 * <p>Rolling itself is applied through NeoForge's camera angle event, which
 * feeds the same field this reads, so there is nothing to inject here.
 */
@Mixin(Camera.class)
public abstract class CameraMixin implements RollCamera {
    @Shadow public abstract float getRoll();

    @Override
    public float doABarrelRoll$getRoll() {
        return getRoll();
    }
}

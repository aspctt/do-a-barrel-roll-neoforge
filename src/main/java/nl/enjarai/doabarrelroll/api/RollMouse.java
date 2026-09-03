package nl.enjarai.doabarrelroll.api;

import net.minecraft.client.player.LocalPlayer;
import org.joml.Vector2d;

public interface RollMouse {
    boolean doABarrelRoll$updateMouse(LocalPlayer player, double cursorDeltaX, double cursorDeltaY, double mouseDelta);

    Vector2d doABarrelRoll$getMouseTurnVec();
}

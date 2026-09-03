package nl.enjarai.doabarrelroll;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.RollMouse;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.impl.key.InputContextImpl;
import nl.enjarai.doabarrelroll.render.HorizonLineWidget;
import nl.enjarai.doabarrelroll.render.MomentumCrosshairWidget;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;
import org.joml.Vector2d;

public class EventCallbacksClient {
    public static void clientTick(Minecraft client) {
        InputContextImpl.getContexts().forEach(InputContextImpl::tick);

        if (!DoABarrelRollClient.isFallFlying()) {
            DoABarrelRollClient.clearValues();
        }

        ModKeybindings.clientTick(client);

        StarFoxUtil.clientTick(client);
    }

    public static void onRenderCrosshair(GuiGraphics context, DeltaTracker tickCounter, int scaledWidth, int scaledHeight) {
        if (!DoABarrelRollClient.isFallFlying()) return;
        var tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);

        var matrices = context.pose();
        var entity = Minecraft.getInstance().getCameraEntity();
        var rollEntity = ((RollEntity) entity);
        if (entity != null) {
            if (ModConfig.INSTANCE.getShowHorizon()) {
                HorizonLineWidget.render(matrices, scaledWidth, scaledHeight,
                        rollEntity.doABarrelRoll$getRoll(tickDelta), entity.getViewXRot(tickDelta));
            }

            if (ModConfig.INSTANCE.getMomentumBasedMouse() && ModConfig.INSTANCE.getShowMomentumWidget()) {
                var rollMouse = (RollMouse) Minecraft.getInstance().mouseHandler;

                MomentumCrosshairWidget.render(matrices, scaledWidth, scaledHeight, new Vector2d(rollMouse.doABarrelRoll$getMouseTurnVec()));
            }
        }
    }
}

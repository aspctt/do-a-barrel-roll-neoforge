package nl.enjarai.doabarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;

import java.util.Locale;

/**
 * Client game bus subscribers.
 *
 * <p>These stand in for what upstream does with mixins into Camera, InGameHud
 * and DebugHud. NeoForge fires events at all three points, and an event survives
 * the next Minecraft version in a way an injection point aimed at a particular
 * call in a particular method does not.
 */
@EventBusSubscriber(modid = DoABarrelRoll.MODID, value = Dist.CLIENT)
public class ModEventsClient {
    /**
     * How much of the leftover roll is shed per tick once rolling stops, so the
     * camera eases back to level instead of snapping.
     */
    private static final float ROLL_BACK_DECAY = 0.5f;

    private static float rollBack;
    private static float lastRollBack;

    /** Whether beforeGuiLayer pushed a pose that still needs popping. */
    private static boolean crosshairPosePushed;

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Post event) {
        var client = Minecraft.getInstance();

        var cameraEntity = client.getCameraEntity();
        if (cameraEntity == null || !((RollEntity) cameraEntity).doABarrelRoll$isRolling()) {
            lastRollBack = rollBack;
            rollBack -= rollBack * ROLL_BACK_DECAY;
        }

        EventCallbacksClient.clientTick(client);
    }

    @SubscribeEvent
    public static void loggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientNetworking.HANDSHAKE_CLIENT.reset();
    }

    /**
     * Rolls the camera.
     *
     * <p>NeoForge feeds the roll set here into Camera.setRotation and negates it
     * itself for the reversed third-person view, so unlike upstream there is
     * nothing to undo for that case here.
     */
    @SubscribeEvent
    public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        var entity = event.getCamera().getEntity();
        if (!(entity instanceof RollEntity rollEntity)) return;

        float partialTick = (float) event.getPartialTick();

        if (rollEntity.doABarrelRoll$isRolling()) {
            float roll = rollEntity.doABarrelRoll$getRoll(partialTick);
            // Held so the roll can be eased away rather than dropped the instant
            // flight ends.
            rollBack = roll;
            lastRollBack = roll;
            event.setRoll(event.getRoll() + roll);
        } else {
            event.setRoll(event.getRoll() + Mth.lerp(partialTick, lastRollBack, rollBack));
        }
    }

    /**
     * Wraps the crosshair layer so the horizon and momentum widgets draw with it,
     * and so the momentum widget can move the crosshair itself by translating the
     * matrix the layer is about to use.
     *
     * <p>Runs last, and skips an already cancelled layer, because the matching pop
     * lives in the Post event and NeoForge does not fire that one for a layer whose
     * Pre was cancelled. A mod that hides the crosshair must not leave a transform
     * pushed over the rest of the HUD.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void beforeGuiLayer(RenderGuiLayerEvent.Pre event) {
        // Recovery for the remaining case: a listener at this same priority
        // cancels after we have already pushed. The next layer undoes it.
        if (crosshairPosePushed && !event.getName().equals(VanillaGuiLayers.CROSSHAIR)) {
            event.getGuiGraphics().pose().popPose();
            crosshairPosePushed = false;
        }

        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR) || event.isCanceled()) return;

        var context = event.getGuiGraphics();
        context.pose().pushPose();
        crosshairPosePushed = true;
        EventCallbacksClient.onRenderCrosshair(
                context, event.getPartialTick(), context.guiWidth(), context.guiHeight());
    }

    @SubscribeEvent
    public static void afterGuiLayer(RenderGuiLayerEvent.Post event) {
        if (!event.getName().equals(VanillaGuiLayers.CROSSHAIR) || !crosshairPosePushed) return;

        event.getGuiGraphics().pose().popPose();
        crosshairPosePushed = false;
    }

    @SubscribeEvent
    public static void renderPeppy(RenderGuiEvent.Pre event) {
        var context = event.getGuiGraphics();
        StarFoxUtil.renderPeppy(
                context,
                event.getPartialTick().getGameTimeDeltaPartialTick(false),
                context.guiWidth(),
                context.guiHeight());
    }

    /**
     * Adds roll to the debug screen's facing line, inside the same bracket as yaw
     * and pitch: "Facing: south (Towards positive Z) (12.3 / -4.5 / 90.0)".
     */
    @SubscribeEvent
    public static void debugText(CustomizeGuiOverlayEvent.DebugText event) {
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) return;

        var lines = event.getLeft();
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line == null || !line.startsWith("Facing: ") || !line.endsWith(")")) continue;

            float roll = ((RollEntity) cameraEntity).doABarrelRoll$getRoll();
            lines.set(i, line.substring(0, line.length() - 1) + String.format(Locale.ROOT, " / %.1f)", roll));
            return;
        }
    }
}

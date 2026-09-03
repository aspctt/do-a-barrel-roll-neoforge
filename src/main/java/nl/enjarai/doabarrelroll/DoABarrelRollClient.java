package nl.enjarai.doabarrelroll;

import net.minecraft.client.Minecraft;
import net.minecraft.util.SmoothDouble;
import nl.enjarai.doabarrelroll.api.event.ClientEvents;
import nl.enjarai.doabarrelroll.api.event.RollEvents;
import nl.enjarai.doabarrelroll.api.event.RollGroup;
import nl.enjarai.doabarrelroll.config.ActivationBehaviour;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.flight.RotationModifiers;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import nl.enjarai.doabarrelroll.util.MixinHooks;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;

public class DoABarrelRollClient {
    public static final SmoothDouble PITCH_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble YAW_SMOOTHER = new SmoothDouble();
    public static final SmoothDouble ROLL_SMOOTHER = new SmoothDouble();
    public static final RollGroup FALL_FLYING_GROUP = RollGroup.of(DoABarrelRoll.id("fall_flying"));

    public static void init() {
        FALL_FLYING_GROUP.trueIf(DoABarrelRollClient::isFallFlying);

        // Keyboard modifiers
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                .useModifier(RotationModifiers.buttonControls(1800)),
                2000, FALL_FLYING_GROUP);

        // Mouse modifiers, including swapping axes
        RollEvents.EARLY_CAMERA_MODIFIERS.register(context -> context
                .useModifier(ModConfig.INSTANCE::configureRotation),
                1000, FALL_FLYING_GROUP);

        // Generic movement modifiers, banking and such
        RollEvents.LATE_CAMERA_MODIFIERS.register(context -> context
                .useModifier(RotationModifiers::applyControlSurfaceEfficacy, ModConfig.INSTANCE::getSimulateControlSurfaceEfficacy)
                .useModifier(RotationModifiers.smoothing(
                        PITCH_SMOOTHER, YAW_SMOOTHER, ROLL_SMOOTHER,
                        ModConfig.INSTANCE.getSmoothing()
                ))
                .useModifier(RotationModifiers::banking, ModConfig.INSTANCE::getEnableBanking)
                .useModifier(RotationModifiers::reorient, ModConfig.INSTANCE::getAutomaticRighting),
                1000, FALL_FLYING_GROUP);

        ClientEvents.SERVER_CONFIG_UPDATE.register(ModConfig.INSTANCE::notifyPlayerOfServerConfig);

        ModConfig.touch();

        // Init barrel rollery.
        StarFoxUtil.register();
    }

    public static void clearValues() {
        PITCH_SMOOTHER.reset();
        YAW_SMOOTHER.reset();
        ROLL_SMOOTHER.reset();
    }

    public static boolean isFallFlying() {
        if (!ClientNetworking.HANDSHAKE_CLIENT.getConfig().map(LimitedModConfigServer::forceEnabled).orElse(false)) {
            var hybrid = ModConfig.INSTANCE.getActivationBehaviour() == ActivationBehaviour.HYBRID ||
                    ModConfig.INSTANCE.getActivationBehaviour() == ActivationBehaviour.HYBRID_TOGGLE;
            if (hybrid && !MixinHooks.thirdJump) {
                return false;
            }

            if (!ModConfig.INSTANCE.getModEnabled()) {
                return false;
            }
        }

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        if (ModConfig.INSTANCE.getDisableWhenSubmerged() && player.isUnderWater()) {
            return false;
        }
        return player.isFallFlying();
    }

    public static boolean isConnectedToRealms() {
        return false; // We are not connected to realms.
    }
}

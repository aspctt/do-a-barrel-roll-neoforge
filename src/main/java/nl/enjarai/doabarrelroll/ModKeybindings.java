package nl.enjarai.doabarrelroll;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import nl.enjarai.doabarrelroll.api.key.InputContext;
import nl.enjarai.doabarrelroll.config.LimitedModConfigServer;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.config.ModConfigScreen;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import nl.enjarai.doabarrelroll.util.ScreenUtil;

import java.util.List;

public class ModKeybindings {
    // 1.21.11 turned the category from a bare translation key into a registered id, and derives the
    // label from it as key.category.<namespace>.<path>. Both sets of keys are in the lang files.
    //? if <1.21.11 {
    private static final String CATEGORY = "category.do_a_barrel_roll.do_a_barrel_roll";
    private static final String MOVEMENT_CATEGORY = "category.do_a_barrel_roll.do_a_barrel_roll.movement";
    //?} else {
    /*public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(DoABarrelRoll.id("do_a_barrel_roll"));
    public static final KeyMapping.Category MOVEMENT_CATEGORY = new KeyMapping.Category(DoABarrelRoll.id("movement"));
    *///?}

    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
            "key.do_a_barrel_roll.toggle_enabled",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_I,
            CATEGORY
    );
    public static final KeyMapping TOGGLE_THRUST = new KeyMapping(
            "key.do_a_barrel_roll.toggle_thrust",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );
    public static final KeyMapping OPEN_CONFIG = new KeyMapping(
            "key.do_a_barrel_roll.open_config",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            InputConstants.UNKNOWN.getValue(),
            CATEGORY
    );

    // The movement bindings all go into CONTEXT below, which is what lets yaw sit
    // on the strafe keys without conflicting with them outside of flight.
    public static final KeyMapping PITCH_UP = movementKey("pitch_up", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping PITCH_DOWN = movementKey("pitch_down", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping YAW_LEFT = movementKey("yaw_left", InputConstants.KEY_A);
    public static final KeyMapping YAW_RIGHT = movementKey("yaw_right", InputConstants.KEY_D);
    public static final KeyMapping ROLL_LEFT = movementKey("roll_left", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping ROLL_RIGHT = movementKey("roll_right", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping THRUST_FORWARD = movementKey("thrust_forward", InputConstants.KEY_W);
    public static final KeyMapping THRUST_BACKWARD = movementKey("thrust_backward", InputConstants.UNKNOWN.getValue());

    public static final List<KeyMapping> ALL = List.of(
            TOGGLE_ENABLED,
            TOGGLE_THRUST,
            OPEN_CONFIG,
            PITCH_UP,
            PITCH_DOWN,
            YAW_LEFT,
            YAW_RIGHT,
            ROLL_LEFT,
            ROLL_RIGHT,
            THRUST_FORWARD,
            THRUST_BACKWARD
    );

    public static final InputContext CONTEXT = InputContext.of(
            DoABarrelRoll.id("fall_flying"),
            DoABarrelRollClient.FALL_FLYING_GROUP
    );

    static {
        CONTEXT.addKeyBinding(PITCH_UP);
        CONTEXT.addKeyBinding(PITCH_DOWN);
        CONTEXT.addKeyBinding(YAW_LEFT);
        CONTEXT.addKeyBinding(YAW_RIGHT);
        CONTEXT.addKeyBinding(ROLL_LEFT);
        CONTEXT.addKeyBinding(ROLL_RIGHT);
        CONTEXT.addKeyBinding(THRUST_FORWARD);
        CONTEXT.addKeyBinding(THRUST_BACKWARD);
    }

    /**
     * Puts a message on the action bar.
     *
     * <p>26.1 split the two things displayClientMessage did into a method each, so the boolean that
     * used to pick between chat and the action bar is gone.
     */
    private static void overlayMessage(Minecraft client, Component message) {
        if (client.player == null) return;

        //? if <26.1 {
        client.player.displayClientMessage(message, true);
        //?} else
        /*client.player.sendOverlayMessage(message);*/
    }

    private static KeyMapping movementKey(String name, int keyCode) {
        return new KeyMapping(
                "key.do_a_barrel_roll." + name,
                KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM,
                keyCode,
                MOVEMENT_CATEGORY
        );
    }

    public static void clientTick(Minecraft client) {
        while (TOGGLE_ENABLED.consumeClick()) {
            if (!ClientNetworking.HANDSHAKE_CLIENT.getConfig().map(LimitedModConfigServer::forceEnabled).orElse(false)) {
                ModConfig.INSTANCE.setModEnabled(!ModConfig.INSTANCE.getModEnabled());
                ModConfig.INSTANCE.save();

                overlayMessage(client, Component.translatable(
                        "key.do_a_barrel_roll." +
                                (ModConfig.INSTANCE.getModEnabled() ? "toggle_enabled.enable" : "toggle_enabled.disable")
                ));
            } else {
                overlayMessage(client, Component.translatable("key.do_a_barrel_roll.toggle_enabled.disallowed"));
            }
        }
        while (TOGGLE_THRUST.consumeClick()) {
            if (ClientNetworking.HANDSHAKE_CLIENT.getConfig().map(LimitedModConfigServer::allowThrusting).orElse(false)) {
                ModConfig.INSTANCE.setEnableThrust(!ModConfig.INSTANCE.getEnableThrust());
                ModConfig.INSTANCE.save();

                overlayMessage(client, Component.translatable(
                        "key.do_a_barrel_roll." +
                                (ModConfig.INSTANCE.getEnableThrust() ? "toggle_thrust.enable" : "toggle_thrust.disable")
                ));
            } else {
                overlayMessage(client, Component.translatable("key.do_a_barrel_roll.toggle_thrust.disallowed"));
            }
        }
        while (OPEN_CONFIG.consumeClick()) {
            ScreenUtil.setScreen(client, ModConfigScreen.create(ScreenUtil.currentScreen(client)));
        }
    }
}

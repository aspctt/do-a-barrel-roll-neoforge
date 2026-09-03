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
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ModKeybindings {
    private static final String CATEGORY = "category.do_a_barrel_roll.do_a_barrel_roll";
    private static final String MOVEMENT_CATEGORY = "category.do_a_barrel_roll.do_a_barrel_roll.movement";

    public static final KeyMapping TOGGLE_ENABLED = new KeyMapping(
            "key.do_a_barrel_roll.toggle_enabled",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
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
    public static final KeyMapping YAW_LEFT = movementKey("yaw_left", GLFW.GLFW_KEY_A);
    public static final KeyMapping YAW_RIGHT = movementKey("yaw_right", GLFW.GLFW_KEY_D);
    public static final KeyMapping ROLL_LEFT = movementKey("roll_left", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping ROLL_RIGHT = movementKey("roll_right", InputConstants.UNKNOWN.getValue());
    public static final KeyMapping THRUST_FORWARD = movementKey("thrust_forward", GLFW.GLFW_KEY_W);
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

                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.translatable(
                                    "key.do_a_barrel_roll." +
                                            (ModConfig.INSTANCE.getModEnabled() ? "toggle_enabled.enable" : "toggle_enabled.disable")
                            ),
                            true
                    );
                }
            } else {
                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.translatable("key.do_a_barrel_roll.toggle_enabled.disallowed"),
                            true
                    );
                }
            }
        }
        while (TOGGLE_THRUST.consumeClick()) {
            if (ClientNetworking.HANDSHAKE_CLIENT.getConfig().map(LimitedModConfigServer::allowThrusting).orElse(false)) {
                ModConfig.INSTANCE.setEnableThrust(!ModConfig.INSTANCE.getEnableThrust());
                ModConfig.INSTANCE.save();

                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.translatable(
                                    "key.do_a_barrel_roll." +
                                            (ModConfig.INSTANCE.getEnableThrust() ? "toggle_thrust.enable" : "toggle_thrust.disable")
                            ),
                            true
                    );
                }
            } else {
                if (client.player != null) {
                    client.player.displayClientMessage(
                            Component.translatable("key.do_a_barrel_roll.toggle_thrust.disallowed"),
                            true
                    );
                }
            }
        }
        while (OPEN_CONFIG.consumeClick()) {
            client.setScreen(ModConfigScreen.create(client.screen));
        }
    }
}

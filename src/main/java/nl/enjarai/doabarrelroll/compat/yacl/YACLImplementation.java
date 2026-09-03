package nl.enjarai.doabarrelroll.compat.yacl;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import nl.enjarai.doabarrelroll.DoABarrelRollClient;
import nl.enjarai.doabarrelroll.ModKeybindings;
import nl.enjarai.doabarrelroll.api.event.ClientEvents;
import nl.enjarai.doabarrelroll.compat.Compat;
import nl.enjarai.doabarrelroll.config.*;
import nl.enjarai.doabarrelroll.math.ExpressionParser;
import nl.enjarai.doabarrelroll.net.ClientNetworking;
import nl.enjarai.doabarrelroll.net.ServerNetworking;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;

public class YACLImplementation {
    public static Screen generateConfigScreen(Screen parent) {
        var inWorld = Minecraft.getInstance().level != null;
        LocalPlayer player;
        var onRealms = DoABarrelRollClient.isConnectedToRealms() &&
                (player = Minecraft.getInstance().player) != null && player.hasPermissions(2);
        var serverConfig = ClientNetworking.HANDSHAKE_CLIENT.getConfig();

        var thrustingAllowed = new Dependable(serverConfig.map(LimitedModConfigServer::allowThrusting).orElse(!inWorld || onRealms));
        var allowDisabled = new Dependable(!serverConfig.map(LimitedModConfigServer::forceEnabled).orElse(false));

        var builder = YetAnotherConfigLib.createBuilder()
                .title(getText("title"))
                .category(ConfigCategory.createBuilder()
                        .name(getText("general"))
                        .option(allowDisabled.add(getBooleanOption("general", "mod_enabled", false, false)
                                .description(OptionDescription.createBuilder()
                                        .text(Component.translatable("config.do_a_barrel_roll.general.mod_enabled.description",
                                                ModKeybindings.TOGGLE_ENABLED.getTranslatedKeyMessage()))
                                        .build())
                                .binding(true, () -> ModConfig.INSTANCE.getModEnabled(), value -> ModConfig.INSTANCE.setModEnabled(value))))
                        .group(OptionGroup.createBuilder()
                                .name(getText("controls"))
                                .option(getBooleanOption("controls", "switch_roll_and_yaw", true, false)
                                        .binding(false, () -> ModConfig.INSTANCE.getSwitchRollAndYaw(), value -> ModConfig.INSTANCE.setSwitchRollAndYaw(value))
                                        .build())
                                .option(getBooleanOption("controls", "invert_pitch", true, false)
                                        .binding(false, () -> ModConfig.INSTANCE.getInvertPitch(), value -> ModConfig.INSTANCE.setInvertPitch(value))
                                        .build())
                                .option(getBooleanOption("controls", "momentum_based_mouse", true, false)
                                        .binding(false, () -> ModConfig.INSTANCE.getMomentumBasedMouse(), value -> ModConfig.INSTANCE.setMomentumBasedMouse(value))
                                        .build())
                                .option(getOption(Double.class, "controls", "momentum_mouse_deadzone", true, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 1.0, 0.01))
                                        .binding(0.2, () -> ModConfig.INSTANCE.getMomentumMouseDeadzone(), value -> ModConfig.INSTANCE.setMomentumMouseDeadzone(value))
                                        .build())
                                .option(getOption(ActivationBehaviour.class, "controls", "activation_behaviour", false, false)
                                        .description(behaviour -> OptionDescription.createBuilder()
                                                .text(getText("controls", "activation_behaviour.description")
                                                        .append(getText("controls", "activation_behaviour.description." + behaviour.name().toLowerCase())))
                                                .build())
                                        .controller(option1 -> EnumControllerBuilder.create(option1)
                                                .enumClass(ActivationBehaviour.class))
                                        .binding(ActivationBehaviour.VANILLA, () -> ModConfig.INSTANCE.getActivationBehaviour(), value -> ModConfig.INSTANCE.setActivationBehaviour(value))
                                        .build())
                                .option(getBooleanOption("controls", "disable_when_submerged", true, false)
                                        .binding(true, () -> ModConfig.INSTANCE.getDisableWhenSubmerged(), value -> ModConfig.INSTANCE.setDisableWhenSubmerged(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("hud"))
                                .option(getBooleanOption("hud", "show_horizon", true, true)
                                        .binding(false, () -> ModConfig.INSTANCE.getShowHorizon(), value -> ModConfig.INSTANCE.setShowHorizon(value))
                                        .build())
                                .option(getBooleanOption("controls", "show_momentum_widget", true, true)
                                        .binding(true, () -> ModConfig.INSTANCE.getShowMomentumWidget(), value -> ModConfig.INSTANCE.setShowMomentumWidget(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("banking"))
                                .option(getBooleanOption("banking", "enable_banking", true, false)
                                        .binding(true, () -> ModConfig.INSTANCE.getEnableBanking(), value -> ModConfig.INSTANCE.setEnableBanking(value))
                                        .build())
                                .option(getOption(Double.class, "banking", "banking_strength", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 100.0, 1.0))
                                        .binding(20.0, () -> ModConfig.INSTANCE.getBankingStrength(), value -> ModConfig.INSTANCE.setBankingStrength(value))
                                        .build())
                                .option(getBooleanOption("banking", "simulate_control_surface_efficacy", true, false)
                                        .binding(false, () -> ModConfig.INSTANCE.getSimulateControlSurfaceEfficacy(), value -> ModConfig.INSTANCE.setSimulateControlSurfaceEfficacy(value))
                                        .build())
                                .option(getBooleanOption("banking", "automatic_righting", true, false)
                                        .binding(false, () -> ModConfig.INSTANCE.getAutomaticRighting(), value -> ModConfig.INSTANCE.setAutomaticRighting(value))
                                        .build())
                                .option(getOption(Double.class, "banking", "righting_strength", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 100.0, 1.0))
                                        .binding(50.0, () -> ModConfig.INSTANCE.getRightingStrength(), value -> ModConfig.INSTANCE.setRightingStrength(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("thrust"))
                                .collapsed(true)
                                .option(thrustingAllowed.add(getBooleanOption("thrust", "enable_thrust", false, false)
                                        .description(OptionDescription.of(Component.translatable(
                                                "config.do_a_barrel_roll.thrust.enable_thrust.description",
                                                ModKeybindings.THRUST_FORWARD.getTranslatedKeyMessage())))
                                        .binding(false, () -> ModConfig.INSTANCE.getEnableThrustClient(), value -> ModConfig.INSTANCE.setEnableThrust(value))))
                                .option(thrustingAllowed.add(getOption(Double.class, "thrust", "max_thrust", true, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(2.0, () -> ModConfig.INSTANCE.getMaxThrust(), value -> ModConfig.INSTANCE.setMaxThrust(value))))
                                .option(thrustingAllowed.add(getOption(Double.class, "thrust", "thrust_acceleration", true, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 1.0, 0.1))
                                        .binding(0.1, () -> ModConfig.INSTANCE.getThrustAcceleration(), value -> ModConfig.INSTANCE.setThrustAcceleration(value))))
                                .option(thrustingAllowed.add(getBooleanOption("thrust", "thrust_particles", false, false)
                                        .binding(true, () -> ModConfig.INSTANCE.getThrustParticles(), value -> ModConfig.INSTANCE.setThrustParticles(value))))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("misc"))
                                .option(getBooleanOption("misc", "enable_easter_eggs", false, false)
                                        .binding(ModConfig.DEFAULT.getEnableEasterEggs(), ModConfig.INSTANCE::getEnableEasterEggs, ModConfig.INSTANCE::setEnableEasterEggs)
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(getText("sensitivity"))
                        .group(OptionGroup.createBuilder()
                                .name(getText("smoothing"))
                                .option(getOption(Double.class, "smoothing", "smoothing_pitch", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 5.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getSmoothingPitch(), value -> ModConfig.INSTANCE.setSmoothingPitch(value))
                                        .build())
                                .option(getOption(Double.class, "smoothing", "smoothing_yaw", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 5.0, 0.1))
                                        .binding(2.5, () -> ModConfig.INSTANCE.getSmoothingYaw(), value -> ModConfig.INSTANCE.setSmoothingYaw(value))
                                        .build())
                                .option(getOption(Double.class, "smoothing", "smoothing_roll", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.0, 5.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getSmoothingRoll(), value -> ModConfig.INSTANCE.setSmoothingRoll(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("desktop"))
                                .option(getOption(Double.class, "desktop", "pitch", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getDesktopPitch(), value -> ModConfig.INSTANCE.setDesktopPitch(value))
                                        .build())
                                .option(getOption(Double.class, "desktop", "yaw", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(0.4, () -> ModConfig.INSTANCE.getDesktopYaw(), value -> ModConfig.INSTANCE.setDesktopYaw(value))
                                        .build())
                                .option(getOption(Double.class, "desktop", "roll", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getDesktopRoll(), value -> ModConfig.INSTANCE.setDesktopRoll(value))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("controller"))
                                .collapsed(!(Compat.checkModLoaded("controlify") || Compat.checkModLoaded("midnightcontrols")))
                                .description(OptionDescription.createBuilder()
                                        .text(getText("controller.description"))
                                        .build())
                                .option(getOption(Double.class, "controller", "pitch", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getControllerPitch(), value -> ModConfig.INSTANCE.setControllerPitch(value))
                                        .build())
                                .option(getOption(Double.class, "controller", "yaw", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(0.4, () -> ModConfig.INSTANCE.getControllerYaw(), value -> ModConfig.INSTANCE.setControllerYaw(value))
                                        .build())
                                .option(getOption(Double.class, "controller", "roll", false, false)
                                        .controller(option -> getDoubleSlider(option, 0.1, 10.0, 0.1))
                                        .binding(1.0, () -> ModConfig.INSTANCE.getControllerRoll(), value -> ModConfig.INSTANCE.setControllerRoll(value))
                                        .build())
                                .build())
                        .build())
                .category(ConfigCategory.createBuilder()
                        .name(getText("advanced"))
                        .option(LabelOption.create(getText("advanced", "description")))
                        .group(OptionGroup.createBuilder()
                                .name(getText("banking_math"))
                                .option(getExpressionOption("banking_math", "banking_x_formula", true, false)
                                        .binding(ModConfig.DEFAULT.getBankingXFormula(), ModConfig.INSTANCE::getBankingXFormula, ModConfig.INSTANCE::setBankingXFormula)
                                        .build())
                                .option(getExpressionOption("banking_math", "banking_y_formula", true, false)
                                        .binding(ModConfig.DEFAULT.getBankingYFormula(), ModConfig.INSTANCE::getBankingYFormula, ModConfig.INSTANCE::setBankingYFormula)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("control_surface_efficacy"))
                                .option(getExpressionOption("control_surface_efficacy", "elevator", true, false)
                                        .binding(ModConfig.DEFAULT.getElevatorEfficacyFormula(), ModConfig.INSTANCE::getElevatorEfficacyFormula, ModConfig.INSTANCE::setElevatorEfficacyFormula)
                                        .build())
                                .option(getExpressionOption("control_surface_efficacy", "aileron", true, false)
                                        .binding(ModConfig.DEFAULT.getAileronEfficacyFormula(), ModConfig.INSTANCE::getAileronEfficacyFormula, ModConfig.INSTANCE::setAileronEfficacyFormula)
                                        .build())
                                .option(getExpressionOption("control_surface_efficacy", "rudder", true, false)
                                        .binding(ModConfig.DEFAULT.getRudderEfficacyFormula(), ModConfig.INSTANCE::getRudderEfficacyFormula, ModConfig.INSTANCE::setRudderEfficacyFormula)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("documentation"))
                                .option(LabelOption.create(getText("documentation", "description")))
                                .option(ButtonOption.createBuilder()
                                        .name(getText("documentation", "get_help"))
                                        .text(getText("documentation", "get_help.text"))
                                        .action((screen, btn) -> {
                                            var client = Minecraft.getInstance();
                                            client.setScreen(new ConfirmScreen((result) -> {
                                                if (result) {
                                                    Util.getPlatform().openUri(URI.create("https://discord.gg/WcYsDDQtyR"));
                                                }
                                                client.setScreen(screen);
                                            }, getText("documentation", "get_help"), getText("documentation", "get_help.confirm"), CommonComponents.GUI_YES, CommonComponents.GUI_NO));
                                        })
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("variables_documentation"))
                                .collapsed(true)
                                .option(LabelOption.create(getText("variables_documentation", "description")))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("functions_documentation"))
                                .collapsed(true)
                                .option(LabelOption.create(getText("functions_documentation", "description")))
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(getText("constants_documentation"))
                                .collapsed(true)
                                .option(LabelOption.create(getText("constants_documentation", "description")))
                                .build())
                        .build());

        // If we're in a world, use the synced config, otherwise, grab our local one.
        var fullServerConfig = inWorld
                ? ClientNetworking.HANDSHAKE_CLIENT.getFullConfig()
                : Optional.of(ServerNetworking.CONFIG_HOLDER.instance);
        MutableConfigServer mut;
        if (fullServerConfig.isPresent()) {
            mut = new MutableConfigServer(fullServerConfig.get());
            builder.category(ConfigCategory.createBuilder()
                    .name(getText("server"))
                    .option(LabelOption.create(getText("server", "description")))
                    .option(getBooleanOption("server", "force_enabled", true, false)
                            .binding(ModConfigServer.DEFAULT.forceEnabled(), () -> mut.forceEnabled, value -> mut.forceEnabled = value)
                            .build())
                    .option(getBooleanOption("server", "force_installed", true, false)
                            .binding(ModConfigServer.DEFAULT.forceInstalled(), () -> mut.forceInstalled, value -> mut.forceInstalled = value)
                            .build())
                    .option(getOption(Integer.class, "server", "installed_timeout", true, false)
                            .controller(option -> IntegerSliderControllerBuilder.create(option)
                                    .range(0, 100)
                                    .step(1))
                            .binding(ModConfigServer.DEFAULT.installedTimeout(), () -> mut.installedTimeout, value -> mut.installedTimeout = value)
                            .build())
                    .group(OptionGroup.createBuilder()
                            .name(getText("gameplay"))
                            .option(getBooleanOption("gameplay", "allow_thrusting", true, false)
                                    .binding(ModConfigServer.DEFAULT.allowThrusting(), () -> mut.allowThrusting, value -> mut.allowThrusting = value)
                                    .build())
                            .option(getOption(KineticDamage.class, "gameplay", "kinetic_damage", false, false)
                                    .description(behaviour -> OptionDescription.createBuilder()
                                            .text(getText("gameplay", "kinetic_damage.description")
                                                    .append(getText("gameplay", "kinetic_damage.description." + behaviour.name().toLowerCase())))
                                            .build())
                                    .controller(option -> EnumControllerBuilder.create(option)
                                            .enumClass(KineticDamage.class)
                                            .valueFormatter(kd -> getText("gameplay", "kinetic_damage." + kd.name().toLowerCase(Locale.ROOT))))
                                    .binding(ModConfigServer.DEFAULT.kineticDamage(), () -> mut.kineticDamage, value -> mut.kineticDamage = value)
                                    .build())
                            .build())
                    .build());
        } else mut = null;

        Consumer<LimitedModConfigServer> configListener = config -> {
            // Update options that have dependent availability.
            thrustingAllowed.set(config.allowThrusting());
            allowDisabled.set(!config.forceEnabled());
        };

        return builder
                .save(() -> {
                    ModConfig.INSTANCE.save();

                    if (mut != null) {
                        if (Minecraft.getInstance().level == null) {
                            ServerNetworking.CONFIG_HOLDER.instance = mut.toImmutable();
                        } else {
                            var original = ClientNetworking.HANDSHAKE_CLIENT.getConfig();
                            if (original.isPresent()){
                                var imut = mut.toImmutable();
                                if (!imut.equals(original.get())) {
                                    ClientNetworking.sendConfigUpdatePacket(imut);
                                    configListener.accept(imut);
                                }
                            }
                        }
                    }
                })
                .screenInit(screen -> {
                    // Add a listener for this screen to update elements when the server config changes.
                    ClientEvents.ServerConfigUpdateEvent listener = configListener::accept;
                    ClientEvents.SERVER_CONFIG_UPDATE.register(listener);

                    // Unregister once this screen goes away. NeoForge has no
                    // per-screen removal callback, so this listens for any screen
                    // closing and removes itself along with the config listener
                    // when the one closing is ours.
                    NeoForge.EVENT_BUS.addListener(ScreenEvent.Closing.class, new Consumer<ScreenEvent.Closing>() {
                        @Override
                        public void accept(ScreenEvent.Closing event) {
                            if (event.getScreen() != screen) return;

                            ClientEvents.SERVER_CONFIG_UPDATE.unregister(listener);
                            NeoForge.EVENT_BUS.unregister(this);
                        }
                    });
                })
                .build()
                .generateScreen(parent);
    }

    private static <T> Option.Builder<T> getOption(Class<T> clazz, String category, String key, boolean description, boolean image) {
        Option.Builder<T> builder = Option.<T>createBuilder()
                .name(getText(category, key));
        var descBuilder = OptionDescription.createBuilder();
        if (description) {
            descBuilder.text(getText(category, key + ".description"));
        }
        if (image) {
            descBuilder.image(DoABarrelRoll.id("textures/gui/config/images/" + category + "/" + key + ".png"), 480, 275);
        }
        builder.description(descBuilder.build());
        return builder;
    }

    private static Option.Builder<Boolean> getBooleanOption(String category, String key, boolean description, boolean image) {
        return getOption(Boolean.class, category, key, description, image)
                .controller(TickBoxControllerBuilder::create);
    }

    private static Option.Builder<ExpressionParser> getExpressionOption(String category, String key, boolean description, boolean image) {
        return getOption(ExpressionParser.class, category, key, false, false)
                .description(parser -> {
                    var descBuilder = OptionDescription.createBuilder();
                    var error = Component.literal(parser.hasError() ? parser.getError().getMessage() : "")
                            .withStyle(ChatFormatting.RED);
                    if (description) {
                        descBuilder.text(getText(category, key + ".description").append("\n\n").append(error));
                    } else {
                        descBuilder.text(error);
                    }
                    if (image) {
                        descBuilder.image(DoABarrelRoll.id("textures/gui/config/images/" + category + "/" + key + ".png"), 480, 275);
                    }
                    return descBuilder.build();
                })
                .customController(ExpressionParserController::new);
    }

    private static DoubleSliderControllerBuilder getDoubleSlider(Option<Double> option, double min, double max, double step) {
        return DoubleSliderControllerBuilder.create(option)
                .range(min, max)
                .step(step);
    }

    private static MutableComponent getText(String category, String key) {
        return Component.translatable("config.do_a_barrel_roll." + category + "." + key);
    }

    private static MutableComponent getText(String key) {
        return Component.translatable("config.do_a_barrel_roll." + key);
    }

    private static class Dependable {
        private final boolean initial;
        private final List<Option<?>> options = new ArrayList<>();

        private Dependable(boolean initial) {
            this.initial = initial;
        }

        <T> Option<T> add(Option.Builder<T> builder) {
            var option = builder
                    .available(initial)
                    .build();
            options.add(option);
            return option;
        }

        void set(boolean value) {
            for (var option : options) {
                option.setAvailable(value);
            }
        }
    }
}

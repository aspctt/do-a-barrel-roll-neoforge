package nl.enjarai.doabarrelroll.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

/**
 * The screen accessors, in the one place that has to know where they live.
 *
 * <p>26.2 moved the current screen off Minecraft and onto its Gui, so the field and the setter both
 * changed shape there. Nothing else about opening a screen changed.
 */
public class ScreenUtil {
    public static void setScreen(Minecraft client, @Nullable Screen screen) {
        //? if <26.2 {
        client.setScreen(screen);
        //?} else
        /*client.gui.setScreen(screen);*/
    }

    public static @Nullable Screen currentScreen(Minecraft client) {
        //? if <26.2 {
        return client.screen;
        //?} else
        /*return client.gui.screen();*/
    }
}

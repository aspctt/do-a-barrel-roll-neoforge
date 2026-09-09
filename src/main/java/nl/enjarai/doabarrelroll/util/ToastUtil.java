package nl.enjarai.doabarrelroll.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;

public class ToastUtil {
    public static void toasty(String key) {
        //? if <1.21.11 {
        Minecraft.getInstance().getToasts().addToast(SystemToast.multiline(
                Minecraft.getInstance(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.do_a_barrel_roll"),
                Component.translatable("toast.do_a_barrel_roll." + key)
        ));
        //?} elif <26.2 {
        /*Minecraft.getInstance().getToastManager().addToast(SystemToast.multiline(
                Minecraft.getInstance(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.do_a_barrel_roll"),
                Component.translatable("toast.do_a_barrel_roll." + key)
        ));
        *///?} else {
        /*SystemToast.addOrUpdate(
                Minecraft.getInstance().gui.toastManager(),
                SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                Component.translatable("toast.do_a_barrel_roll"),
                Component.translatable("toast.do_a_barrel_roll." + key)
        );
        *///?}
    }
}

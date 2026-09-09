package nl.enjarai.doabarrelroll.config;

import net.minecraft.network.chat.Component;
//? if <1.21.11 {
import net.minecraft.util.OptionEnum;
//?}

// OptionEnum was dropped in 1.21.11; the options that used it now just carry their own caption. The
// three methods below stay either way, because this mod's config screen calls them directly.
//? if <1.21.11 {
public enum ActivationBehaviour implements OptionEnum {
//?} else
/*public enum ActivationBehaviour {*/
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    //? if <1.21.11
    @Override
    public int getId() {
        return this.ordinal();
    }

    //? if <1.21.11
    @Override
    public String getKey() {
        return "config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase();
    }

    //? if <1.21.11
    @Override
    public Component getCaption() {
        return Component.translatable(getKey());
    }
}

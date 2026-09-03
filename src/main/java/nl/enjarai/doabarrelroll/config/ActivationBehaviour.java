package nl.enjarai.doabarrelroll.config;

import net.minecraft.network.chat.Component;
import net.minecraft.util.OptionEnum;

public enum ActivationBehaviour implements OptionEnum {
    VANILLA,
    TRIPLE_JUMP,
    HYBRID,
    HYBRID_TOGGLE;

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public String getKey() {
        return "config.do_a_barrel_roll.controls.activation_behaviour." + this.name().toLowerCase();
    }

    @Override
    public Component getCaption() {
        return Component.translatable(getKey());
    }
}

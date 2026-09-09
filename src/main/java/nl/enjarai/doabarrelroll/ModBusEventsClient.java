package nl.enjarai.doabarrelroll;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
//? if >=1.21.11 {
/*import net.minecraft.client.gui.components.debug.DebugScreenEntryStatus;
import net.minecraft.client.gui.components.debug.DebugScreenProfile;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import nl.enjarai.doabarrelroll.render.RollDebugEntry;
*///?}

public class ModBusEventsClient {
    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        // A modded category has to be declared from 1.21.11 on, where it is an id rather than a
        // translation key. Earlier versions take the key straight off the binding.
        //? if >=1.21.11 {
        /*event.registerCategory(ModKeybindings.CATEGORY);
        event.registerCategory(ModKeybindings.MOVEMENT_CATEGORY);
        *///?}
        ModKeybindings.ALL.forEach(event::register);
    }

    // The roll readout on the debug screen. Up to 1.21.10 it is appended to the vanilla facing
    // line from the game bus instead, in ModEventsClient.
    //? if >=1.21.11 {
    /*@SubscribeEvent
    public static void registerDebugEntries(RegisterDebugEntriesEvent event) {
        var id = DoABarrelRoll.id("roll");
        event.register(id, new RollDebugEntry());
        event.includeInProfile(id, DebugScreenProfile.DEFAULT, DebugScreenEntryStatus.IN_OVERLAY);
    }
    *///?}
}

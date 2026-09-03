package nl.enjarai.doabarrelroll;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import nl.enjarai.doabarrelroll.config.ModConfigScreen;
import nl.enjarai.doabarrelroll.net.ModNetworking;

/**
 * Entry point. The mod is useful on both sides: a client gets the flight
 * controls, a server gets roll syncing between clients and the server-side
 * restrictions, and either half works on its own against a vanilla counterpart.
 */
@Mod(DoABarrelRoll.MODID)
public class DoABarrelRollNeoForge {
    public DoABarrelRollNeoForge(IEventBus modEventBus, ModContainer container) {
        DoABarrelRoll.init();

        // Registered here rather than through @EventBusSubscriber(bus = MOD),
        // which NeoForge has deprecated for removal.
        modEventBus.register(ModNetworking.class);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            DoABarrelRollClient.init();
            modEventBus.register(ModBusEventsClient.class);

            // Puts the config screen behind the "Config" button in the mod list,
            // which is where NeoForge players look for it. On Fabric this is Mod
            // Menu's job; NeoForge has it built in.
            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (modContainer, parent) -> ModConfigScreen.create(parent)
            );
        }
    }
}

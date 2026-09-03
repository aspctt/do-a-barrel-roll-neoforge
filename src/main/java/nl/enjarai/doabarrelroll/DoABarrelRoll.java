package nl.enjarai.doabarrelroll;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.resources.ResourceLocation;
import nl.enjarai.doabarrelroll.net.ServerNetworking;
import nl.enjarai.doabarrelroll.util.ModPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DoABarrelRoll {
    public static final String MODID = "do_a_barrel_roll";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static void init() {
        ServerNetworking.init();
    }

    /**
     * Resolves a permission for the player behind a connection, falling back to the
     * given operator level when no permission handler grants or denies it.
     */
    public static boolean checkPermission(ServerGamePacketListenerImpl handler, String permission, int operatorLevel) {
        return ModPermissions.resolve(handler.getPlayer(), permission, operatorLevel);
    }
}

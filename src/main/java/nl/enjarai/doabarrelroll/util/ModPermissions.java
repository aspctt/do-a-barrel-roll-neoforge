package nl.enjarai.doabarrelroll.util;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.permission.PermissionAPI;
import net.neoforged.neoforge.server.permission.events.PermissionGatherEvent;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContext;
import net.neoforged.neoforge.server.permission.nodes.PermissionDynamicContextKey;
import net.neoforged.neoforge.server.permission.nodes.PermissionNode;
import net.neoforged.neoforge.server.permission.nodes.PermissionTypes;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * The NeoForge counterpart to the Fabric permissions API upstream uses.
 *
 * <p>Both nodes fall back to an operator level, which differs per call site, so
 * it is passed as dynamic context rather than baked into the node. With no
 * permission handler installed NeoForge returns the node default, which is that
 * same operator check, so an unconfigured server behaves exactly as before.
 */
@EventBusSubscriber(modid = DoABarrelRoll.MODID)
public class ModPermissions {
    public static final PermissionDynamicContextKey<Integer> DEFAULT_PERMISSION_LEVEL_CONTEXT = new PermissionDynamicContextKey<>(
            Integer.class, "default_permission_level", Objects::toString);

    public static final PermissionNode<Boolean> IGNORE_CONFIG_NODE = new PermissionNode<>(
            DoABarrelRoll.MODID, "ignore_config", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);
    public static final PermissionNode<Boolean> CONFIGURE_NODE = new PermissionNode<>(
            DoABarrelRoll.MODID, "configure", PermissionTypes.BOOLEAN,
            ModPermissions::defaultResolve, DEFAULT_PERMISSION_LEVEL_CONTEXT);

    public static final List<PermissionNode<Boolean>> NODES = List.of(IGNORE_CONFIG_NODE, CONFIGURE_NODE);

    @SubscribeEvent
    public static void gatherPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(NODES.toArray(new PermissionNode[0]));
    }

    public static boolean resolve(ServerPlayer player, String permission, int defaultPermissionLevel) {
        for (var node : NODES) {
            if (node.getNodeName().equals(permission)) {
                return PermissionAPI.getPermission(
                        player, node, DEFAULT_PERMISSION_LEVEL_CONTEXT.createContext(defaultPermissionLevel));
            }
        }
        return player.hasPermissions(defaultPermissionLevel);
    }

    private static boolean defaultResolve(@Nullable ServerPlayer player, UUID playerUUID, PermissionDynamicContext<?>... context) {
        if (player != null) {
            for (var key : context) {
                if (key.getDynamic() == DEFAULT_PERMISSION_LEVEL_CONTEXT) {
                    return player.hasPermissions((int) key.getValue());
                }
            }
        }
        return false;
    }
}

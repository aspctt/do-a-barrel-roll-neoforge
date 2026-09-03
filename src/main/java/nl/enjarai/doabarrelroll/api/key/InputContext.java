package nl.enjarai.doabarrelroll.api.key;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import nl.enjarai.doabarrelroll.impl.key.InputContextImpl;

import java.util.List;
import java.util.function.Supplier;

/**
 * A named set of key bindings that are only live while some condition holds.
 *
 * <p>On NeoForge this is a {@link IKeyConflictContext}, so a binding added to a
 * context is reported as pressed only while the context is active, and the
 * controls screen does not flag it as conflicting with a binding that can never
 * be active at the same time. That is what lets the flight controls sit on keys
 * the vanilla movement bindings already use.
 */
public interface InputContext extends IKeyConflictContext {
    static InputContext of(ResourceLocation id, Supplier<Boolean> activeCondition) {
        return new InputContextImpl(id, activeCondition);
    }

    ResourceLocation getId();

    /**
     * @return whether the bindings in this context are currently live.
     */
    boolean isActive();

    /**
     * Puts a binding under this context. Must be called before the binding is
     * registered, and a binding belongs to at most one context.
     */
    void addKeyBinding(KeyMapping keyBinding);

    List<KeyMapping> getKeyBindings();

    /**
     * @return the binding in this context currently bound to the given key, or
     *         null if this context has none.
     */
    KeyMapping getKeyBinding(InputConstants.Key key);
}

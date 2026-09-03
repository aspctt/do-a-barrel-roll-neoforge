package nl.enjarai.doabarrelroll.impl.key;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.settings.IKeyConflictContext;
import nl.enjarai.doabarrelroll.api.key.InputContext;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class InputContextImpl implements InputContext {
    private static final List<InputContextImpl> CONTEXTS = new ReferenceArrayList<>();

    public static List<InputContextImpl> getContexts() {
        return CONTEXTS;
    }

    private final ResourceLocation id;
    private final Supplier<Boolean> activeCondition;
    private final List<KeyMapping> keyBindings = new ReferenceArrayList<>();

    public InputContextImpl(ResourceLocation id, Supplier<Boolean> activeCondition) {
        this.id = id;
        this.activeCondition = activeCondition;
        CONTEXTS.add(this);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    /**
     * Called by NeoForge from {@link KeyMapping#isDown()} on every query, so this
     * stays a direct read of the condition rather than something cached per tick.
     */
    @Override
    public boolean isActive() {
        return activeCondition.get();
    }

    /**
     * Only bindings in the same context can conflict with each other. A binding
     * outside any context, including every vanilla one, is checked from its own
     * side too, and the universal context those use answers true; the conflict
     * suppression for that case lives in KeyBindsListEntryMixin.
     */
    @Override
    public boolean conflicts(IKeyConflictContext other) {
        return this == other;
    }

    @Override
    public void addKeyBinding(KeyMapping keyBinding) {
        Objects.requireNonNull(keyBinding);
        keyBindings.add(keyBinding);
        keyBinding.setKeyConflictContext(this);
    }

    @Override
    public List<KeyMapping> getKeyBindings() {
        return keyBindings;
    }

    @Override
    public KeyMapping getKeyBinding(InputConstants.Key key) {
        for (var keyBinding : keyBindings) {
            if (keyBinding.getKey().equals(key)) {
                return keyBinding;
            }
        }
        return null;
    }
}

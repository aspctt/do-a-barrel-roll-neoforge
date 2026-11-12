package nl.enjarai.doabarrelroll.impl.key;

import com.mojang.blaze3d.platform.InputConstants;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
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
    private boolean active;

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
     * Re-reads the condition. Called once per client tick, matching upstream,
     * which evaluated the same condition on its own tick rather than per query.
     *
     * <p>NeoForge only hands a key press or release to bindings whose context is
     * active at that moment. So a key already held when this context switches on,
     * like W carried from the run-up into a takeoff, never reached the thrust
     * binding, and thrust did nothing until W was let go and pressed again. A key
     * let go while the context was off stayed down too, and came back as phantom
     * thrust on the next flight. Upstream re-read the keyboard whenever the state
     * flipped, which fixes both, so this does the same on the way in.
     */
    public void tick() {
        boolean isActive = activeCondition.get();
        if (isActive == active) return;

        active = isActive;
        if (active) {
            syncHeldKeys();
        }
    }

    /**
     * Sets each of this context's bindings to whether its key is physically held.
     *
     * <p>Upstream called KeyMapping.setAll() for this, but that resyncs every binding
     * in the game, and on 1.21.1 it also flips toggle sprint or sneak whenever their
     * key happens to be held at that moment. Only this context's bindings missed
     * events, so only they are touched.
     */
    private void syncHeldKeys() {
        for (var keyBinding : keyBindings) {
            var key = keyBinding.getKey();

            // Mouse buttons and unbound keys are left alone, as vanilla's own resync does
            if (key.getType() != InputConstants.Type.KEYSYM || key.getValue() == InputConstants.UNKNOWN.getValue()) continue;

            keyBinding.setDown(isKeyDown(key.getValue()));
        }
    }

    /**
     * Whether a keyboard key is held right now. 1.21.11 replaced the raw GLFW window
     * handle this took with the Window itself, and 26.3's SDL3 input needs neither.
     */
    private static boolean isKeyDown(int keyCode) {
        //? if <1.21.11 {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyCode);
        //?} else if <26.3 {
        /*return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), keyCode);
        *///?} else {
        /*return InputConstants.isKeyDown(keyCode);
        *///?}
    }

    /**
     * NeoForge asks this from {@link KeyMapping#isDown()}, which is a much hotter
     * path than the key events upstream gated on, and the condition behind it
     * walks a roll group's list of conditions. Hence the cached field: the answer
     * cannot change within a tick anyway.
     */
    @Override
    public boolean isActive() {
        return active;
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

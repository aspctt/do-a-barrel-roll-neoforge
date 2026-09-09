package nl.enjarai.doabarrelroll.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.debug.DebugEntryPosition;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.client.gui.components.debug.DebugScreenEntry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import nl.enjarai.doabarrelroll.api.RollEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * The roll readout on the debug screen, for 1.21.11 and later.
 *
 * <p>Up to 1.21.10 the roll was appended to the vanilla facing line, inside the same bracket as yaw
 * and pitch, by rewriting that line before it was drawn. The debug screen is a registry of entries
 * from 1.21.11 on and an entry only ever adds lines, so the roll goes in as a line of its own in the
 * same position group instead, immediately under Facing.
 *
 * <p>This class is compiled only for the versions that have the registry; the build excludes it
 * elsewhere.
 */
public class RollDebugEntry implements DebugScreenEntry {
    @Override
    public void display(DebugScreenDisplayer displayer, @Nullable Level level,
                        @Nullable LevelChunk chunk, @Nullable LevelChunk serverChunk) {
        var cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity == null) return;

        float roll = ((RollEntity) cameraEntity).doABarrelRoll$getRoll();
        displayer.addToGroup(DebugEntryPosition.GROUP, String.format(Locale.ROOT, "Roll: %.1f", roll));
    }
}

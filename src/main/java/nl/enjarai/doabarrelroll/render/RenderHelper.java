package nl.enjarai.doabarrelroll.render;

import net.minecraft.client.gui.GuiGraphics;
//? if <1.21.11 {
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
//?} else
/*import net.minecraft.client.renderer.RenderPipelines;*/

import java.util.function.BiConsumer;

public class RenderHelper {
    /**
     * Plots one white pixel, blended so that it inverts whatever is behind it, which is how the
     * crosshair reads against any background.
     *
     * <p>1.21.11 replaced the immediate mode buffer with render pipelines, and the inverting blend
     * these widgets set by hand is now a pipeline of its own, so the blend belongs to the draw rather
     * than to global state around it.
     */
    public static BiConsumer<Integer, Integer> blankPixel(GuiGraphics graphics) {
        //? if <1.21.11 {
        return (x, y) -> {
            int color = 0xffffffff;
            var matrix = graphics.pose().last().pose();
            var bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            bufferBuilder.addVertex(matrix, (float) x, (float) y + 1, 0.0F).setColor(color);
            bufferBuilder.addVertex(matrix, (float) x + 1, (float) y + 1, 0.0F).setColor(color);
            bufferBuilder.addVertex(matrix, (float) x + 1, (float) y, 0.0F).setColor(color);
            bufferBuilder.addVertex(matrix, (float) x, (float) y, 0.0F).setColor(color);
            BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        };
        //?} else {
        /*return (x, y) -> graphics.fill(RenderPipelines.GUI_INVERT, x, y, x + 1, y + 1, 0xffffffff);
        *///?}
    }
}

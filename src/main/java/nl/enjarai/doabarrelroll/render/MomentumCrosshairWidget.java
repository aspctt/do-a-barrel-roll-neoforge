package nl.enjarai.doabarrelroll.render;

import net.minecraft.client.gui.GuiGraphics;
//? if <1.21.11 {
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
//?}
import nl.enjarai.doabarrelroll.ModMath;
import org.joml.Vector2d;

public class MomentumCrosshairWidget extends RenderHelper {

    public static void render(GuiGraphics graphics, int scaledWidth, int scaledHeight, Vector2d mouseTurnVec) {
        int centerX = scaledWidth / 2;
        int centerY = scaledHeight / 2 - 1;
        mouseTurnVec.mul(50);
        var lineVec = new Vector2d(mouseTurnVec).add(
                new Vector2d(mouseTurnVec).negate().normalize().mul(Math.min(mouseTurnVec.length(), 10f)));

        if (!lineVec.equals(new Vector2d()) && mouseTurnVec.lengthSquared() > 10f * 10f) {

            // From 1.21.11 the inverting blend is carried by the pipeline the pixel is drawn with, so
            // there is no global blend state to set up and put back here.
            //? if <1.21.11 {
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            //?}
            ModMath.forBresenhamLine(
                    centerX, centerY,
                    centerX + (int) lineVec.x, centerY + (int) lineVec.y,
                    blankPixel(graphics)
            );
            //? if <1.21.11 {
            RenderSystem.defaultBlendFunc();
            //?}
        }

        // change the position of the crosshair, which is rendered up the stack. The GUI transform
        // became a 2D matrix stack in 1.21.11, so there is no depth argument to pass any more.
        //? if <1.21.11 {
        graphics.pose().translate((int) mouseTurnVec.x, (int) mouseTurnVec.y, 0);
        //?} else
        /*graphics.pose().translate((int) mouseTurnVec.x, (int) mouseTurnVec.y);*/
    }
}

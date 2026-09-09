package nl.enjarai.doabarrelroll.render;

import net.minecraft.client.gui.GuiGraphics;
//? if <1.21.11 {
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.GameRenderer;
//?}
import nl.enjarai.doabarrelroll.ModMath;
import nl.enjarai.doabarrelroll.math.MagicNumbers;
import org.joml.Vector2d;

public class HorizonLineWidget extends RenderHelper {
    public static void render(GuiGraphics graphics, int scaledWidth, int scaledHeight, double roll, double pitch) {
        int centerX = scaledWidth / 2 - 1;
        int centerY = scaledHeight / 2 - 1;
        roll *= -MagicNumbers.TORAD;

        var v = new Vector2d(Math.cos(roll), Math.sin(roll));
        var offset = new Vector2d(v).perpendicular().mul(pitch * scaledHeight * 0.007);

        centerX += Math.round(offset.x);
        centerY += Math.round(offset.y);

        // From 1.21.11 the inverting blend is carried by the pipeline the pixel is drawn with, so
        // there is no global blend state to set up and put back here.
        //? if <1.21.11 {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        //?}
        for (int i = 0; i < 2; i++) {
            v.negate();

            var start = v.mul(10.0, new Vector2d());
            var end = v.mul(50.0, new Vector2d());

            ModMath.forBresenhamLine(
                    centerX + (int) start.x, centerY + (int) start.y,
                    centerX + (int) end.x, centerY + (int) end.y,
                    blankPixel(graphics)
            );
        }
        //? if <1.21.11 {
        RenderSystem.defaultBlendFunc();
        //?}
    }
}

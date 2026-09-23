package nl.enjarai.doabarrelroll.util;

//? if <26.3 {
import net.minecraft.Util;
//?} else
/*import com.mojang.blaze3d.Blaze3D;*/

import java.net.URI;

/**
 * Opening a link in the system browser, in the one place that has to know how.
 *
 * <p>26.3 moved the window layer from GLFW to SDL3 and took opening links with it, from the platform
 * helper on Util to Blaze3D.
 */
public class LinkUtil {
    public static void openUri(URI uri) {
        //? if <26.3 {
        Util.getPlatform().openUri(uri);
        //?} else
        /*Blaze3D.openUri(uri);*/
    }
}

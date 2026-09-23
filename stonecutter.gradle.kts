plugins {
    id("dev.kikugie.stonecutter")
}

stonecutter active "1.21.1"

// Builds every target in one go. Building one on its own still works as ./gradlew "<target>:build".
// Stonecutter generates the sources per subproject, so this is a plain aggregate rather than anything
// that has to switch the active version back and forth.
tasks.register("buildAll") {
    group = "build"
    description = "Assembles and tests every Minecraft version declared in settings.gradle.kts."
    dependsOn(stonecutter.versions.map { ":${it.project}:build" })
}

stonecutter parameters {
    // Available to source files as `//$ minecraft` swaps and in `//? if` conditions.
    swaps["minecraft"] = "\"${node.metadata.version}\";"

    // Pure renames only. Anything that changes arity, arguments or semantics is handled with an inline
    // `//? if` directive instead, so the difference is visible where it matters.
    replacements {
        string(current.parsed >= "1.21.11") {
            // ResourceLocation became Identifier. The mod has no type of its own whose name contains
            // either word, so these patterns cannot collide in either direction.
            replace("import net.minecraft.resources.ResourceLocation;", "import net.minecraft.resources.Identifier;")
            replace("ResourceLocation ", "Identifier ")
            replace("ResourceLocation,", "Identifier,")
            replace("ResourceLocation>", "Identifier>")
            replace("ResourceLocation.", "Identifier.")
            replace("ResourceLocation)", "Identifier)")

            // Util moved down into the util package it names. Nothing else in the mod imports a type
            // called Util, so the import line is the only thing that has to move.
            replace("import net.minecraft.Util;", "import net.minecraft.util.Util;")
        }

        string(current.parsed >= "26.1") {
            // GuiGraphics was renamed. It is a rename only: pose, fill and blit keep their shapes and
            // the NeoForge GUI events hand back the renamed type. The event accessor is spelled
            // getGuiGraphics() either way, and has no space after the name, so it is left alone.
            replace("import net.minecraft.client.gui.GuiGraphics;", "import net.minecraft.client.gui.GuiGraphicsExtractor;")
            replace("GuiGraphics ", "GuiGraphicsExtractor ")
        }

        string(current.parsed >= "26.3") {
            // 26.3 moved input from GLFW to SDL3 and renamed the keyboard key type to match. The
            // InputConstants.KEY_ constants carry the new codes and the key names stored in options.txt
            // are unchanged, so existing bindings carry over.
            replace("InputConstants.Type.KEYSYM", "InputConstants.Type.KEYBOARD")
        }
    }
}

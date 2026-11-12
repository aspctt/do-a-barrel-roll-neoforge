import me.modmuss50.mpp.PublishModTask

plugins {
    id("dev.kikugie.stonecutter")
    // Versioned here and applied bare in build.gradle.kts, so every subproject shares this one copy of the
    // plugin. Each subproject hands the root's GitHub task to parent(), which casts it to the plugin's own
    // task class, and that cast fails against a second copy loaded separately.
    id("me.modmuss50.mod-publish-plugin") version "2.2.1"
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

// Releasing. `./gradlew publishMods` puts every target on Modrinth and CurseForge and attaches each jar to
// one GitHub release for the version. The per-target half lives in build.gradle.kts.

val releaseVersion: String = providers.gradleProperty("mod_version").get()

// Uploads only happen with all three tokens set. Missing any one makes the whole run a dry run, which
// writes what would have been sent under each project's build/publishMods and contacts nothing, so a
// forgotten token can never leave a release up on some platforms and not others.
// A provider rather than a value, so only a build that publishes ever reads the tokens.
val releaseTokens = listOf("MODRINTH_TOKEN", "CURSEFORGE_TOKEN", "GITHUB_TOKEN")
val isReleaseDryRun: Provider<Boolean> = releaseTokens
    .map { name -> providers.environmentVariable(name).map { it.isBlank() }.orElse(true) }
    .reduce { isMissing, isOtherMissing -> isMissing.zip(isOtherMissing) { first, second -> first || second } }

// The configuration cache would store the tokens the publish tasks read, encrypted, under .gradle in this
// folder. A build that publishes skips the cache instead, so the tokens are never written to disk.
allprojects {
    tasks.withType<PublishModTask>().configureEach {
        notCompatibleWithConfigurationCache("Keeps the publishing tokens out of the configuration cache.")
    }
}

// A link reference at the foot of CHANGELOG.md, such as "[3.7.5]: https://...", which ends the last section
val changelogLinkReference = Regex("""^\[[^\]]+]: .*""")

/**
 * One version's notes from CHANGELOG.md: everything under its "## [x.y.z]" heading, up to the next
 * heading or the link references. Called when a publish task runs rather than while configuring, so a
 * version without a section yet stops only a release, never a build.
 */
fun changelogSection(version: String): String {
    val lines = file("CHANGELOG.md").readLines()
    val start = lines.indexOfFirst { it.startsWith("## [$version]") }
    check(start >= 0) { "CHANGELOG.md has no section for $version, so there is nothing to publish as its changelog." }

    return lines.drop(start + 1)
        .takeWhile { !it.startsWith("## ") && !changelogLinkReference.matches(it) }
        .joinToString("\n")
        .trim()
}

publishMods {
    // GitHub's release notes, without the version heading its title already shows. The subprojects build
    // the portal changelogs from this, so CHANGELOG.md is the only place the notes are written.
    changelog = providers.provider { changelogSection(releaseVersion) }
    type = STABLE
    version = releaseVersion
    displayName = releaseVersion
    dryRun = isReleaseDryRun

    // The release itself, created empty. Each subproject then uploads its jar into it through parent().
    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        repository = "aspctt/do-a-barrel-roll-neoforge"
        // Only used when the tag does not exist yet, in which case GitHub creates it at the tip of main.
        // Push the tag first so it marks the commit the jars were built from.
        commitish = "main"
        tagName = releaseVersion
        allowEmptyFiles = true
    }
}

// On each platform the targets upload one at a time, oldest Minecraft version first, so the GitHub release
// lists its jars in that order and the newest version ends up on top of each portal's file list. Left
// alone, parallel execution uploads them in whatever order the workers finish. The platforms still run
// alongside each other. Wired once every target has been evaluated, since each one creates its own tasks.
gradle.projectsEvaluated {
    for (taskName in listOf("publishGithub", "publishModrinth", "publishCurseforge")) {
        val uploads = stonecutter.versions.map { project(":${it.project}").tasks.named(taskName) }
        uploads.zipWithNext { earlier, later ->
            later.configure { mustRunAfter(earlier) }
        }
    }
}

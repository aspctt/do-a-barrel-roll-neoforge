plugins {
    id("java-library")
    id("maven-publish")
    id("net.neoforged.moddev") version "2.0.147"
    id("idea")
}

// This script is the central build for every versioned subproject, so anything that reads a file has to
// reach for the root rather than the version directory it is being evaluated in.
fun prop(name: String): String = property(name) as String

val modId: String = prop("mod_id")
val archivesNameValue: String = prop("archives_name")
val modVersion: String = prop("mod_version")
val minecraftVersion: String = prop("minecraft_version")

fun extraBuildMetadata(): String {
    val buildNumber = System.getenv("GITHUB_RUN_NUMBER") ?: return ""
    return ".build.$buildNumber"
}

version = "$modVersion+$minecraftVersion" + extraBuildMetadata()
group = prop("mod_group_id")

base {
    archivesName = archivesNameValue
}

// Compared numerically against the Stonecutter target name, because 1.21.11 sorts before 1.21.2 as a
// string and 26.1 sorts before 1.21.1.
fun versionAtLeast(target: String): Boolean {
    fun parts(version: String) = version.split(".").map { it.toIntOrNull() ?: 0 }
    val current = parts(stonecutter.current.version)
    val other = parts(target)
    for (i in 0 until maxOf(current.size, other.size)) {
        val a = current.getOrElse(i) { 0 }
        val b = other.getOrElse(i) { 0 }
        if (a != b) return a > b
    }
    return true
}

repositories {
    mavenCentral()
    // Yet Another Config Lib. Its own maven rather than Modrinth's, because Modrinth resolves a version
    // to its primary file and YACL publishes the Fabric and NeoForge builds under one version, so the
    // coordinate can hand back the wrong loader's jar.
    maven("https://maven.isxander.dev/releases") { name = "Xander Maven" }
    // Transitive dependencies of YACL.
    maven("https://maven.terraformersmc.com/releases") { name = "Terraformers" }
}

// Mojang ships Java 21 to end users through 1.21.11, and Java 25 from 26.1.
val javaVersion = if (versionAtLeast("26.1")) 25 else 21
java.toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)

neoForge {
    version = prop("neo_version")

    // Parchment lags new Minecraft releases, so a target without it still builds; only the parameter
    // names and javadoc are missing.
    if (project.hasProperty("parchment_mappings_version")) {
        parchment {
            mappingsVersion = prop("parchment_mappings_version")
            minecraftVersion = prop("parchment_minecraft_version")
        }
    }

    runs {
        create("client") {
            client()
            programArguments.addAll("--width", "1920", "--height", "1080")
            // One run directory shared by every version, so worlds and options survive switching targets.
            gameDirectory = rootProject.file("run")
        }

        create("server") {
            server()
            programArgument("--nogui")
            gameDirectory = rootProject.file("run-server")
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

// Sets up a dependency configuration called "localRuntime". Use it instead of "runtimeOnly" for a
// dependency that should be present when the development client launches but must not be pulled in by
// anyone depending on this mod.
val localRuntime = configurations.create("localRuntime")
configurations.runtimeClasspath.get().extendsFrom(localRuntime)

dependencies {
    // The config screen is the only thing that touches YACL, and every entry point into it is guarded by
    // Compat.isYACLLoaded(), so the mod runs correctly with YACL absent. compileOnly keeps it out of the
    // published dependency set; localRuntime installs it for development launches.
    compileOnly("dev.isxander:yet-another-config-lib:${prop("yacl_version")}")
    localRuntime("dev.isxander:yet-another-config-lib:${prop("yacl_version")}")
}

// 26.2 split the mod list image in two, a wide banner and a square icon, and deprecated the single
// property that used to serve both. The mod ships a square icon.
val iconField = if (versionAtLeast("26.2")) "iconFile" else "logoFile"

// The debug screen became a registry of entries in 1.21.11. Up to 1.21.10 the roll is appended to the
// vanilla facing line from a game bus event instead, and the entry class has no supertype to implement.
if (!versionAtLeast("1.21.11")) {
    sourceSets.main.get().java.exclude("**/render/RollDebugEntry.java")
}

// 1.21.9 replaced the single pack format number with a supported range, so the field itself differs and
// not just its value.
val packFormatField = if (versionAtLeast("1.21.9")) {
    val major = prop("resource_pack_format")
    val minor = prop("resource_pack_format_minor")
    "\"min_format\": $major," + System.lineSeparator() + "        \"max_format\": [$major, $minor],"
} else {
    "\"pack_format\": " + prop("resource_pack_format") + ","
}

// Every mixin this version applies. A mixin left out here is also left out of the compile, so a target
// class that no longer exists never has to be worked around in the source.
val commonMixins = buildList {
    add("CommandsMixin")
    add("roll.ServerEntityMixin")
    add("roll.entity.EntityMixin")
    add("roll.entity.LivingEntityMixin")
    add("roll.entity.PlayerMixin")
}.sorted()

val clientMixins = buildList {
    add("client.LivingEntityMixin")
    add("client.LocalPlayerMixin")
    add("client.PlayerMixin")
    add("client.key.KeyBindsListEntryMixin")
    add("client.key.KeyMappingAccessor")
    add("client.roll.CameraMixin")
    add("client.roll.MouseHandlerMixin")
    add("client.roll.PlayerRendererMixin")
    add("client.roll.entity.LocalPlayerMixin")
}.sorted()

fun mixinList(entries: List<String>) = entries.joinToString(",\n    ") { "\"$it\"" }

// Expand the declared properties into the mod metadata, the mixin config and the pack description, which
// is where they live so that versions are declared once in gradle.properties rather than duplicated.
val generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    val replaceProperties = mapOf(
        "minecraft_version" to prop("minecraft_version"),
        "minecraft_version_range" to prop("minecraft_version_range"),
        "neo_version" to prop("neo_version"),
        "neo_version_range" to prop("neo_version_range"),
        "loader_version_range" to prop("loader_version_range"),
        "mod_id" to modId,
        "mod_name" to prop("mod_name"),
        "mod_license" to prop("mod_license"),
        "mod_version" to project.version.toString(),
        "yacl_min_version" to prop("yacl_min_version"),
        "pack_format_field" to packFormatField,
        "java_version" to javaVersion.toString(),
        "icon_field" to iconField,
        "common_mixins" to mixinList(commonMixins),
        "client_mixins" to mixinList(clientMixins),
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(rootProject.file("src/main/templates"))
    into(layout.buildDirectory.dir("generated/sources/modMetadata"))
}
sourceSets.main.get().resources.srcDir(generateModMetadata)
neoForge.ideSyncTask(generateModMetadata)

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$archivesNameValue" }
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = archivesNameValue
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = rootProject.file("repo").toURI()
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

// For tools/check-linkage.py: the exact compile classpath of the NeoForge build this target is configured
// against, which -Pneo_version can point at another build of the same Minecraft line for one run.
tasks.register("writeCompileClasspath") {
    val classpath = configurations.compileClasspath.get()
    val output = layout.buildDirectory.file("linkage/${prop("neo_version")}.classpath")
    inputs.files(classpath)
    outputs.file(output)
    doLast {
        output.get().asFile.writeText(classpath.files.joinToString("\n"))
    }
}

// IDEA no longer downloads sources/javadoc jars for dependencies on its own.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

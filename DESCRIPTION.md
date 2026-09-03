# <p align=center> Ballistic API </p>

![Version](https://img.shields.io/badge/Available_for-1.21.1-blue)
![Mod Loader](https://img.shields.io/badge/Mod_Loader-NeoForge-orange)
![Requires](https://img.shields.io/badge/Requires-AzureLib-blueviolet)
![License](https://img.shields.io/badge/License-All_Rights_Reserved-red)

Ballistic API is a library for adding guns, ammunition, and attachments to Minecraft through content packs. It ships the systems, not the weapons: there is nothing to craft and no creative tab to browse. Everything a player fires is defined by a pack, and adding a weapon requires no code.

### Content packs

A pack is plain data, read straight off disk from `ballistic/packs/` as a loose folder or an archive. Definitions are JSONC, so comments and trailing commas are allowed.

Guns, ammunition, and attachments are resolved and validated together, so a magazine pointing at ammunition that does not exist is reported at load with the file named, rather than crashing at the first shot. `/ballistic reload` re-reads everything without restarting.

A pack declares what a weapon is and what it looks like:

Stats, fire modes, and ballistics --> gameplay, resolved on the server

Model, texture, animations, sounds --> presentation, client only

### Attachments

Attachments modify stats through a pipeline rather than overwriting them. Every additive change is summed before any multiplier applies, so installation order never changes the result, and every stat stays inside bounds the pack declares.

### Rendering

Weapons render as animated 3D models through AzureLib, with sounds and animations mapped per event by the pack. First person arms are the player's own, drawn with their real skin.

Because presentation is client side only, a dedicated server runs correctly with no models, textures, or sounds on disk at all.

### Requirements

Minecraft 1.21.1, NeoForge 21.1.248 or newer, and AzureLib 3.1.11 or newer.

Tested alongside Sodium, Lithium, FerriteCore, ScalableLux, NotEnoughAnimations, ModernFix, and Iris. ModernFix's dynamic resources feature is supported specifically.

### License

Ballistic API is All Rights Reserved. The full terms are in [LICENSE](LICENSE), and the short version is:

- Download it and play with it freely.
- Content packs are yours. Create them for any purpose including commercial ones, distribute and sell them on any terms you choose, and use the pack formats, schemas, field names, directory layouts, and public API to do it. No ownership is claimed over your work and no attribution is required. This grant is irrevocable: it cannot be withdrawn from packs already published, and it survives any future change to the licence.
- Separate mods, tools, editors, and validators that interoperate through the public API or pack formats are equally permitted.
- Modpacks may include the mod by reference, the way a CurseForge manifest or a Modrinth index does, so the launcher fetches it from an official page. Re-hosting, bundling, or altering the JAR is not permitted.
- The mod's own source code and assets stay reserved.

Trademarks and third-party licences are covered in [NOTICE](NOTICE).

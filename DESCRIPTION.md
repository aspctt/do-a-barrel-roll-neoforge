# <p align="center" style="text-align: center;"><img alt="Banner" src="https://i.ibb.co/gbbthDJQ/Do-Another-Barrel-Roll-Banner.png"></p>

<p align="center" style="text-align: center;"><img alt="Available for" src="https://img.shields.io/badge/Available_for-1.21.1_--_26.3-blue"> <img alt="Requires" src="https://img.shields.io/badge/Requires-Nothing-brightgreen"> <img alt="License" src="https://img.shields.io/badge/License-GPL--3.0--only-red"></p>

<p align="center" style="text-align: center;"><img alt="Available for NeoForge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg"> <img alt="Won't support Forge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg"></p>

<p align="center" style="text-align: center;"><a href="https://buymeacoffee.com/aspctt"><img alt="Buy Me a Coffee" height="40" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/donate/buymeacoffee-singular_vector.svg"></a></p>

<p align="center" style="text-align: center;"><a href="https://github.com/aspctt/do-a-barrel-roll-neoforge"><img alt="Available on GitHub" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg"></a> <a href="https://modrinth.com/mod/do-another-barrel-roll"><img alt="Available on Modrinth" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg"></a> <a href="https://www.curseforge.com/minecraft/mc-mods/do-another-barrel-roll"><img alt="Available on CurseForge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg"></a></p>

**A native NeoForge port of enjarai's Do a Barrel Roll.**

Do a Barrel Roll unlocks the camera during elytra flight. Moving the mouse sideways rolls you and moving it up and down pitches you, while A and D yaw. Rolling to one side also turns you toward it, which the mod calls banking, and camera smoothing takes the edge off sharp inputs.

<p align="center" style="text-align: center;"><img alt="PreviewGIF" src="https://github.com/enjarai/do-a-barrel-roll/raw/1.19.4/dev/img/ravine.gif"></p>

### Why this version exists

The official NeoForge build is made from a multiloader template and runs on the Forgified Fabric API, so that has to be installed alongside it before the game will start. This port is written against NeoForge directly, with no Fabric API, no Forgified Fabric API, no CICADA, no Mod Menu and no Fabric permissions API. Nothing is required beyond NeoForge itself.

Flying works the same as upstream. Configs, keybinds, permission nodes and the server handshake keep their original names, so an existing config file carries straight over.

### Controls

The mouse handles roll and pitch, while A and D yaw. The I key turns the mod on and off, and every key can be rebound. The flight keys only work while you are flying, so yaw shares A and D with strafing without showing up as a conflict.

### Configuration

Install [YACL](https://modrinth.com/mod/yacl) and the config screen opens from the Config button next to the mod in NeoForge's mod list. It covers sensitivity and smoothing per axis, banking, a horizon widget and how the elytra activates. YACL is optional; without it the mod runs the same and the button offers to open YACL's download page.

### Server-side features

Install the mod on the server as well and players see each other roll. Vanilla clients and vanilla servers stay compatible either way. Thrusting, which lets you accelerate without fireworks, stays off on servers until they allow it. Operators get a Server tab for thrusting, forced activation, forced installation, kinetic damage and an operator bypass, and both permission nodes are registered with NeoForge's permission API.

### Requirements

Minecraft 1.21.1, 1.21.11, 26.1, 26.2 or 26.3, each with the matching NeoForge. The 26.1 jar covers 26.1, 26.1.1 and 26.1.2. YACL is optional, for the config screen.

### License

GPL-3.0-only, the same licence as upstream. The full terms are in [LICENSE](https://github.com/aspctt/do-a-barrel-roll-neoforge/blob/main/LICENSE).

Do a Barrel Roll is by [enjarai](https://github.com/enjarai), based on [Cool Elytra Roll](https://github.com/Jorbon/cool_elytra) by Jorbon. Mod icon by Mizeno. Native NeoForge port by aspctt.

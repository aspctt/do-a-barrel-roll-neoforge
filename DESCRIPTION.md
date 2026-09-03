# <p align=center> Do Another Barrel Roll </p>

![Version](https://img.shields.io/badge/Available_for-1.21.1-blue)
![Mod Loader](https://img.shields.io/badge/Mod_Loader-NeoForge-orange)
![Dependencies](https://img.shields.io/badge/Dependencies-None-brightgreen)
![License](https://img.shields.io/badge/License-GPL--3.0-red)

**A native NeoForge port of enjarai's Do a Barrel Roll.** Same mod, different name because CurseForge does not allow a loader in a project title. It shares a mod id with the original, so the two cannot be installed together.

Do a Barrel Roll is a lightweight, mostly clientside mod that changes elytra flight to be more fun and semi-realistic. It redesigns movement around a completely unlocked camera, giving you full pitch, yaw and roll control in flight, along with camera modifiers like smoothing and banking.

### Why this version exists

The official NeoForge build is the Fabric jar running through Sinytra Connector, so it needs both Connector and the Forgified Fabric API before it will start. This port is written against NeoForge directly, with no Connector, no Fabric API, no CICADA, no Mod Menu and no Fabric permissions API. Nothing is required beyond NeoForge itself.

Behaviour is unchanged. Configs, keybinds, permission nodes and the server handshake keep their original names, so an existing config file carries straight over.

### Controls

Mouse x axis rolls, mouse y axis pitches, and the strafe keys yaw. All rebindable. The flight bindings sit in their own key conflict context, so putting yaw on A and D does not conflict with vanilla strafing: they are only live while you are flying.

### Configuration

Install [YACL](https://modrinth.com/mod/yacl) and the config screen appears behind the Config button next to the mod in NeoForge's mod list. YACL is optional; without it the mod runs the same and the button offers to install it.

### Server-side features

Install it on the server as well and playermodel roll is synced between clients. Vanilla clients and vanilla servers stay fully compatible either way. Operators get a Server tab covering thrusting, forced activation, forced installation and kinetic damage, and both permission nodes are registered with NeoForge's permission API.

### Requirements

Minecraft 1.21.1 and NeoForge 21.1.249 or newer. YACL 3.6.0 or newer is optional, for the config screen.

### License

GPL-3.0-only, the same licence as upstream. The full terms are in [LICENSE](LICENSE).

Do a Barrel Roll is by [enjarai](https://github.com/enjarai), based on [Cool Elytra Roll](https://github.com/Jorbon/cool_elytra) by Jorbon. Mod icon by Mizeno. Native NeoForge port by aspctt.

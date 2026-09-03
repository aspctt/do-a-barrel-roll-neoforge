# Changelog

## 3.7.3+1.21.1

First release of the native NeoForge port, forked from upstream 3.7.3.

- Runs on NeoForge directly. Sinytra Connector, Fabric API, Forgified Fabric API, CICADA, Mod Menu and the Fabric permissions API are all gone, and nothing replaces them: the jar has no dependencies beyond NeoForge.
- Config screen is reached through NeoForge's mod list Config button. YACL stays optional.
- Camera roll, the crosshair widgets, the Peppy overlay and the F3 roll readout run on NeoForge events instead of mixins.
- Kinetic damage is applied through the incoming damage event rather than by rewriting a local inside `LivingEntity.travel`.
- Flight keybindings use NeoForge key conflict contexts, so yaw on A and D no longer shows as conflicting with vanilla strafing.
- Permission nodes `do_a_barrel_roll.configure` and `do_a_barrel_roll.ignore_config` are registered with NeoForge's permission API.
- Serverbound roll packet moved to `do_a_barrel_roll:roll_sync_c2s`. NeoForge allows one payload per channel id rather than one per direction; the clientbound packet keeps the original id.
- Controller support is absent. Upstream's Controlify integration is a Fabric entrypoint and was already missing from the official NeoForge build.
- Lang files converted from YAML to JSON.

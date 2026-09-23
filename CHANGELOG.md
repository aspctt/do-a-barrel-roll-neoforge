# Changelog

## 3.7.4, Minecraft 1.21.1, 1.21.11, 26.1, 26.2 and 26.3

Adds the four newer Minecraft versions alongside 1.21.1. One jar per API band, built from one source
tree with Stonecutter. The 26.1 jar covers 26.1, 26.1.1 and 26.1.2.

- The debug screen roll readout is its own line from 1.21.11 on. That version turned the debug screen
  into a registry of entries which can only add lines, so it can no longer be folded into the vanilla
  facing line the way it still is on 1.21.1.
- The playermodel bank is written into the render state from 1.21.11 on, rather than intercepted in
  the renderer, because that is where the rotation now comes from.
- The crosshair and horizon widgets draw through the inverting GUI pipeline from 1.21.11 on, instead
  of setting the blend around an immediate mode draw.
- Keybinding categories are registered ids from 1.21.11 on, so the lang files carry both spellings of
  the category keys.
- Everything a player interacts with is unchanged, and configs, keybinds, permission nodes and the
  server handshake keep their names across all five versions.
- 26.3 moved input from GLFW to SDL3. Default keybinds use the new key codes, and bindings already
  saved in options.txt carry over because the key names did not change.
- On 26.3, jumps made in lava do not count towards triple jump or hybrid activation. Vanilla 26.3
  refuses to start gliding in lava as well as water, and the activation check follows it.
- Ships this project's own mod icon rather than the one inherited from upstream.

## 3.7.3+1.21.1

First release of the native NeoForge port, forked from upstream 3.7.3.

- Runs on NeoForge directly. Fabric API, Forgified Fabric API, CICADA, Mod Menu and the Fabric permissions API are all gone, and nothing replaces them: the jar has no dependencies beyond NeoForge.
- Config screen is reached through NeoForge's mod list Config button. YACL stays optional.
- Camera roll, the crosshair widgets, the Peppy overlay and the F3 roll readout run on NeoForge events instead of mixins.
- Kinetic damage is applied through the incoming damage event rather than by rewriting a local inside `LivingEntity.travel`.
- Flight keybindings use NeoForge key conflict contexts, so yaw on A and D no longer shows as conflicting with vanilla strafing.
- Permission nodes `do_a_barrel_roll.configure` and `do_a_barrel_roll.ignore_config` are registered with NeoForge's permission API.
- Serverbound roll packet moved to `do_a_barrel_roll:roll_sync_c2s`. NeoForge allows one payload per channel id rather than one per direction; the clientbound packet keeps the original id.
- Controller support is absent. Upstream's Controlify integration is a Fabric entrypoint and was already missing from the official NeoForge build.
- Lang files converted from YAML to JSON.

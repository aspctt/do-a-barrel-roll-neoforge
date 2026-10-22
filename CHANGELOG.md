# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Changed

- Each jar requires the oldest NeoForge build it has been checked against: 21.1.217, 21.11.45, 26.1.0.19-beta, 26.2.0.76 and 26.3.0.12-beta

## [3.7.4] - 2026-09-09

### Added

- Minecraft 1.21.11, 26.1 and 26.2 support alongside 1.21.1, with the 26.1 jar covering 26.1, 26.1.1 and 26.1.2
- Minecraft 26.3 support, added to this version on 2026-09-23

### Changed

- The F3 roll readout is its own line from 1.21.11 on, since the debug screen there can only add lines rather than change vanilla's facing line
- On 26.3, jumps made in lava no longer count towards triple jump or hybrid activation, matching vanilla refusing to glide in lava
- On 26.3, default keybinds use the new SDL3 key codes, and binds already saved in options.txt carry over
- The 1.21.1 jar no longer requires NeoForge 21.1.249 or newer
- The mod list's homepage button opens the Modrinth page instead of GitHub
- The mod ships its own icon rather than the one inherited from upstream

## [3.7.3] - 2026-09-03

First release of the native NeoForge port, forked from Do a Barrel Roll 3.7.3.

### Changed

- The config screen opens from the Config button in NeoForge's mod list, and YACL stays optional
- Camera roll, the crosshair widgets, the Peppy overlay and the F3 roll readout run on NeoForge events instead of mixins
- Kinetic damage goes through NeoForge's incoming damage event rather than rewriting a local inside `LivingEntity.travel`
- Flight keybinds use NeoForge key conflict contexts, so yaw on A and D no longer shows as conflicting with strafing
- The permission nodes `do_a_barrel_roll.configure` and `do_a_barrel_roll.ignore_config` are registered with NeoForge's permission API
- The serverbound roll packet moved to `do_a_barrel_roll:roll_sync_c2s`, since NeoForge allows one payload per channel id rather than one per direction
- Lang files are JSON rather than YAML

### Removed

- Fabric API, Forgified Fabric API, CICADA, Mod Menu and the Fabric permissions API, with nothing in their place
- Controller support, since upstream's Controlify integration is a Fabric entrypoint and was already missing from its NeoForge build

[Unreleased]: https://github.com/aspctt/do-a-barrel-roll-neoforge/compare/3.7.4...HEAD
[3.7.4]: https://github.com/aspctt/do-a-barrel-roll-neoforge/compare/3.7.3...3.7.4
[3.7.3]: https://github.com/aspctt/do-a-barrel-roll-neoforge/releases/tag/3.7.3

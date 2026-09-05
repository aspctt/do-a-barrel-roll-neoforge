<h1 style="text-align: center;"> Do Another Barrel Roll </h1>

<p style="text-align: center;">
	<img src="https://img.shields.io/badge/Available_for-1.21.1-blue" alt="Version">
	<img src="https://img.shields.io/badge/Requires-Nothing-brightgreen" alt="Requires">
	<img src="https://img.shields.io/badge/License-GPL--3.0--only-red" alt="License">
</p>

<p style="text-align: center;">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg" alt="NeoForge">
	<img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/forge_vector.svg" alt="Forge">
</p>

<p style="text-align: center;">
	<a href="https://github.com/aspctt/do-a-barrel-roll-neoforge"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/github_vector.svg" alt="Available on GitHub"></a>
	<a href="https://modrinth.com/mod/do-another-barrel-roll"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/modrinth_vector.svg" alt="Available on Modrinth"></a>
	<a href="https://www.curseforge.com/minecraft/mc-mods/do-another-barrel-roll"><img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact-minimal/available/curseforge_vector.svg" alt="Available on CurseForge"></a>
</p>

<p><strong>A native NeoForge port of enjarai's Do a Barrel Roll.</strong></p>

<p>Do a Barrel Roll is a lightweight, mostly clientside mod that changes elytra flight to be more fun and semi-realistic. It redesigns movement around a completely unlocked camera, giving you full pitch, yaw and roll control in flight, along with camera modifiers like smoothing and banking.</p>

<h3>Why this version exists</h3>

<p>The official NeoForge build is the Fabric jar running through Sinytra Connector, so it needs both Connector and the Forgified Fabric API before it will start. This port is written against NeoForge directly, with no Connector, no Fabric API, no CICADA, no Mod Menu and no Fabric permissions API. Nothing is required beyond NeoForge itself.</p>

<p>Behaviour is unchanged. Configs, keybinds, permission nodes and the server handshake keep their original names, so an existing config file carries straight over.</p>

<h3>Controls</h3>

<p>Mouse x axis rolls, mouse y axis pitches, and the strafe keys yaw. All rebindable. The flight bindings sit in their own key conflict context, so putting yaw on A and D does not conflict with vanilla strafing: they are only live while you are flying.</p>

<h3>Configuration</h3>

<p>Install <a href="https://modrinth.com/mod/yacl">YACL</a> and the config screen appears behind the Config button next to the mod in NeoForge's mod list. YACL is optional; without it the mod runs the same and the button offers to install it.</p>

<h3>Server-side features</h3>

<p>Install it on the server as well and playermodel roll is synced between clients. Vanilla clients and vanilla servers stay fully compatible either way. Operators get a Server tab covering thrusting, forced activation, forced installation and kinetic damage, and both permission nodes are registered with NeoForge's permission API.</p>

<h3>Requirements</h3>

<p>Minecraft 1.21.1 and NeoForge 21.1.249 or newer. YACL 3.6.0 or newer is optional, for the config screen.</p>

<h3>License</h3>

<p>GPL-3.0-only, the same licence as upstream. The full terms are in <a href="https://github.com/aspctt/do-a-barrel-roll-neoforge/blob/main/LICENSE">LICENSE</a>.</p>

<p>Do a Barrel Roll is by <a href="https://github.com/enjarai">enjarai</a>, based on <a href="https://github.com/Jorbon/cool_elytra">Cool Elytra Roll</a> by Jorbon. Mod icon by Mizeno. Native NeoForge port by aspctt.</p>
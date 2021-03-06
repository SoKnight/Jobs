# Jobs
Just another custom jobs plugin, written from nothing.

Plugin is compatible with Spigot 1.13+.
See [wiki](https://github.com/SoKnight/Jobs/wiki) for more information.

## Features
- Integration with WorldGuard: Create regions for job's workspace, manage WG flags and configure internal/external points for teleport (see below)
- Hooking into PAPI: Use placeholders for collecting and getting information about workers and use it into menus.
- Customizable jobs salaries, boosts from materials and permissions, max levels, per-level exp boost, default exp and more.
- Experience and level system. Higher level - higher salary.
- Auto-regeneration of blocks and auto-respawning of animals info workspaces.
- Admin tools for adding and removing blocks per everyone job.
- Messages prefix, all messages customizable in messages.yml.
- Some messages will send to player's actionbar without polluting the chat (configurable).
- Using SQLite and MySQL (second not tested) database for workers profiles storage.

## Requirements
- [SKLibrary](https://github.com/SoKnight/SKLibrary)
- [PEconomy](https://github.com/SoKnight/PEconomy)
- WorldGuard plugin + WorldEdit for him
- PlaceholdersAPI (soft-depend, if you want use placeholders from plugin)

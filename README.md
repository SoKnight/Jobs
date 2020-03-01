# Jobs
Just another custom jobs plugin, written from nothing.

See config in config.yml file and messages in messages.yml file.
Plugin use Paper API for MC 1.14.4 (you can use spigot).
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
- WorldGuard plugin + WorldEdit for him
- PEconomy (my plugin, search in my repositories)
- PlaceholdersAPI (soft-depend, if you want use placeholders from plugin)

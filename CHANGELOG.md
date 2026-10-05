# Changelog

## [1.1.1]

## Config

- **Afk in config** - Added `afk` commant to the config file, allowing server administrators to configure AFK is enabled or disabled on server. This provides flexibility for server owners to manage player activity and ensure a smooth gaming experience. 

### Chat & Formatting

- **LuckPerms Hex Color Code Support** — full support for hex color codes in LuckPerms usernames, prefixes, and suffixes across chat formatting, tab list display, above-head scoreboard nametags, and MKT placeholders. Supports `&#RRGGBB`, `§#RRGGBB`, BungeeCord/Spigot `&x&r&r&g&g&b&b` / `§x§r§r§g§g§b§b`, and MiniMessage `<#RRGGBB>` shorthand tags, ensuring gradient and custom-colored ranks and names render correctly in-game. 

## [1.1.0]

### Automation

- **Command scheduler (`/scheduler`)** — named schedulers in `config/mktessentials/scheduler.yml`, each with its own command list, selection mode and schedule. Intervals (`every: 5m`, or `5m-10m` for a random one), fixed times of day with weekday filters, a one-shot run after startup, and a `spread` that staggers the commands of a single run. Selection modes: `all`, `random` (never the same line twice in a row), `random:N`, `weighted`, `sequential` and `shuffle` (every command runs once before any repeats). Conditions gate a run on player count, a named player being online, someone being in a dimension, server uptime, a real-time window or an in-game time window — an unmet condition either skips the run or holds it until it is met. `/scheduler list|info|history|run|enable|disable|reload` inspect and drive them, and `/mkt reload` picks up file edits without a restart. Permissions: `mktessentials.admin.scheduler.view` / `.manage` (or `mktessentials.admin.scheduler.*`). Config: `commands.yml -> admin.scheduler`.

### Saved Items

- **Item storage (`/itemstore`)** — save exact copies of custom items into named categories and hand them out later. `/itemstore edit <category>` opens a chest: whatever is in it when you close it becomes the category, so adding, removing, replacing and reordering are all one GUI. Every component survives the round-trip — display name, lore, enchantments, attributes, custom model data and mod-added data. Per-item draw weights power crates and random rewards (`/itemstore giverandom`), items that do not fit drop at the player's feet, and `/itemstore announce` broadcasts the reward with the item's own hover tooltip. Combine with `/cmdqueue` to pay out to offline players. Permissions: `mktessentials.admin.itemstore.view` / `.edit` / `.give` (or `mktessentials.admin.itemstore.*`). Config: `commands.yml -> admin.itemstore`.

### Command Queue

- **Command queue (`/cmdqueue`)** — queue console commands to run for a player reliably: as soon as they are online, after a delay, or both. The queue survives a restart, so a purchase made while the server is down is still paid out on the next boot. Statuses (`pending`, `completed`, `failed`, `cancelled`), full history with who queued what and when, and `cancel` / `retry` / `remove` / `clear` for administrators. An optional `key` makes an entry idempotent so a webstore retry cannot grant the same reward twice. Permissions: `mktessentials.admin.queue.view` / `.add` / `.manage` (or `mktessentials.admin.queue.*`) (all level 4 — queueing a command equals console access). Config: `commands.yml -> admin.queue`.

- **Report log (`/reports`)** — `/helpop` and `/report` are now persisted to `<world>/mktessentials/reports.log` instead of only reaching the console, and staff can page through them in game with clickable navigation. Permission: `mktessentials.admin.helpop`.

## [1.0.0]

A large chat, cosmetics and integration release. MKT Essentials is now fully self-sufficient: the tab list, nametags, MOTD and placeholders all work with no companion mods, while optional hooks light up when other mods are present. Chat gains rich formatting, interactive elements and a Discord bridge.

### Chat & Formatting

- **Nicknames (`/nick`)** — set a display nickname that now shows **everywhere**: in chat, in the tab list, and above the player's head (the skin is preserved). Staff with `mktessentials.nick.see` see the real name on chat hover. Config: `chat.yml → nickname`.

- **Markdown-style formatting** — write `*italic*`, `**bold**`, `__underline__`, `~~strikethrough~~` and `??matrix??` (obfuscated) directly in chat. Works in chat and on anvil/book/sign text. Config: `chat.yml → markdown`.

- **Inline objects** — `[item]` inserts the item you're holding, with its full tooltip on hover; `<head>` inserts your player head. Config: `chat.yml → objects`.

- **Interactive replacements** — URLs become clickable links, `:emoji:` shortcodes expand, and `||spoiler||` renders as hover-to-reveal blocks. Config: `chat.yml → replacement`.

- **Mentions** — type `@player` to highlight and ping them (with a sound). Recipients can opt out with `/mentions`. Config: `chat.yml → mention`.

- **Per-player chat colour (`/chatcolor`)** — pick a personal base colour for your messages. Config: `chat.yml → chatcolor`.

- **Chat settings GUI (`/chatsetting`)** — a clickable menu to toggle your personal chat options (mentions, etc.), plus an ignore-list menu.

- **Local / per-world chat** — messages reach only nearby players (configurable radius) or the current world, with a global prefix to broadcast server-wide. Config: `chat.yml → chat-local`.

- **Question highlighting** — messages ending in a question are visually emphasised. Config: `chat.yml → questionanswer`.

- **Anonymous chat (`/anon`)** and **stream announcements (`/stream`)** — send a message without your name attached, or announce a stream with a clickable link. Config: `chat.yml → anon`, `stream`.

- **Symbols (`/symbol`)** — insert glyphs/special characters into chat.

- **Coloured anvil, book and sign text** — legacy `&` codes, MiniMessage and markdown now apply to renamed items in an anvil, text written in books, and text on signs. Config: `chat.yml → objects` toggles `anvil-color`, `book-color`, `sign-color`.

### Cosmetics & Display

- **Native tab list** — configurable header/footer with placeholders and frame animations, plus an optional latency/ping display. Config: `chat.yml → tablist`.

- **Native above-head nametags** — rank prefix/suffix rendered above players via scoreboard teams (nickname-aware). Config: `chat.yml → nametag`.

- **Below-name scoreboard** — show a number or health value beneath player nametags in the world. Config: `chat.yml → belowname`.

- **Sidebar scoreboard** and **boss bar** — a configurable side panel and a top boss bar, both with placeholders and animation support. Config: `chat.yml → sidebar`, `bossbar`.

- **Server brand (F3)** — customise the server name shown on the F3 debug screen, with optional rotation between multiple texts. Config: `chat.yml → brand`.

- **Welcome greeting with skin face** — a personalised join greeting (first-join vs returning) that can render the player's actual skin face as pixel art in chat. Uses the player's name so it works in offline mode. Config: `chat.yml → greeting`.

- **MOTD & favicon override** — set the server-list description and icon directly in the mod, with a separate maintenance MOTD. Config: `chat.yml → motd`.

### New Commands & Features

- **Mail (`/mail`)** — send and read offline mail; unread mail is announced on join.

- **Polls (`/poll`)** — create and vote in server polls.

- **Roleplay (`/me`, `/do`)** — action/narration messages.

- **Maintenance mode (`/maintenance`)** — put the server in maintenance with its own MOTD; non-exempt players are kept out.

- **Right-click player interactions**, **`/online`** (formatted online list), and **social commands** for managing chat relationships.

- **Fun commands** — `/dice`, `/coin` and similar.

- **`/geolocate <player>`** — look up the approximate location of a connected IP (staff tool).

### Integrations

- **Optional mod hooks** (auto-detected, toggles in `integration.yml → mods`):
  - **TAB** — when installed, the mod's native tab list, nametags and ping display step aside so TAB manages them.
  - **MiniMOTD** — when installed, the mod's MOTD override steps aside.
  - **SkinsRestorer** — the greeting face uses the player's SkinsRestorer skin.
  - **Plasmo Voice** & **Simple Voice Chat** — a player muted in-game is silently muted in voice chat too.

- **2-way Discord chat relay** — bridge a Discord channel with in-game chat. In-game messages (and optional join/leave notices) post to Discord — as plain bot messages or, with a webhook, showing each player's name and avatar — while channel messages appear in-game. Anti-`@everyone` protection and colour-code sanitisation apply in both directions. The bot status shows the live online player count. Config: `integration.yml → discord.relay`. Requires the KotlinForForge mod and the bot's *Message Content Intent*.

### Moderation

- **Shadowban "phantom" full isolation** — with `moderation.shadowban-method: phantom`, a shadowbanned player joins into an empty world: they see no other players (entities and tab list), no one sees them, they receive no one's chat, no join/leave spam is generated, and — with a voice-chat mod hooked — their microphone is silently dropped. Mobs and items remain visible.

### Improvements

- **Fully self-sufficient** — no companion mod is required. The tab list, nametags and MOTD are native, and MKT placeholders (`%mktessentials:*%`) resolve internally; the Text Placeholder API is used only to expose them to other mods when present.

- **Configuration reorganised by concern** — chat, social and cosmetic settings live in `chat.yml`; external mod/service hooks live in `integration.yml`. Existing setups are migrated automatically.

### Bug Fixes

- **`/me` no longer collides with vanilla** — the vanilla `/me` is removed so the mod's roleplay command handles it correctly.

- **`/tp` supports selectors and all forms again** — `@p`/`@e`/`@s`/`@a`, player→player, player→coordinates, and coordinate targets all work, mirroring vanilla `/teleport`.

- **`/mkt reload` no longer crashes on a type mismatch** — a mistyped config value now logs a warning and falls back to the default instead of throwing.

- **The Discord bot starts whenever Discord is enabled**, independent of the auth system, and fails softly (server still starts) with a clear hint when its Kotlin dependency is missing.

### Notes

- Backward compatible: existing configs are migrated, and every new feature ships disabled or with safe defaults.

## [0.4.0]

### New Features

- **Per-rank home limits via dynamic LuckPerms permissions** — set the maximum number of homes per rank with `mktessentials.homes.<number>` (the highest granted number wins, e.g. `mktessentials.homes.10`). `mktessentials.homes.*` or `mktessentials.homes.unlimited` grant unlimited homes. Resolution order: dynamic permission → `mktessentials.max_homes` meta → config `general.max-homes`.

- **Separate teleport cooldowns for TPA, RTP and Warps** — each teleport type now runs on its own independent cooldown clock, so using `/tpa` no longer blocks `/rtp` or `/warp`. New config keys under `teleportation`: `cooldown-tpa`, `cooldown-rtp`, `cooldown-warp` (`-1` = inherit the global `cooldown`). Per-rank overrides via LuckPerms meta `mktessentials.teleport_cooldown.<type>` (`tpa`/`rtp`/`warp`), with the legacy `mktessentials.teleport_cooldown` still applying to all types.

### Notes

- Fully backward compatible — existing configs and the global `teleportation.cooldown` behave as before.

## [0.3.0]

### New Features

- **`/back` now works after death** — your death location is saved automatically, and a hint message tells you that `/back` will return you there.

- **`/setspawn`** — set a custom server spawn point at your current location. `/spawn` teleports to it instead of the world spawn (stored in `spawn.json`).

- **`/afk`** — manually toggle AFK status. Moving, chatting, or running `/afk` again clears it.

- **`/tpacancel` and `/tptoggle`** — cancel your outgoing teleport requests, or block incoming ones entirely. Staff with `mktessentials.admin.tptoggle.bypass` can still send you requests.

- **`/msgtoggle` and `/ignore <player>`** — block all private messages, or ignore a specific player (hides both their chat messages and their `/msg`). Staff with `mktessentials.admin.msgbypass` bypass these.

- **Warn system: `/warn`, `/unwarn`, `/warns`, `/history`** — warn players with a reason and browse a full punishment history (warns, bans, tempbans, mutes, kicks are all recorded in `punishments.json`). Reaching the configurable warn limit (`moderation.max-warns`, default 3) automatically tempbans the player for `moderation.warn-ban-duration` (default 1d).

- **`/tempmute <player> <duration> [reason]`** — timed mute with a required duration and an optional reason, recorded in the punishment history.

- **IP bans: `/banip <player|ip> [reason]` and `/unbanip <ip>`** — ban by player name (online or offline, using their last known IP) or by literal IP. Everyone connected from that IP is kicked immediately, and banned IPs are rejected at login (stored in `ipbans.json`).

- **`/whois <player>`** — staff overview of a player: UUID, nickname, IP, ping, gamemode, location, health/food, fly/god/vanish status, play time, first join, and mute status. Works for offline players too.

- **`/playtime [player]`** — total play time, tracked per session from now on.

- **`/trash`** — a disposal chest; items left inside are destroyed when you close it.

- **Virtual workstations: `/workbench` (alias `/craft`), `/anvil`, `/grindstone`, `/stonecutter`, `/smithing`** — open the vanilla menus anywhere, no block required.

- **`/exp give|set <player> <levels>`** — manage player experience levels.

- **`/ptime` and `/pweather`** — per-player, client-side time (`day`, `noon`, `night`, `midnight`, exact ticks, `reset`) and weather (`clear`, `rain`, `reset`). Affects only what you see; the server stays untouched.

- **Kits and warps GUI** — `/kits` opens a clickable menu showing each kit's icon, item count, cooldown, and ready status (click to claim). `/warps` opens a clickable warp list (click to teleport).

- **Kit creation GUI** — `/createkit <name> <cooldown>` now opens a chest: place the kit contents, close it, done — your items are given back. The old behavior (snapshot of your inventory) is available as `/createkit <name> <cooldown> frominv`.

- **`/helpop <message>` and `/report <player> <reason>`** — contact online staff (anyone with `mktessentials.admin.helpop`). Messages are also logged to the console, with a 30-second cooldown for regular players.

- **`/tps` and `/lag`** — server performance at a glance: TPS, MSPT, memory usage, player count, and loaded chunks/entities per dimension.

- **Configurable text commands** — define your own info commands in `messages.yml` (`text-commands` section), each with custom aliases and message lines. Ships with `/rules` (alias `/zasady`), `/www`, `/vote`, and `/discordinvite` (alias `/dc`) as editable examples.

### Improvements

- **`/mkt help` is now complete and accurate** — all commands added in this release are listed, plus previously missing ones (`/socialspy`, `/broadcast`, `/kickme`, `/noon`, `/midnight`, `/storm`, `/createkit`, `/deletekit`). A new **Account** section appears when the auth system is enabled, showing `/register`, `/login`, `/changepassword`, `/link`, `/unlink`, `/discord` based on the configured auth mode. Text commands are listed dynamically from the config, so renamed aliases (e.g. `/zasady`) show up correctly. Also fixed: the help claimed `/homes` exists — the actual command is `/listhomes`.

- **`/mkt permissions` lists all new permission nodes** — moderation nodes (`mktessentials.moderation.warn`, `.banip`, `.history`), admin nodes (`admin.whois`, `admin.exp`, `admin.lag`, `admin.setspawn`, `admin.helpop`, `admin.msgbypass`, `admin.tptoggle.bypass`), and player nodes for all new commands (including `command.text.<name>` for text commands).

- **Auto-broadcasts** — new `broadcast.enabled` toggle in `messages.yml`, and broadcasts now pause while the server is empty instead of firing the moment the first player joins.

- **New commands can be toggled** in `commands.yml`: `warn`, `banip`, `exp`, `whois`, `playtime`, `stations`, `ptime`, `helpop`, `tps`.

### Bug Fixes

- **Security: Discord linking now runs on the server thread** — completing a `/link` from Discord previously executed database queries and player state changes on the bot's thread, which could corrupt data or crash. It is now safely scheduled on the main thread.

- **Security: `/unlink` (and `/auth unlink`) now invalidates the session** — previously a relog after unlinking would auto-authenticate the player and bypass the Discord link requirement.

- **Security: failed login attempts are now counted per IP** in a 10-minute window — previously disconnecting and rejoining reset the counter, making the max-attempts limit useless against brute force.

- **Security: changing your password now invalidates old sessions** — a session created before the password change (e.g. by someone who knew the old password) no longer stays valid.

- **Players are now frozen immediately on join** when authentication is required — previously there was a 1-tick window before the freeze applied.

- **Kicks for too many login attempts show the correct message** — previously the "took too long to log in" message was shown (new lang key: `auth.kicked-max-attempts`).

- **Corrupted or empty player data files no longer break joining** — the mod now falls back to fresh data instead of caching `null` or crashing the join handler.

- **AFK announcements no longer reveal vanished or shadowbanned players.**

- **Auth database failures no longer cause crashes** — if the database fails to initialize, queries are skipped with a logged error instead of throwing.

- **Teleport cooldowns are cleaned up** when they expire and when players disconnect (slow memory leak fix), and chunk pre-loading now targets the correct chunk at negative coordinates.

- **Ban/mute durations are protected against overflow** — absurdly large values now show an error instead of silently producing an already-expired ban.

- **Items no longer become permanently undespawnable** after the item despawn feature is disabled in the config.

- **Player data is explicitly saved on server shutdown**, and data modified for offline players (e.g. offline mutes) is saved and released from memory immediately.

## [0.2.1]

### Bug Fixes

- **`/speed` command is now always available** — previously, the command would silently stop working if `/heal`, `/fly`, and `/god` were all disabled in the config. It now registers independently regardless of other commands being toggled off.

- **`/speed` values now make sense** — value `1` is the default (vanilla) speed for both walking and flying. The minimum allowed value is `1` (previously `0`, which would completely freeze the player).

- **`/speed` changes now persist across reconnects** — your custom walk and fly speed are saved and automatically restored when you rejoin the server. Previously they would reset every time you disconnected.

- **`/speed fly` now warns you if the target can't fly** — if the player doesn't have flight enabled (and isn't in Creative/Spectator), you'll get a notice that the speed was saved but won't take effect until flight is turned on.

- **Join/quit messages now work without PlaceholderAPI** — if the PlaceholderAPI mod is not installed on the server, join and leave messages will correctly show the player's name instead of displaying a raw placeholder like `%mktessentials:full_name/safe%`.

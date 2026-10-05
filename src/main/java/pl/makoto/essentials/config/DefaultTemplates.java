package pl.makoto.essentials.config;

/**
 * Contains static String constants with the full default YAML content (including comments)
 * for each configuration file. These are written verbatim to disk on first run.
 * Since SnakeYAML strips comments on write, we use pre-authored template strings.
 */
final class DefaultTemplates {

    private DefaultTemplates() {
        // Utility class — no instantiation
    }

    static final String SETTINGS_YML = """
            # ============================================
            #  MKT Essentials - Settings
            # ============================================
            # Main configuration file for MKT Essentials.
            # Edit values below to customize the plugin.
            # Changes require /mkt reload or server restart.

            # Language for server messages (file must exist in lang/ folder)
            # Available: en_us, pl_pl (or add your own)
            language: "en_us"

            # ============================================
            #  General Settings
            # ============================================
            general:
              # Prefix shown before all mod messages (supports & color codes)
              message-prefix: "&8[&6MKT&8] &r"
              # Maximum homes per player. Override per rank with either:
              #   - dynamic permission: mktessentials.homes.<number> (highest granted wins, e.g. mktessentials.homes.10);
              #     mktessentials.homes.* or .unlimited grants unlimited homes
              #   - LuckPerms meta: mktessentials.max_homes
              max-homes: 3

            # ============================================
            #  Teleportation
            # ============================================
            teleportation:
              # Delay in seconds before teleport executes (0 = instant)
              delay: 3
              # Cooldown between teleports in seconds (0 = no cooldown). This is the global/default cooldown.
              cooldown: 10
              # Per-type cooldown overrides (-1 = inherit the global "cooldown" above).
              # Each teleport type runs its own independent cooldown clock, so /tpa does not block /rtp.
              # Can also be overridden per rank with LuckPerms meta:
              #   mktessentials.teleport_cooldown.tpa / .rtp / .warp (or mktessentials.teleport_cooldown for all types)
              cooldown-tpa: -1
              cooldown-rtp: -1
              cooldown-warp: -1
              # Play sound and particle effects on teleport
              effects: true
              # Seconds before a TPA request expires
              tpa-timeout: 60

            # ============================================
            #  Random Teleport (RTP)
            # ============================================
            rtp:
              # Minimum distance from center/player
              min-distance: 500
              # Maximum distance from center/player
              max-distance: 5000
              # true = distance relative to player, false = relative to center coordinates
              relative-to-player: true
              # Center coordinates (used when relative-to-player is false)
              center-x: 0.0
              center-z: 0.0
              # Biomes to exclude from RTP (players won't land in these)
              biome-blacklist:
                - "minecraft:ocean"
                - "minecraft:deep_ocean"
                - "minecraft:river"

            # ============================================
            #  AFK Settings
            # ============================================
            afk:
              # Enable or disable the AFK system and command
              enabled: true
              # Seconds of inactivity before marking player as AFK (0 = disabled)
              timeout: 300

            # ============================================
            #  Data Management
            # ============================================
            data:
              # Interval in seconds between automatic player data saves (0 = disabled)
              auto-save-interval: 300

            # ============================================
            #  Vanish Settings
            # ============================================
            vanish:
              # Send fake join/quit messages when admins vanish/unvanish
              fake-messages: true

            # ============================================
            #  Inventory Backups
            # ============================================
            backup:
              # Automatically backup inventory on player death
              on-death: true
              # Automatically backup inventory when player joins the server
              on-join: false
              # Automatically backup inventory when player leaves the server
              on-quit: true
              # Interval in seconds between scheduled backups for all online players (0 = disabled)
              interval: 0
              # Maximum number of backups to keep per player (oldest are deleted)
              max-per-player: 10

            # ============================================
            #  Authentication (login / register)
            # ============================================
            # Discord link and web-sync now live in integration.yml.
            auth:
              # Mode: "full", "auth-only", "link-only", "optional", "disabled"
              mode: "disabled"
              # Hours before a session expires (auto-login from same IP)
              session-timeout-hours: 24
              # Maximum failed login attempts before kick
              max-login-attempts: 5
              # Seconds before an unauthenticated player is kicked
              login-timeout-seconds: 60
              # Minutes of invulnerability for first-time players (0 = disabled)
              newbie-protection-minutes: 30

            # ============================================
            #  Shadowban
            # ============================================
            moderation:
              # Shadowban method: "timeout", "full", "internal-error", "phantom"
              # timeout: Shows "Connection timed out" on join
              # full: Shows "Server is full!" on join
              # internal-error: Kicks after 2-3 seconds with fake internal error
              # phantom: Full isolation. The player joins into an empty world — they see NO other
              #          players (neither in-world entities nor the tab list) and no one sees them.
              #          Their chat is echoed only back to themselves; they receive no one else's
              #          chat; no join/quit spam; and with a voice-chat mod + mute-sync hook on,
              #          their microphone is silently dropped too. Mobs/items stay visible.
              shadowban-method: "timeout"
              # Active warns (/warn) that trigger an automatic tempban (0 = no escalation)
              max-warns: 3
              # Tempban duration applied when the warn limit is reached (e.g. 1d, 12h, 30m)
              warn-ban-duration: "1d"

            # ============================================
            #  Item Management
            # ============================================
            items:
              # Custom despawn time for ground items in seconds (0 = use vanilla behavior)
              despawn-time: 300
              # Merge nearby identical items into stacks to reduce entity count
              stacking: true
              # Radius in blocks to search for stackable items
              stacking-radius: 3
              # Maximum stack size for ground item merging (64 = vanilla, set higher to merge more into one entity)
              max-stack-size: 64
              # Show floating hologram above items with name and countdown
              show-hologram: true
              # Global sweep interval in seconds (0 = disabled, only individual timers)
              sweep-interval: 0
              # Seconds before sweep to broadcast warning
              sweep-warning: 30
              # Items that never despawn (supports modded items, e.g., "create:brass_ingot")
              whitelist:
                - "minecraft:netherite_sword"
                - "minecraft:netherite_pickaxe"
                - "minecraft:netherite_axe"
                - "minecraft:netherite_shovel"
                - "minecraft:netherite_hoe"
                - "minecraft:netherite_helmet"
                - "minecraft:netherite_chestplate"
                - "minecraft:netherite_leggings"
                - "minecraft:netherite_boots"
                - "minecraft:elytra"
                - "minecraft:shulker_box"
                - "minecraft:totem_of_undying"
            """;

    static final String CHAT_YML = """
            # ============================================
            #  MKT Essentials - Chat, Social & Presentation
            # ============================================
            # Everything related to chat, social features and how the server presents itself:
            # nicknames, mentions, moderation, links, tab list, nametags, boss bar, MOTD, roleplay,
            # mail and maintenance. Gameplay settings live in settings.yml.
            # Changes require /mkt reload or server restart.

            # ============================================
            #  Nicknames (/nick)
            # ============================================
            nickname:
              # Allow players to change their nickname with /nick
              enabled: true
              # Minimum / maximum VISIBLE length (color & format codes are not counted)
              min-length: 1
              max-length: 16
              # Optional regex the VISIBLE nickname must match (colors stripped).
              # Empty = no restriction. Example (letters, digits, underscore, 3-16 chars):
              #   allowed-pattern: "^[A-Za-z0-9_]{3,16}$"
              allowed-pattern: ""

            # ============================================
            #  Chat Mentions (@name)
            # ============================================
            # Ping other players in chat by typing @<their name or nickname>.
            # The mention is highlighted and the mentioned player hears a sound.
            mention:
              enabled: true
              # Color for the highlighted @mention: a named color (aqua, gold, ...) or hex (#55FFFF)
              color: "aqua"
              # Sound played to the mentioned player (namespaced sound id, empty = no sound)
              sound: "minecraft:block.note_block.pling"
              # Allow players to mention (ping) themselves
              self: false

            # ============================================
            #  Chat Replacements (links / spoilers)
            # ============================================
            replacement:
              enabled: true
              # Turn http(s) links into clickable links
              url: true
              # ||text|| becomes an obfuscated spoiler
              spoiler: true
              # Color for clickable links
              url-color: "blue"

            # ============================================
            #  Inline chat objects (no resource pack)
            # ============================================
            # [item] inserts the item you're holding (with its tooltip on hover);
            # <head> inserts your player head. Text between them is formatted normally.
            objects:
              enabled: true

            # ============================================
            #  Markdown-style chat formatting
            # ============================================
            # *italic*  **bold**  __underline__  ~~strikethrough~~  ??matrix?? (obfuscated)
            markdown:
              enabled: true

            # ============================================
            #  Per-player Chat Color (/chatcolor)
            # ============================================
            chatcolor:
              enabled: true

            # ============================================
            #  Color codes on items / books / signs
            # ============================================
            # Allow & color/format codes (permission-filtered) when renaming items in an anvil,
            # writing books, and editing signs.
            text-colors:
              anvil: true
              book: true
              sign: true

            # ============================================
            #  Stream announce (/stream <url>)
            # ============================================
            stream:
              enabled: true
              announce-format: "&d&l⭐ &e%mktessentials:name% &7is now streaming! &b{url}"

            # ============================================
            #  Anonymous chat (/anon <message>)
            # ============================================
            anon:
              enabled: true
              format: "&8[&7Anon&8] &f{message}"

            # ============================================
            #  Question highlight
            # ============================================
            # Colors the base text of a message that ends with '?'.
            questionanswer:
              enabled: false
              color: "yellow"

            # ============================================
            #  Local (range-based) Chat
            # ============================================
            # When enabled, chat is scoped so it doesn't reach the whole server.
            # mode "range" = only players within "radius" blocks (same dimension);
            # mode "world" = everyone in the same dimension (per-world chat).
            # Start a message with the global prefix to send it to everyone.
            # Tip: put %mktessentials:world% in the chat format (messages.yml) to show the world.
            chat-local:
              enabled: false
              mode: "range"
              radius: 100
              global-prefix: "!"

            # ============================================
            #  Emoji / Symbol Shortcuts
            # ============================================
            # Typing the code inserts the symbol in chat.
            emojis:
              ":heart:": "❤"
              ":star:": "★"
              ":check:": "✔"
              ":cross:": "✖"
              ":arrow:": "➤"
              ":skull:": "☠"
              ":note:": "♪"
              ":sun:": "☀"

            # ============================================
            #  Named Animations (<animation:name>)
            # ============================================
            # Reusable animations you can embed anywhere with <animation:name> — in the tab
            # header/footer, boss bar, MOTD, chat/greeting formats, etc. The frame advances every
            # "interval" ticks (20 = 1 second). Unknown names render as empty.
            animations:
              loading:
                interval: 10
                frames:
                  - "&7Loading&8."
                  - "&7Loading&8.."
                  - "&7Loading&8..."
              title:
                interval: 8
                frames:
                  - "&c&lMKT"
                  - "&6&lMKT"
                  - "&e&lMKT"
                  - "&a&lMKT"
                  - "&b&lMKT"
                  - "&d&lMKT"

            # ============================================
            #  Chat Moderation (anti-spam / caps / swear)
            # ============================================
            # Players with mktessentials.chat.moderation.bypass (default OP) skip all filters.
            chat-moderation:
              enabled: true
              # Excessive CAPS filter
              caps:
                enabled: true
                # Only check messages at least this many characters long
                min-length: 8
                # Block/fix messages whose letters are more than this percent uppercase
                max-percent: 70
                # "lowercase" = convert to lowercase, "block" = reject the message
                mode: "lowercase"
              # Anti-flood / anti-spam
              flood:
                enabled: true
                # Minimum seconds between two messages from the same player
                cooldown-seconds: 1.5
                # Block sending the exact same message twice in a row
                block-duplicate: true
                # Collapse runs of the same character longer than this (0 = disabled), e.g. "aaaaaa" -> "aaaa"
                max-repeated-chars: 4
              # Swear/word filter (fill in your own word list; empty = disabled)
              swear:
                enabled: false
                # "censor" = replace with the censor char, "block" = reject the message
                mode: "censor"
                censor-char: "*"
                words: []
              # New-player restrictions (based on total playtime)
              newbie:
                enabled: false
                min-playtime-minutes: 10
                # Block links from players below the playtime threshold
                block-links: true

            # ============================================
            #  Tab List (header / footer)
            # ============================================
            # Native tab list — no external TAB mod required.
            # Player names in the list are formatted by the chat name settings; here you set the
            # header/footer. Placeholders (%mktessentials:name% etc.) and {online}/{max} are supported.
            tablist:
              enabled: true
              # How often (in ticks, 20 = 1s) the header/footer is refreshed
              update-interval: 40
              header:
                - "&6&lMKT Server"
                - ""
              footer:
                - ""
                - "&7Players online: &f{online}&7/&f{max}"
              # Show each player's ping as a number next to their name in the tab list
              ping-number:
                enabled: false
                update-interval: 40
              # Animated header/footer: cycle through frames (each frame is a list of lines).
              # When enabled, these replace the static header/footer above.
              animation:
                enabled: false
                # Ticks between frames (20 = 1 second)
                interval: 20
                header:
                  - ["&6&lMKT Server", "&7Welcome!"]
                  - ["&6&lMKT Server", "&aHave fun!"]
                footer:
                  - ["", "&7discord.gg/example"]
                  - ["", "&7Players online: &f{online}&7/&f{max}"]

            # ============================================
            #  Nametags (above the head)
            # ============================================
            # Native rank prefix/suffix above players' heads via scoreboard teams
            # (uses LuckPerms prefix/suffix). No external TAB mod required.
            nametag:
              enabled: true

            # ============================================
            #  Below-name number (under the head, in the world)
            # ============================================
            belowname:
              enabled: false
              # "health" or "ping"
              type: "health"
              # Text shown after the number (e.g. a heart)
              suffix: "&c❤"
              update-interval: 40

            # ============================================
            #  Sidebar (scoreboard side panel)
            # ============================================
            # Server-wide panel. {online}/{max} supported; per-player placeholders are NOT
            # (one objective is shared by everyone).
            sidebar:
              enabled: false
              update-interval: 40
              title: "&6&lMKT SERVER"
              lines:
                - "&7Welcome!"
                - "&7Online: &f{online}&7/&f{max}"

            # ============================================
            #  Boss Bar
            # ============================================
            # A persistent boss bar shown to every player. Supports placeholders.
            bossbar:
              enabled: false
              text: "&6Welcome to the server, %mktessentials:name%!"
              # Colors: pink, blue, red, green, yellow, purple, white
              color: "purple"
              # Overlay: progress, notched_6, notched_10, notched_12, notched_20
              overlay: "progress"
              update-interval: 40

            # ============================================
            #  Server Brand (F3 debug screen)
            # ============================================
            # Overrides the "Server Brand" line in the F3 screen. Multiple texts rotate.
            brand:
              enabled: false
              # Ticks between rotations (only when more than one text)
              update-interval: 100
              texts:
                - "&bMKT &fEssentials"

            # ============================================
            #  Server List MOTD
            # ============================================
            # Overrides the message shown in the multiplayer server list.
            # Disabled by default so it doesn't replace your server.properties MOTD unexpectedly.
            # Supports & color codes and MiniMessage. Up to 2 lines are shown.
            motd:
              enabled: false
              lines:
                - "&6MKT Server &8» &7Welcome!"
                - "&aRunning MKT Essentials"
              # Custom server-list icon: file name of a 64x64 PNG placed in
              # config/mktessentials/icon/ (empty = keep vanilla server-icon.png). e.g. "server.png"
              icon: ""

            # ============================================
            #  Right-click Player Info
            # ============================================
            # Right-clicking another player shows their info to you.
            rightclick:
              enabled: true
              # Require the player to be sneaking (shift) to trigger it
              require-sneak: true
              # Lines shown to the clicker. Placeholders resolve for the CLICKED player.
              format:
                - "&8&m                    "
                - " &6%mktessentials:full_name%"
                - " &7Real name: &f%mktessentials:real_name%"
                - "&8&m                    "

            # ============================================
            #  Roleplay (/me, /do, /try)
            # ============================================
            # {message} = the text the player typed, %mktessentials:name% = their (nick) name.
            roleplay:
              enabled: true
              me-format: "&d* %mktessentials:name% &f{message}"
              do-format: "&d* &f{message}"
              # {result} is replaced by try-success or try-fail (50/50)
              try-format: "&d* %mktessentials:name% &7tries to &f{message}&7 and {result}."
              try-success: "&asucceeds"
              try-fail: "&cfails"

            # ============================================
            #  Mail (offline messages)
            # ============================================
            mail:
              enabled: true
              # Maximum stored messages per player (oldest dropped past this)
              max-per-player: 30

            # ============================================
            #  Maintenance Mode (/maintenance on|off)
            # ============================================
            # When active, only players with mktessentials.maintenance.bypass can join.
            maintenance:
              # Start the server already in maintenance mode
              enabled-on-start: false
              # Kick/deny message (supports \\n for new lines and & colors)
              kick-message: "&cThe server is under maintenance.\\n&7Please check back later."
              # Shown automatically in the server list while maintenance is active (up to 2 lines)
              motd:
                - "&c&lUNDER MAINTENANCE"
                - "&7We'll be back soon!"
              # Icon shown automatically during maintenance: file name of a 64x64 PNG in
              # config/mktessentials/icon/ (empty = keep the normal icon)
              icon: ""

            # ============================================
            #  Greeting (personal welcome for the joining player)
            # ============================================
            # Shown only to the joining player. Lines containing the [#][#][#][#][#][#][#][#] marker
            # are replaced with the player's skin face (colored pixels). Placeholders supported.
            greeting:
              enabled: true
              # "chat", "actionbar", or "title" (skin face only works in "chat")
              type: "chat"
              # Render the player's skin face in place of the [#][#][#][#][#][#][#][#] markers
              skin-face: true
              # Avatar API returning the small face image. <name> = account name (works offline too),
              # <uuid> = the player's UUID. Using <name> is recommended for offline-mode servers.
              avatar-url: "https://mc-heads.net/avatar/<name>/8.png"
              first-join:
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]  &6&lHello,"
                - "[#][#][#][#][#][#][#][#]  &e%mktessentials:name%"
                - "[#][#][#][#][#][#][#][#]  &7Welcome to the server!"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
              returning:
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]  &6&lWelcome back,"
                - "[#][#][#][#][#][#][#][#]  &e%mktessentials:name%"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
                - "[#][#][#][#][#][#][#][#]"
              # Title timing (ticks), only used when type: title
              title-fade-in: 10
              title-stay: 60
              title-fade-out: 20
            """;

    static final String INTEGRATION_YML = """
            # ============================================
            #  MKT Essentials - Integrations
            # ============================================
            # Settings for integrations with other mods / services.
            #
            # Auto-detected (no configuration needed): LuckPerms (permissions & chat prefixes),
            # Text Placeholder API (exposes %mktessentials:*% to other mods) and Curios (/invsee
            # curio slots). They are used automatically when installed and safely ignored otherwise.

            # ============================================
            #  Discord bot (account linking)
            # ============================================
            discord:
              # Enable the embedded Discord bot
              enabled: false
              # Discord bot token (from the Discord Developer Portal)
              bot-token: ""
              # Guild (server) ID where the bot operates
              guild-id: ""
              # Name of the slash command for linking (language-dependent)
              link-command-name: "link"
              # Role ID to assign when a player links their account (empty = disabled)
              linked-role-id: ""
              # Show the online player count in the bot status
              show-player-count: true

              # 2-way chat bridge between a Discord channel and in-game chat (reuses the bot above).
              # Requires the bot's "MESSAGE CONTENT INTENT" enabled in the Discord Developer Portal.
              relay:
                enabled: false
                # The Discord channel ID to bridge (right-click channel → Copy ID; needs Developer Mode)
                channel-id: ""
                # Optional: post in-game messages through this webhook so they show the player's name and
                # avatar. Empty = the bot posts them as plain text. Create one in the channel settings.
                webhook-url: ""
                # <player>/<message> for MC→Discord; Discord markdown allowed
                format-to-discord: "**<player>**: <message>"
                # <author>/<message> for Discord→MC; & colour codes allowed (user text is not styled)
                format-from-discord: "&9[Discord] &b<author>&7: &f<message>"
                # Mirror join/leave to Discord
                announce-join-quit: true
                join-to-discord: "**<player>** joined the server"
                quit-to-discord: "**<player>** left the server"

            # ============================================
            #  Web sync (external backend / website panel)
            # ============================================
            # Mirror link/unlink events to an external backend (website panel / shared database).
            # Leave disabled unless you run such a backend.
            web-sync:
              enabled: false
              # Endpoint receiving POST {action,minecraftUuid,minecraftUsername,discordId,discordUsername,discordAvatar}
              url: ""
              # Sent as "Authorization: Bearer <secret>"
              secret: ""
              # On server start, push all currently linked accounts (idempotent upsert)
              backfill-on-start: false
              # Inbound HTTP channel: lets the backend tell the mod to unlink an account in-game
              api:
                enabled: false
                # Bind address — keep 127.0.0.1 if the backend runs on the same machine
                bind: "127.0.0.1"
                port: 8766
                # Bearer secret the backend must send. Empty = reuse web-sync.secret.
                secret: ""

            # ============================================
            #  Optional mod hooks (auto-detected)
            # ============================================
            # These hooks only activate when the matching mod is installed; they are safely
            # ignored otherwise. Each toggle lets you turn the hook off even when the mod is present.
            mods:
              # TAB — when installed, let TAB manage the player list & nametags
              # (MKT's native tablist/nametag/ping back off to avoid a double-render conflict).
              tab:
                enabled: true
              # MiniMOTD — when installed, let it handle the server-list MOTD
              # (MKT's own MOTD override backs off).
              minimotd:
                enabled: true
              # SkinsRestorer — use a player's SkinsRestorer skin for the greeting face/avatar.
              skinsrestorer:
                enabled: true
              # PlasmoVoice — a player muted in MKT is also muted in voice chat.
              plasmovoice:
                enabled: true
              # Simple Voice Chat — a player muted in MKT is also muted in voice chat.
              simplevoicechat:
                enabled: true
            """;

    static final String COMMANDS_YML = """
            # ============================================
            #  MKT Essentials - Commands
            # ============================================
            # Enable or disable command groups.
            # Set to false to completely disable a command.
            # Changes require server restart.

            # ============================================
            #  Teleportation Commands
            # ============================================
            teleportation:
              home: true      # /home, /sethome, /delhome, /homes
              warp: true      # /warp, /setwarp, /delwarp, /warps
              spawn: true     # /spawn
              back: true      # /back
              top: true       # /top
              rtp: true       # /rtp (random teleport)
              tpa: true       # /tpa, /tpahere, /tpaccept, /tpdeny

            # ============================================
            #  Admin Commands
            # ============================================
            admin:
              heal: true      # /heal [player]
              feed: true      # /feed [player]
              fly: true       # /fly [player]
              god: true       # /god [player]
              vanish: true    # /vanish
              speed: true     # /speed fly|walk <value> [player]
              clearinv: true  # /clearinv [player]
              tpall: true     # /tpall
              invsee: true    # /invsee <player>
              enderchest: true # /enderchest <player>
              backup: true    # /invbackup save|list|restore|delete
              clearitems: true # /clearitems [radius]
              queue: true     # /cmdqueue (queued console commands for online&offline players)
              itemstore: true # /itemstore (saved item categories, crates, rewards)
              scheduler: true # /scheduler (automated commands, see scheduler.yml)

            # ============================================
            #  Moderation Commands
            # ============================================
            moderation:
              kick: true      # /kick <player> [reason]
              ban: true       # /ban, /tempban, /unban
              banip: true     # /banip, /unbanip
              mute: true      # /mute, /unmute, /tempmute
              warn: true      # /warn, /unwarn, /warns, /history
              shadowban: true  # /shadowban, /unshadowban, /shadowbanlist

            # ============================================
            #  Utility Commands
            # ============================================
            utility:
              repair: true    # /repair
              enchant: true   # /enchant <enchantment> <level>
              exp: true       # /exp give|set <player> <levels>
              kit: true       # /kit, /kits, /createkit, /deletekit
              nick: true      # /nick [nickname]
              msg: true       # /msg, /reply, /msgtoggle, /ignore
              shortcuts: true # /gm, /gmc, /gms, /gma, /gmsp, /tp, /tphere, /tppos, /i, /more, /skull, /near, /seen, /sudo
              whois: true     # /whois <player>
              playtime: true  # /playtime [player]
              stations: true  # /trash, /workbench, /craft, /anvil, /grindstone, /stonecutter, /smithing
              ptime: true     # /ptime, /pweather (per-player client time/weather)
              helpop: true    # /helpop, /report
              tps: true       # /tps, /lag
              afk: true       # /afk
            """;

    static final String MESSAGES_YML = """
            # ============================================
            #  MKT Essentials - Messages & Chat
            # ============================================
            # Configure chat formatting, join/quit messages,
            # and automated broadcasts.

            # ============================================
            #  Chat Format
            # ============================================
            chat:
              # Chat format template. Available placeholders:
              # %mktessentials:dot% - recording/streaming indicator
              # %mktessentials:prefix% - LuckPerms prefix
              # %mktessentials:name% - player display name
              # %mktessentials:suffix% - LuckPerms suffix
              # {message} - the chat message content
              format: "%mktessentials:dot%%mktessentials:prefix%%mktessentials:name%%mktessentials:suffix%&8: &f{message}"
              # Per-group chat formats (LuckPerms primary group name → format)
              # If a player's group is listed here, this format is used instead of the default above.
              # Same placeholders available as in the default format.
              group-formats:
                # admin: "&c[Admin] &f%mktessentials:name%&8: &f{message}"
                # vip: "&6[VIP] &f%mktessentials:name%&8: &f{message}"

            # ============================================
            #  Join & Quit Messages
            # ============================================
            join-quit:
              # Enable custom join/quit messages
              enabled: true
              # Available: %mktessentials:full_name/safe%
              join-message: "&8[&a+&8] &7%mktessentials:full_name/safe% joined the game."
              quit-message: "&8[&c-&8] &7%mktessentials:full_name/safe% left the game."

            # ============================================
            #  Automated Broadcasts
            # ============================================
            broadcast:
              # Enable automated broadcasts
              enabled: true
              # Interval between broadcasts in seconds
              interval: 300
              # Prefix prepended to each broadcast message
              prefix: "&8[&bINFO&8] &r"
              # Order: "random" or "sequential"
              order: "random"
              # List of messages to broadcast
              messages:
                - "&7Welcome to our server!"
                - "&7Join our Discord: &b/discord"
                - "&7Use &6/rtp &7to start your adventure!"

            # ============================================
            #  Text Commands
            # ============================================
            # Custom commands that print configured text. Each entry can define
            # multiple aliases (e.g. "rules" and "zasady") and multiple lines.
            # Aliases that collide with existing commands are skipped.
            # Changes require a server restart (commands are registered at startup).
            text-commands:
              rules:
                aliases: ["rules", "zasady"]
                messages:
                  - "&6--- &eServer Rules &6---"
                  - "&71. Be respectful to other players."
                  - "&72. No griefing or stealing."
                  - "&73. No cheating or exploiting bugs."
              www:
                aliases: ["www", "website"]
                messages:
                  - "&bOur website: &fhttps://example.com"
              vote:
                aliases: ["vote", "glosuj"]
                messages:
                  - "&aVote for our server: &fhttps://example.com/vote"
              discord-invite:
                aliases: ["discordinvite", "dc"]
                messages:
                  - "&9Join our Discord: &fhttps://discord.gg/yourcode"
            """;

    static final String LANG_EN_US = """
            # MKT Essentials - English (en_us)
            general:
              prefix: "&8[&6MKT&8] &r"
              no-permission: "&cYou don't have permission to do that."
              player-not-found: "&cPlayer '{player}' not found."
              player-only: "&cThis command can only be used by players."
              reload-success: "&aConfiguration reloaded successfully."
              reload-failed: "&cFailed to reload configuration. Check console for errors."

            teleportation:
              delay: "&7Teleporting in &6{seconds}&7 seconds... Don't move!"
              cancelled: "&cTeleportation cancelled."
              cooldown: "&cYou must wait &6{seconds}&c seconds before teleporting again."
              teleported: "&aTeleported successfully!"

            homes:
              set: "&aHome '&6{name}&a' has been set."
              deleted: "&aHome '&6{name}&a' has been deleted."
              not-found: "&cHome '&6{name}&c' not found."
              limit-reached: "&cYou have reached your home limit ({max})."
              list: "&7Your homes: &6{homes}"
              none: "&7You have no homes set."

            warps:
              set: "&aWarp '&6{name}&a' has been set."
              deleted: "&aWarp '&6{name}&a' has been deleted."
              not-found: "&cWarp '&6{name}&c' not found."
              list: "&7Available warps: &6{warps}"
              none: "&7No warps available."

            tpa:
              sent: "&aTeleport request sent to &6{player}&a."
              received: "&6{player} &7has requested to teleport {direction}."
              accepted: "&aTeleport request accepted."
              denied: "&cTeleport request denied."
              expired: "&cTeleport request has expired."
              no-request: "&cNo pending teleport requests found."
              no-request-from: "&cNo pending request from that player."
              to-you: "to you"
              you-to-them: "you to them"
              self: "&cYou cannot teleport to yourself!"

            admin:
              healed: "&aHealed &6{player}&a."
              fed: "&aFed &6{player}&a."
              fly-enabled: "&7Flight &aenabled &7for &6{player}&7."
              fly-disabled: "&7Flight &cdisabled &7for &6{player}&7."
              god-enabled: "&7God mode &aenabled &7for &6{player}&7."
              god-disabled: "&7God mode &cdisabled &7for &6{player}&7."
              vanish-enabled: "&7Vanish &aenabled&7. You are now hidden."
              vanish-disabled: "&7Vanish &cdisabled&7. You are now visible."
              speed-fly: "&7Flying speed set to &6{value} &7for &6{player}&7."
              speed-fly-no-fly: "&eNote: &6{player} &ecannot fly. Speed saved and will apply when flight is enabled."
              speed-walk: "&7Walking speed set to &6{value} &7for &6{player}&7."
              clearinv: "&7Cleared inventory of &6{player}&7."
              tpall: "&aTeleported &6{count} &aplayers to your location."

            moderation:
              kicked: "&7Kicked &6{player}&7. Reason: &f{reason}"
              banned: "&7Banned &6{player}&7. Reason: &f{reason}"
              temp-banned: "&7Temporarily banned &6{player} &7for &e{duration}&7. Reason: &f{reason}"
              unbanned: "&aUnbanned &6{player}&a."
              not-banned: "&cPlayer is not banned."
              muted: "&7Muted &6{player} &7{duration}."
              unmuted: "&aUnmuted &6{player}&a."
              muted-notify: "&cYou have been muted {duration}."
              already-muted: "&cYou are muted for {remaining}."
              permanently-muted: "&cYou are permanently muted."
              shadowbanned: "&7Shadowbanned &6{player}&7. Method: &e{method}&7. Reason: &f{reason}"
              unshadowbanned: "&aRemoved shadowban from &6{player}&a."
              not-shadowbanned: "&cPlayer is not shadowbanned."
              shadowban-list-header: "&7--- Shadowbanned Players ---"
              shadowban-list-entry: "&7- &6{player} &7({method}) &8[{reason}]"
              shadowban-list-empty: "&7No shadowbanned players."

            utility:
              repair-success: "&aItem repaired successfully."
              repair-empty-hand: "&cYou must hold an item to repair."
              repair-not-damageable: "&cThis item cannot be repaired."
              enchant-success: "&aApplied &6{enchantment} &alevel &6{level} &ato your item."
              enchant-empty-hand: "&cYou must hold an item to enchant."
              enchant-invalid: "&cInvalid enchantment."

            afk:
              disabled: "&cThe AFK system is disabled on this server."
              now-afk: "&7{player} is now AFK."
              no-longer-afk: "&7{player} is no longer AFK."

            rtp:
              searching: "&7Searching for a safe location..."
              failed: "&cCould not find a safe location after 50 attempts. Please try again."

            back:
              no-location: "&cNo back location found!"

            auth:
              welcome-link-required: "&7You must link your Discord account to play on this server."
              welcome-register: "&7Please register with &6/register <password> <password>"
              welcome-login: "&7Please login with &6/login <password>"
              link-code: "&7Your link code: &b&l{code}"
              link-instruction: "&7Use &b/{command} {code} &7on our Discord server to link your account."
              link-success: "&aYour Discord account has been linked successfully!"
              register-success: "&aRegistration successful! You are now logged in."
              login-success: "&aLogin successful! Welcome back."
              login-failed: "&cWrong password! Attempt {attempts}/{max}."
              already-registered: "&cYou are already registered."
              already-linked: "&cYour account is already linked to a Discord account."
              passwords-dont-match: "&cPasswords do not match."
              frozen-reminder: "&7Please authenticate to continue playing."
              kicked-timeout: "&cYou were kicked for not authenticating within {seconds} seconds."
              kicked-max-attempts: "&cToo many failed login attempts ({max}). Try again in a few minutes."
              newbie-protection: "&aYou have newbie protection for &6{minutes} &aminutes."
              password-changed: "&aPassword changed successfully."
              wrong-old-password: "&cThe old password is incorrect."
              must-link-first: "&cYou must link your Discord account before registering."
              not-registered: "&cYou are not registered. Use &6/register &cto create an account."
              discord-info: "&7Linked Discord ID: &b{discord_id}"
              no-discord-linked: "&7No Discord account linked."
              unlink-success: "&aDiscord account unlinked successfully."
              code-invalid: "&cInvalid or expired link code."
              code-expired: "&cThis link code has expired. Generate a new one with /link."
              discord-already-linked: "&cThis Discord account is already linked to another player."
              password-too-short: "&cPassword must be at least 4 characters."
              password-too-long: "&cPassword must be at most 64 characters."
              admin-reset: "&aAccount for &6{player} &ahas been reset."
              admin-unlink: "&aDiscord unlinked for &6{player}&a."
              admin-info-header: "&7--- Account Info for &6{player} &7---"
              admin-info-uuid: "&7UUID: &f{uuid}"
              admin-info-discord: "&7Discord: &f{discord}"
              admin-info-registered: "&7Registered: &f{date}"
              admin-info-last-login: "&7Last login: &f{date}"
              admin-info-last-ip: "&7Last IP: &f{ip}"
              admin-no-account: "&cNo account found for that player."

            shortcuts:
              gamemode-self: "&7Gamemode set to &6{mode}&7."
              gamemode-other: "&7Set &6{player}&7's gamemode to &6{mode}&7."
              tp-to: "&7Teleported to &6{player}&7."
              tp-here: "&7Teleported &6{player} &7to you."
              tp-pos: "&7Teleported to &6{x}, {y}, {z}&7."
              invalid-item: "&cInvalid item ID: &6{item}"
              item-not-found: "&cItem not found: &6{item}"
              gave-item: "&7Gave &6{amount}x {item}&7."
              must-hold-item: "&cYou must be holding an item!"
              more-success: "&7Stack size set to &6{amount}&7."
              skull-given: "&7Gave skull of &6{player}&7."
              near-none: "&7No players within &6{radius} &7blocks."
              near-found: "&7Nearby (&6{count}&7): {list}"
              seen-online: "&6{player} &7is currently &aonline&7."
              seen-never: "&cPlayer '&6{player}&c' has never joined this server."
              seen-ago: "&6{player} &7was last seen &e{time} ago&7."
              seen-unknown: "&6{player} &7has played before but last seen time is unknown."
              sudo-executed: "&7Forced &6{player} &7to execute: &f/{command}"

            items:
              sweep-warning: "&e\u26A0 &7Ground items will be cleared in &6{seconds} &7seconds!"
              sweep-cleared: "&7Cleared &6{count} &7ground items."
              clearitems-success: "&aCleared &6{count} &aitems from the ground."
              clearitems-radius: "&aCleared &6{count} &aitems within &6{radius} &ablocks."
              clearitems-none: "&7No items to clear."
            """;

    static final String LANG_PL_PL = """
            # MKT Essentials - Polski (pl_pl)
            general:
              prefix: "&8[&6MKT&8] &r"
              no-permission: "&cNie masz uprawnien do wykonania tej czynnosci."
              player-not-found: "&cGracz '{player}' nie zostal znaleziony."
              player-only: "&cTa komenda moze byc uzyta tylko przez graczy."
              reload-success: "&aKonfiguracja zostala pomyslnie przeladowana."
              reload-failed: "&cNie udalo sie przeladowac konfiguracji. Sprawdz konsole."

            teleportation:
              delay: "&7Teleportacja za &6{seconds}&7 sekund... Nie ruszaj sie!"
              cancelled: "&cTeleportacja anulowana."
              cooldown: "&cMusisz poczekac &6{seconds}&c sekund przed kolejna teleportacja."
              teleported: "&aTeleportacja udana!"

            homes:
              set: "&aDom '&6{name}&a' zostal ustawiony."
              deleted: "&aDom '&6{name}&a' zostal usuniety."
              not-found: "&cDom '&6{name}&c' nie zostal znaleziony."
              limit-reached: "&cOsiagnieto limit domow ({max})."
              list: "&7Twoje domy: &6{homes}"
              none: "&7Nie masz ustawionych domow."

            warps:
              set: "&aWarp '&6{name}&a' zostal ustawiony."
              deleted: "&aWarp '&6{name}&a' zostal usuniety."
              not-found: "&cWarp '&6{name}&c' nie zostal znaleziony."
              list: "&7Dostepne warpy: &6{warps}"
              none: "&7Brak dostepnych warpow."

            tpa:
              sent: "&aProba teleportacji wyslana do &6{player}&a."
              received: "&6{player} &7chce sie teleportowac {direction}."
              accepted: "&aProba teleportacji zaakceptowana."
              denied: "&cProba teleportacji odrzucona."
              expired: "&cProba teleportacji wygasla."
              no-request: "&cBrak oczekujacych prob teleportacji."
              no-request-from: "&cBrak oczekujacej prosby od tego gracza."
              to-you: "do ciebie"
              you-to-them: "ciebie do niego"
              self: "&cNie mozesz teleportowac sie do siebie!"

            admin:
              healed: "&aUleczono &6{player}&a."
              fed: "&aNakarmiono &6{player}&a."
              fly-enabled: "&7Latanie &awlaczone &7dla &6{player}&7."
              fly-disabled: "&7Latanie &cwylaczone &7dla &6{player}&7."
              god-enabled: "&7Tryb boga &awlaczony &7dla &6{player}&7."
              god-disabled: "&7Tryb boga &cwylaczony &7dla &6{player}&7."
              vanish-enabled: "&7Vanish &awlaczony&7. Jestes teraz niewidzialny."
              vanish-disabled: "&7Vanish &cwylaczony&7. Jestes teraz widoczny."
              speed-fly: "&7Predkosc latania ustawiona na &6{value} &7dla &6{player}&7."
              speed-fly-no-fly: "&eUwaga: &6{player} &enie moze latac. Predkosc zapisana i zostanie zastosowana po wlaczeniu lotu."
              speed-walk: "&7Predkosc chodzenia ustawiona na &6{value} &7dla &6{player}&7."
              clearinv: "&7Wyczyszczono ekwipunek &6{player}&7."
              tpall: "&aTeleportowano &6{count} &agraczy do twojej lokalizacji."

            moderation:
              kicked: "&7Wyrzucono &6{player}&7. Powod: &f{reason}"
              banned: "&7Zbanowano &6{player}&7. Powod: &f{reason}"
              temp-banned: "&7Tymczasowo zbanowano &6{player} &7na &e{duration}&7. Powod: &f{reason}"
              unbanned: "&aOdbanowano &6{player}&a."
              not-banned: "&cGracz nie jest zbanowany."
              muted: "&7Wyciszono &6{player} &7{duration}."
              unmuted: "&aOdciszono &6{player}&a."
              muted-notify: "&cZostales wyciszony {duration}."
              already-muted: "&cJestes wyciszony jeszcze przez {remaining}."
              permanently-muted: "&cJestes permanentnie wyciszony."
              shadowbanned: "&7Shadowban nalozony na &6{player}&7. Metoda: &e{method}&7. Powod: &f{reason}"
              unshadowbanned: "&aUsunieto shadowban z &6{player}&a."
              not-shadowbanned: "&cGracz nie jest shadowbanowany."
              shadowban-list-header: "&7--- Shadowbanowani gracze ---"
              shadowban-list-entry: "&7- &6{player} &7({method}) &8[{reason}]"
              shadowban-list-empty: "&7Brak shadowbanowanych graczy."

            utility:
              repair-success: "&aPrzedmiot naprawiony pomyslnie."
              repair-empty-hand: "&cMusisz trzymac przedmiot, aby go naprawic."
              repair-not-damageable: "&cTen przedmiot nie moze zostac naprawiony."
              enchant-success: "&aDodano &6{enchantment} &apoziom &6{level} &ana twoj przedmiot."
              enchant-empty-hand: "&cMusisz trzymac przedmiot, aby go zaczarowac."
              enchant-invalid: "&cNieprawidlowy czar."

            afk:
              disabled: "&cSystem AFK jest wylaczony na tym serwerze."
              now-afk: "&7{player} jest teraz AFK."
              no-longer-afk: "&7{player} nie jest juz AFK."

            rtp:
              searching: "&7Szukanie bezpiecznej lokalizacji..."
              failed: "&cNie znaleziono bezpiecznej lokalizacji po 50 probach. Sprobuj ponownie."

            back:
              no-location: "&cNie znaleziono poprzedniej lokalizacji!"

            auth:
              welcome-link-required: "&7Musisz polaczyc swoje konto Discord, aby grac na tym serwerze."
              welcome-register: "&7Zarejestruj sie komenda &6/register <haslo> <haslo>"
              welcome-login: "&7Zaloguj sie komenda &6/login <haslo>"
              link-code: "&7Twoj kod polaczenia: &b&l{code}"
              link-instruction: "&7Uzyj &b/{command} {code} &7na naszym Discordzie, aby polaczyc konto."
              link-success: "&aTwoje konto Discord zostalo pomyslnie polaczone!"
              register-success: "&aRejestracja udana! Jestes teraz zalogowany."
              login-success: "&aLogowanie udane! Witaj ponownie."
              login-failed: "&cBledne haslo! Proba {attempts}/{max}."
              already-registered: "&cJestes juz zarejestrowany."
              already-linked: "&cTwoje konto jest juz polaczone z kontem Discord."
              passwords-dont-match: "&cHasla nie sa identyczne."
              frozen-reminder: "&7Prosze sie uwierzytelnic, aby kontynuowac gre."
              kicked-timeout: "&cZostales wyrzucony za brak uwierzytelnienia w ciagu {seconds} sekund."
              kicked-max-attempts: "&cZbyt wiele nieudanych prob logowania ({max}). Sprobuj ponownie za kilka minut."
              newbie-protection: "&aMasz ochrone dla nowych graczy przez &6{minutes} &aminut."
              password-changed: "&aHaslo zostalo zmienione pomyslnie."
              wrong-old-password: "&cStare haslo jest nieprawidlowe."
              must-link-first: "&cMusisz najpierw polaczyc konto Discord przed rejestracja."
              not-registered: "&cNie jestes zarejestrowany. Uzyj &6/register &caby utworzyc konto."
              discord-info: "&7Polaczone Discord ID: &b{discord_id}"
              no-discord-linked: "&7Brak polaczonego konta Discord."
              unlink-success: "&aKonto Discord zostalo odlaczone pomyslnie."
              code-invalid: "&cNieprawidlowy lub wygasly kod polaczenia."
              code-expired: "&cTen kod polaczenia wygasl. Wygeneruj nowy komenda /link."
              discord-already-linked: "&cTo konto Discord jest juz polaczone z innym graczem."
              password-too-short: "&cHaslo musi miec co najmniej 4 znaki."
              password-too-long: "&cHaslo moze miec maksymalnie 64 znaki."
              admin-reset: "&aKonto gracza &6{player} &azostalo zresetowane."
              admin-unlink: "&aDiscord odlaczony dla &6{player}&a."
              admin-info-header: "&7--- Informacje o koncie &6{player} &7---"
              admin-info-uuid: "&7UUID: &f{uuid}"
              admin-info-discord: "&7Discord: &f{discord}"
              admin-info-registered: "&7Zarejestrowano: &f{date}"
              admin-info-last-login: "&7Ostatnie logowanie: &f{date}"
              admin-info-last-ip: "&7Ostatnie IP: &f{ip}"
              admin-no-account: "&cNie znaleziono konta dla tego gracza."

            shortcuts:
              gamemode-self: "&7Tryb gry ustawiony na &6{mode}&7."
              gamemode-other: "&7Ustawiono tryb gry &6{player}&7 na &6{mode}&7."
              tp-to: "&7Teleportowano do &6{player}&7."
              tp-here: "&7Teleportowano &6{player} &7do ciebie."
              tp-pos: "&7Teleportowano na &6{x}, {y}, {z}&7."
              invalid-item: "&cNieprawidlowe ID przedmiotu: &6{item}"
              item-not-found: "&cNie znaleziono przedmiotu: &6{item}"
              gave-item: "&7Dano &6{amount}x {item}&7."
              must-hold-item: "&cMusisz trzymac przedmiot!"
              more-success: "&7Rozmiar stosu ustawiony na &6{amount}&7."
              skull-given: "&7Dano glowe &6{player}&7."
              near-none: "&7Brak graczy w zasiegu &6{radius} &7blokow."
              near-found: "&7W poblizu (&6{count}&7): {list}"
              seen-online: "&6{player} &7jest aktualnie &aonline&7."
              seen-never: "&cGracz '&6{player}&c' nigdy nie dolaczyl do tego serwera."
              seen-ago: "&6{player} &7byl widziany &e{time} temu&7."
              seen-unknown: "&6{player} &7gral wczesniej ale czas ostatniej wizyty jest nieznany."
              sudo-executed: "&7Wymuszono na &6{player} &7wykonanie: &f/{command}"

            items:
              sweep-warning: "&e\u26A0 &7Przedmioty na ziemi zostana usuniete za &6{seconds} &7sekund!"
              sweep-cleared: "&7Usunieto &6{count} &7przedmiotow z ziemi."
              clearitems-success: "&aUsunieto &6{count} &aprzedmiotow z ziemi."
              clearitems-radius: "&aUsunieto &6{count} &aprzedmiotow w zasiegu &6{radius} &ablokow."
              clearitems-none: "&7Brak przedmiotow do usuniecia."
            """;

    static final String SCHEDULER_YML = """
            # ============================================
            #  MKT Essentials - Scheduler
            # ============================================
            # Named schedulers that run console commands automatically.
            # Edit this file and run /mkt reload — no restart needed.
            #
            # Schedule (pick one):
            #   every: 5m          how often to run (s/m/h/d, e.g. 30s, 5m, 2h, 1d)
            #   every: 5m-10m      random interval between the two values
            #   at: ["12:00"]      fire at these times of day instead of an interval
            #   once: true         run one time after the server starts
            #   delay: 30s         wait this long after startup before the first run
            #   days: [monday]     limit an "at" schedule to these weekdays
            #
            # mode: how the command list is used on each run
            #   all          run every command, in order
            #   random       one random command, never the same one twice in a row
            #   random:2     that many random commands
            #   weighted     one command, chosen by its "weight"
            #   sequential   the next command in the list, advancing every run
            #   shuffle      random, but every command runs once before any repeats
            #
            # spread: 3s     stagger the commands of one run by up to this much
            #
            # conditions: all must hold, otherwise "when-invalid" decides what happens
            #   min-players / max-players   player count bounds
            #   player: Steve               that player must be online
            #   dimension: minecraft:the_nether   someone must be in that dimension
            #   uptime: 5m                  server must have been up this long
            #   between: "18:00-23:00"      real time window (may cross midnight)
            #   game-time: "13000-23000"    in-game overworld tick window (night here)
            #   when-invalid: skip | wait   skip this run, or hold it until conditions hold

            schedulers:

              # Rotating hints, one random line every 5-10 minutes, only with players online.
              tips:
                enabled: true
                every: 5m-10m
                mode: shuffle
                conditions:
                  min-players: 1
                  when-invalid: skip
                commands:
                  - "broadcast &7Type &f/help &7to see every command."
                  - "broadcast &7Set your home with &f/sethome&7."
                  - "broadcast &7Join our Discord with &f/discord&7."

              # Nightly restart warning, weekdays only.
              restart-warning:
                enabled: false
                at: ["03:55", "03:58"]
                days: [monday, tuesday, wednesday, thursday, friday]
                mode: all
                commands:
                  - "broadcast &c&lServer restarts in a few minutes!"

              # A weighted example: the rare line shows up roughly a tenth of the time.
              weighted-example:
                enabled: false
                every: 30m
                mode: weighted
                conditions:
                  min-players: 3
                commands:
                  - command: "broadcast &7Common message"
                    weight: 9
                  - command: "broadcast &6Rare message!"
                    weight: 1
            """;
}

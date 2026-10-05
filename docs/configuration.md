# Configuration & Setup

## Installation

1. Install **NeoForge 1.21.1** (21.1.228 or newer) on your server.
2. Download `mktessentials-1.0.0.jar` and drop it into `mods/`.
3. (Optional) Add any of the [integration mods](integrations.md) to the same folder.
4. Start the server once to generate configs, stop it, edit, start again.

All bundled libraries (JDA, SQLite, jBCrypt, Jackson, …) ship **inside** the JAR.

> **Discord features** additionally require the **KotlinForForge** mod (it provides the Kotlin
> runtime the Discord library needs). Without it the server still starts — only Discord is
> disabled, with a hint in the log.

## Config layout

First launch creates:

```
config/mktessentials/
├── settings.yml      — Gameplay: teleport, RTP, AFK, items, warns, auth
├── chat.yml          — Chat, formatting, cosmetics (tab, nametag, sidebar, brand, greeting, MOTD…)
├── integration.yml   — External mod/service hooks (Discord, mod hooks, web sync)
├── commands.yml      — Enable/disable individual commands
├── messages.yml      — Chat format, join/quit, broadcasts, text commands
├── accounts.db       — SQLite auth database (created when auth is enabled)
└── lang/
    ├── en_us.yml
    └── pl_pl.yml
```

Per-world data (homes, warps, kits, bans, IP bans, punishment history, spawn) lives in
`<world>/mktessentials/`.

Apply changes with `/mkt reload` (most settings) or restart (auth/Discord/web changes).
Switch language with `language: "pl_pl"` (or `en_us`) at the top of `settings.yml`.

## Common config snippets

### Enable/disable commands (`commands.yml`)
```yaml
admin:
  fly: true
  vanish: false   # disabled
```

### AFK (`settings.yml` & `commands.yml`)
```yaml
# settings.yml
afk:
  enabled: true       # Enable/disable AFK system and /afk command
  timeout: 300        # Inactivity timeout in seconds (0 = disable auto-AFK)

# commands.yml
utility:
  afk: true           # Enable/disable /afk command
```

### Per-group chat format (`messages.yml`)
```yaml
chat:
  format: "%mktessentials:prefix%%mktessentials:name%%mktessentials:suffix%&8: &f{message}"
  group-formats:
    admin: "&c[Admin] &f%mktessentials:name%&8: &f{message}"
    vip:   "&6[VIP] &f%mktessentials:name%&8: &f{message}"
```

### Text commands (`messages.yml`)
```yaml
text-commands:
  rules:
    aliases: ["rules", "zasady"]
    messages:
      - "&6--- &eServer Rules &6---"
      - "&71. Be respectful."
```

### Teleportation (`settings.yml`)
```yaml
general:
  max-homes: 3              # default; override per rank with mktessentials.homes.<n>
teleportation:
  delay: 3                  # warmup seconds (0 = instant)
  cooldown: 10              # global cooldown (0 = none)
  cooldown-tpa: -1          # per-type overrides (-1 = inherit "cooldown")
  cooldown-rtp: -1
  cooldown-warp: -1
```

### RTP (`settings.yml`)
```yaml
rtp:
  min-distance: 500
  max-distance: 5000
  biome-blacklist: ["minecraft:ocean", "minecraft:deep_ocean", "minecraft:river"]
```

### Item management (`settings.yml`)
```yaml
items:
  despawn-time: 300        # seconds (0 = disabled)
  stacking: true
  show-hologram: true
  whitelist: ["minecraft:netherite_*", "create:brass_ingot"]
```

### Shadowban method (`settings.yml`)
```yaml
moderation:
  shadowban-method: "timeout"   # timeout | full | internal-error | phantom
```
See [Moderation](moderation.md) for what each method does.

## Building from source

```bash
./gradlew build
```
Output: `build/libs/mktessentials-1.0.0.jar`

## Troubleshooting

| Symptom | Cause & Fix |
|---------|-------------|
| Server won't start: *conflicting versions of kotlin-stdlib* | Two mods ship Kotlin. Keep **KotlinForForge** and make sure no other mod bundles its own Kotlin. |
| `Discord bot needs the KotlinForForge mod` in the log | Install **KotlinForForge** to use Discord (linking/relay). |
| Discord bot connects then drops: `CloseCode 4014 (DISALLOWED_INTENTS)` | Enable the required intents on the **Bot** tab of the Discord Developer Portal — **Server Members** (linking) and **Message Content** (relay). |
| Discord relay: channel messages don't reach the game | Enable **Message Content Intent** and check `discord.relay.channel-id`. |
| `Discord bot token is empty` | Set `discord.bot-token` and `discord.enabled: true` in `integration.yml`. |
| Discord avatar always shows Steve (offline mode) | Expected unless the login name matches a real Minecraft account; the avatar is fetched by name. |
| LuckPerms not loading on 1.21.1 | Use the [patched LuckPerms](https://github.com/onmydestiny/LuckPerms-PATCHED). |
| Permissions ignored | Without LuckPerms the mod uses vanilla OP. Install it and grant `mktessentials.*`. |

← Back to the [README](../README.md)

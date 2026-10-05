# Integrations & Permissions

MKT Essentials runs fully standalone. Every integration below is **optional and auto-detected** at
startup — installed = used, absent = safely ignored.

## Optional mod hooks

Toggles live in `integration.yml → mods`.

| Mod | What it does | Notes |
|-----|--------------|-------|
| **LuckPerms** | Permissions, prefixes/suffixes/usernames, per-group chat | Recommended. Full hex color code support (`&#RRGGBB`, `§#RRGGBB`, `&x...`, `<#RRGGBB>`). Use the [patched build](https://github.com/onmydestiny/LuckPerms-PATCHED) on 1.21.1. |
| **Text Placeholder API** | Exposes `%mktessentials:*%` to other mods, and resolves theirs in chat | Use the [NeoForge port](https://github.com/MakotoPD/TextPlaceholderAPI-NeoForge). |
| **TAB** | When present, MKT's native tab list / nametags / ping step aside so TAB manages them | Avoids double-rendering. |
| **MiniMOTD** | When present, MKT's MOTD override steps aside | — |
| **SkinsRestorer** | The greeting face uses the player's SkinsRestorer skin | — |
| **Plasmo Voice** | A player muted in-game is silently muted in voice chat | — |
| **Simple Voice Chat** | A player muted in-game is silently muted in voice chat | — |
| **Curios API** | `/invsee` shows Curios slots; backups include them | — |

Without LuckPerms the mod falls back to vanilla OP levels.

---

## Placeholders

With the Text Placeholder API installed, these resolve in `messages.yml` chat/join/quit formats and
in TAB's config:

```
%mktessentials:name%        %mktessentials:username%    %mktessentials:nick%        %mktessentials:real_name%
%mktessentials:prefix%      %mktessentials:suffix%      %mktessentials:full_name%
%mktessentials:tab_full_name%
```

MKT placeholders always resolve **internally** for the mod's own features (tab, nametag, chat) — the
API is only needed to share them with other mods.

---

## Permissions

Every command has its own node (`mktessentials.command.*`, `mktessentials.admin.*`,
`mktessentials.moderation.*`). Use any permission manager; LuckPerms is recommended.

```
/lp group default permission set mktessentials.command.home true
/lp group vip permission set mktessentials.command.rtp true
/lp group admin permission set mktessentials.admin.* true
```

Print every node in-game with **`/mkt permissions`**.

### Per-rank limits & cooldowns

| Node | Type | Effect |
|------|------|--------|
| `mktessentials.homes.<number>` | permission | Max homes for the rank (**highest granted number wins**; `.*` or `.unlimited` = no limit) |
| `mktessentials.max_homes` | meta | Alternative home limit (fallback: config `general.max-homes`) |
| `mktessentials.teleport_cooldown.tpa` / `.rtp` / `.warp` | meta | Per-type teleport cooldown (seconds) |
| `mktessentials.teleport_cooldown` | meta | Cooldown for all teleport types (global override) |
| `mktessentials.teleport_delay` | meta | Warmup delay before a teleport executes |
| `mktessentials.teleport.bypass` | permission | Skip teleport delay & all cooldowns |

```
# VIP: 10 homes + faster RTP;  MVP: 25;  Admin: unlimited
/lp group vip permission set mktessentials.homes.10
/lp group vip meta set mktessentials.teleport_cooldown.rtp 15
/lp group mvp permission set mktessentials.homes.25
/lp group admin permission set mktessentials.homes.unlimited
```

Each teleport type tracks its own cooldown clock — using `/tpa` doesn't start the `/rtp` or
`/warp` cooldown.

← Back to the [README](../README.md)

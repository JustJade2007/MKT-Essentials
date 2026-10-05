package pl.makoto.essentials.config;

import pl.makoto.essentials.auth.AuthMode;

import java.util.*;

public final class Settings {
    // General
    private static String language = "en_us";
    private static String messagePrefix = "&8[&6MKT&8] &r";
    private static int maxHomes = 3;

    // Teleportation
    private static int teleportDelay = 3;
    private static int teleportCooldown = 10;
    // Per-type cooldown overrides (-1 = inherit the global teleportCooldown)
    private static int teleportCooldownTpa = -1;
    private static int teleportCooldownRtp = -1;
    private static int teleportCooldownWarp = -1;
    private static boolean teleportEffects = true;
    private static int tpaTimeout = 60;

    // RTP
    private static int rtpMinDistance = 500;
    private static int rtpMaxDistance = 5000;
    private static boolean rtpRelative = true;
    private static double rtpCenterX = 0.0;
    private static double rtpCenterZ = 0.0;
    private static List<String> rtpBiomeBlacklist = List.of("minecraft:ocean", "minecraft:deep_ocean", "minecraft:river");

    // AFK
    private static boolean afkEnabled = true;
    private static int afkTimeout = 300;

    // Nickname (/nick validation — inspired by FlectonePulse allowedInput)
    private static boolean nicknameEnabled = true;
    private static int nicknameMinLength = 1;
    private static int nicknameMaxLength = 16;
    // Regex applied to the VISIBLE nickname (color/format codes stripped). Empty = no restriction.
    private static String nicknameAllowedPattern = "";

    // Roleplay commands (/me /do /try) — inspired by FlectonePulse me/do/try
    private static boolean roleplayEnabled = true;
    private static String meFormat = "&d* %mktessentials:name% &f{message}";
    private static String doFormat = "&d* &f{message}";
    private static String tryFormat = "&d* %mktessentials:name% &7tries to &f{message}&7 and {result}.";
    private static String trySuccess = "&asucceeds";
    private static String tryFail = "&cfails";

    // Mail (offline messages) — inspired by FlectonePulse mail
    private static boolean mailEnabled = true;
    private static int mailMaxPerPlayer = 30;

    // Maintenance mode — inspired by FlectonePulse maintenance
    private static boolean maintenanceDefault = false;
    private static String maintenanceKick = "&cThe server is under maintenance.\n&7Please check back later.";
    // Shown in the server list while maintenance is active (auto, like FlectonePulse)
    private static List<String> maintenanceMotd = List.of("&c&lUNDER MAINTENANCE", "&7We'll be back soon!");
    private static String maintenanceIcon = ""; // filename in config/mktessentials/icon/ (64x64 PNG)

    // Native tab list header/footer (no external TAB mod required)
    private static boolean tablistEnabled = true;
    private static int tablistUpdateInterval = 40; // ticks
    private static List<String> tablistHeader = List.of("&6&lMKT Server", "");
    private static List<String> tablistFooter = List.of("", "&7Players online: &f{online}&7/&f{max}");
    // Ping number shown next to each player's name in the tab list (scoreboard LIST objective)
    private static boolean tablistPingEnabled = false;
    private static int tablistPingInterval = 40; // ticks
    // Animated header/footer: cycle through frames (each frame is a list of lines)
    private static boolean tablistAnimate = false;
    private static int tablistAnimationInterval = 20; // ticks between frames
    private static List<List<String>> tablistHeaderFrames = List.of();
    private static List<List<String>> tablistFooterFrames = List.of();

    // Native above-head nametags via scoreboard teams (rank prefix/suffix)
    private static boolean nametagEnabled = true;

    // Below-name scoreboard (a number under the player's name in the world)
    private static boolean belowNameEnabled = false;
    private static String belowNameType = "health"; // "health" or "ping"
    private static String belowNameSuffix = "&c❤";  // shown after the number
    private static int belowNameInterval = 40; // ticks

    // Native sidebar scoreboard (side panel)
    private static boolean sidebarEnabled = false;
    private static int sidebarInterval = 40; // ticks
    private static String sidebarTitle = "&6&lMKT SERVER";
    private static List<String> sidebarLines = List.of("&7Welcome!", "&7Online: &f{online}&7/&f{max}");

    // Persistent boss bar — inspired by FlectonePulse bossbar
    private static boolean bossbarEnabled = false;
    private static String bossbarText = "&6Welcome to the server, %mktessentials:name%!";
    private static String bossbarColor = "purple";
    private static String bossbarOverlay = "progress";
    private static int bossbarUpdateInterval = 40; // ticks

    // Server brand shown in the F3 debug screen — inspired by FlectonePulse brand
    private static boolean brandEnabled = false;
    private static int brandUpdateInterval = 100; // ticks (used when rotating multiple texts)
    private static List<String> brandTexts = List.of("&bMKT &fEssentials");

    // Server list MOTD override — inspired by FlectonePulse status module
    private static boolean motdEnabled = false;
    private static List<String> motdLines = List.of("&6MKT Server &8» &7Welcome!", "&aRunning MKT Essentials");
    // Path to a 64x64 PNG used as the server-list icon (empty = keep vanilla server-icon.png)
    private static String motdIcon = "";

    // Right-click player info — inspired by FlectonePulse rightclick module
    private static boolean rightclickEnabled = true;
    private static boolean rightclickRequireSneak = true;
    private static List<String> rightclickFormat = List.of(
            "&8&m                    ",
            " &6%mktessentials:full_name%",
            " &7Real name: &f%mktessentials:real_name%",
            "&8&m                    ");

    // Chat mentions (@name) — inspired by FlectonePulse mention module
    private static boolean mentionEnabled = true;
    private static String mentionColor = "aqua";
    private static String mentionSound = "minecraft:block.note_block.pling";
    private static boolean mentionSelf = false;

    // Chat replacements (clickable URLs, spoilers) — inspired by FlectonePulse format/replacement
    private static boolean replacementEnabled = true;
    private static boolean replacementUrl = true;
    private static boolean replacementSpoiler = true;
    private static String replacementUrlColor = "blue";

    // Inline chat objects: [item] (held item + tooltip) and <head> (player head), no resource pack
    private static boolean objectsEnabled = true;

    // Markdown-style chat formatting (*italic* **bold** __underline__ ~~strike~~ ??matrix??)
    private static boolean markdownEnabled = true;

    // Per-player chat color (/chatcolor)
    private static boolean chatcolorEnabled = true;

    // Color/format codes on renamed items (anvil), written books, and signs (permission-filtered)
    private static boolean anvilColorEnabled = true;
    private static boolean bookColorEnabled = true;
    private static boolean signColorEnabled = true;

    // Stream announce (/stream)
    private static boolean streamEnabled = true;
    private static String streamFormat = "&d&l⭐ &e%mktessentials:name% &7is now streaming! &b{url}";

    // Anonymous chat (/anon)
    private static boolean anonEnabled = true;
    private static String anonFormat = "&8[&7Anon&8] &f{message}";

    // Question highlight (message ending with ?)
    private static boolean questionEnabled = false;
    private static String questionColor = "yellow";

    // Newbie chat restrictions (players below a playtime threshold)
    private static boolean newbieChatEnabled = false;
    private static int newbieMinPlaytimeMinutes = 10;
    private static boolean newbieBlockLinks = true;

    // Local chat — inspired by FlectonePulse chat ranges. Mode: "range" (distance) or "world" (same dimension).
    private static boolean chatLocalEnabled = false;
    private static String chatLocalMode = "range";
    private static int chatLocalRadius = 100;
    private static String chatGlobalPrefix = "!";

    // Named reusable animations usable anywhere via <animation:name> — inspired by FlectonePulse
    public record Animation(int interval, List<String> frames) {}
    private static Map<String, Animation> animations = Map.of();

    // Emoji / symbol shortcuts (:code: -> symbol) — inspired by FlectonePulse sprite/symbol
    private static Map<String, String> emojis = defaultEmojis();

    private static Map<String, String> defaultEmojis() {
        Map<String, String> m = new HashMap<>();
        m.put(":heart:", "❤");
        m.put(":star:", "★");
        m.put(":check:", "✔");
        m.put(":cross:", "✖");
        m.put(":arrow:", "➤");
        m.put(":skull:", "☠");
        m.put(":note:", "♪");
        m.put(":sun:", "☀");
        return m;
    }

    // Chat moderation (swear / caps / flood) — inspired by FlectonePulse format/moderation
    private static boolean chatModEnabled = true;
    private static boolean capsEnabled = true;
    private static int capsMinLength = 8;
    private static int capsMaxPercent = 70;
    private static String capsMode = "lowercase"; // "lowercase" or "block"
    private static boolean floodEnabled = true;
    private static double floodCooldownSeconds = 1.5;
    private static boolean floodBlockDuplicate = true;
    private static int floodMaxRepeatedChars = 4; // collapse runs longer than this; 0 = disabled
    private static boolean swearEnabled = false;
    private static String swearMode = "censor"; // "censor" or "block"
    private static String swearCensorChar = "*";
    private static List<String> swearWords = List.of();

    // Data
    private static int autoSaveInterval = 300;

    // Vanish
    private static boolean vanishFakeMessages = true;

    // Backup
    private static boolean backupOnDeath = true;
    private static boolean backupOnJoin = false;
    private static boolean backupOnQuit = true;
    private static int backupInterval = 0; // 0 = disabled, seconds between scheduled backups
    private static int maxBackupsPerPlayer = 10;

    // Auth
    private static AuthMode authMode = AuthMode.DISABLED;
    private static int sessionTimeoutHours = 24;
    private static int maxLoginAttempts = 5;
    private static int loginTimeoutSeconds = 60;
    private static int newbieProtectionMinutes = 30;

    // Discord
    private static boolean discordEnabled = false;
    private static String discordBotToken = "";
    private static String discordGuildId = "";
    private static String discordLinkCommandName = "link";
    private static String discordLinkedRoleId = "";
    private static boolean discordShowPlayerCount = true;

    // Discord chat relay (2-way bridge MC <-> a Discord channel), reuses the linking bot
    private static boolean discordRelayEnabled = false;
    private static String discordRelayChannelId = "";
    private static String discordRelayWebhookUrl = "";
    private static String discordRelayToDiscord = "**<player>**: <message>";
    private static String discordRelayFromDiscord = "&9[Discord] &b<author>&7: &f<message>";
    private static boolean discordRelayJoinQuit = true;
    private static String discordRelayJoin = "**<player>** joined the server";
    private static String discordRelayQuit = "**<player>** left the server";

    // Web sync (opcjonalna integracja linkowania z zewnętrznym backendem, np. panel www)
    private static boolean webSyncEnabled = false;
    private static String webSyncUrl = "";
    private static String webSyncSecret = "";
    private static boolean webSyncBackfillOnStart = false;

    // Web API — kanał przychodzący (panel www → mod), np. rozłączanie kont z panelu
    private static boolean webApiEnabled = false;
    private static String webApiBind = "127.0.0.1";
    private static int webApiPort = 8766;
    private static String webApiSecret = "";

    // Messages
    private static String chatFormat = "%mktessentials:dot%%mktessentials:prefix%%mktessentials:name%%mktessentials:suffix%&8: &f{message}";
    private static Map<String, String> groupChatFormats = new HashMap<>();
    // Greeting (personal welcome shown to the joining player) — inspired by FlectonePulse greeting.
    // Lines with the [#][#][#][#][#][#][#][#] marker render the player's skin face (pixels).
    private static boolean greetingEnabled = true;
    private static String greetingType = "chat"; // "chat", "actionbar", or "title"
    private static boolean greetingSkin = true;
    private static String greetingAvatarUrl = "https://mc-heads.net/avatar/<name>/8.png";
    private static List<String> greetingFirstJoin = List.of(
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]  &6&lHello,",
            "[#][#][#][#][#][#][#][#]  &e%mktessentials:name%",
            "[#][#][#][#][#][#][#][#]  &7Welcome to the server!",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]");
    private static List<String> greetingReturning = List.of(
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]  &6&lWelcome back,",
            "[#][#][#][#][#][#][#][#]  &e%mktessentials:name%",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]",
            "[#][#][#][#][#][#][#][#]");
    private static int greetingTitleFadeIn = 10;
    private static int greetingTitleStay = 60;
    private static int greetingTitleFadeOut = 20;

    private static boolean joinQuitEnabled = true;
    private static String joinMessage = "&8[&a+&8] &7%mktessentials:full_name/safe% joined the game.";
    private static String quitMessage = "&8[&c-&8] &7%mktessentials:full_name/safe% left the game.";
    private static boolean broadcastEnabled = true;
    private static int broadcastInterval = 300;
    private static List<String> broadcastMessages = List.of("&7Welcome to our server!", "&7Join our Discord: &b/discord", "&7Use &6/rtp &7to start your adventure!");
    private static String broadcastPrefix = "&8[&bINFO&8] &r";
    private static String broadcastOrder = "random";

    // Text commands (messages.yml → text-commands)
    public record TextCommand(String name, List<String> aliases, List<String> messages) {}
    private static List<TextCommand> textCommands = List.of();

    // Moderation
    private static String shadowbanMethod = "timeout";
    private static int maxWarns = 3;
    private static String warnBanDuration = "1d";

    // Items
    private static int itemDespawnTime = 300; // seconds, 0 = disabled
    private static boolean itemStacking = true;
    private static int itemStackingRadius = 3;
    private static boolean itemHologramEnabled = true;
    private static int itemSweepInterval = 0; // seconds, 0 = disabled
    private static int itemSweepWarning = 30; // seconds before sweep to warn
    private static int itemMaxStackSize = 64; // max stack size for ground item merging (64 = vanilla)
    private static List<String> itemWhitelist = List.of("minecraft:netherite_sword", "minecraft:netherite_pickaxe", "minecraft:elytra", "minecraft:shulker_box");

    // Commands
    private static Map<String, Boolean> commandToggles = new HashMap<>();

    private Settings() {}

    // Getters
    public static String getLanguage() { return language; }
    public static String getMessagePrefix() { return messagePrefix; }
    public static int getMaxHomes() { return maxHomes; }
    public static int getTeleportDelay() { return teleportDelay; }
    public static int getTeleportCooldown() { return teleportCooldown; }
    /** Cooldown (seconds) for a teleport type key (tpa/rtp/warp/...), falling back to the global cooldown. */
    public static int getTeleportCooldown(String type) {
        int override = switch (type) {
            case "tpa" -> teleportCooldownTpa;
            case "rtp" -> teleportCooldownRtp;
            case "warp" -> teleportCooldownWarp;
            default -> -1;
        };
        return override >= 0 ? override : teleportCooldown;
    }
    public static boolean getTeleportEffects() { return teleportEffects; }
    public static int getTpaTimeout() { return tpaTimeout; }
    public static int getRtpMinDistance() { return rtpMinDistance; }
    public static int getRtpMaxDistance() { return rtpMaxDistance; }
    public static boolean isRtpRelative() { return rtpRelative; }
    public static double getRtpCenterX() { return rtpCenterX; }
    public static double getRtpCenterZ() { return rtpCenterZ; }
    public static List<String> getRtpBiomeBlacklist() { return rtpBiomeBlacklist; }
    public static boolean isAfkEnabled() { return afkEnabled; }
    public static int getAfkTimeout() { return afkTimeout; }
    public static boolean isRoleplayEnabled() { return roleplayEnabled; }
    public static String getMeFormat() { return meFormat; }
    public static String getDoFormat() { return doFormat; }
    public static String getTryFormat() { return tryFormat; }
    public static String getTrySuccess() { return trySuccess; }
    public static String getTryFail() { return tryFail; }
    public static boolean isMailEnabled() { return mailEnabled; }
    public static int getMailMaxPerPlayer() { return mailMaxPerPlayer; }
    public static boolean isMaintenanceDefault() { return maintenanceDefault; }
    public static String getMaintenanceKick() { return maintenanceKick; }
    public static List<String> getMaintenanceMotd() { return maintenanceMotd; }
    public static String getMaintenanceIcon() { return maintenanceIcon; }
    public static boolean isTablistEnabled() { return tablistEnabled; }
    public static int getTablistUpdateInterval() { return tablistUpdateInterval; }
    public static List<String> getTablistHeader() { return tablistHeader; }
    public static List<String> getTablistFooter() { return tablistFooter; }
    public static boolean isTablistPingEnabled() { return tablistPingEnabled; }
    public static int getTablistPingInterval() { return tablistPingInterval; }
    public static boolean isTablistAnimate() { return tablistAnimate; }
    public static int getTablistAnimationInterval() { return tablistAnimationInterval; }
    public static List<List<String>> getTablistHeaderFrames() { return tablistHeaderFrames; }
    public static List<List<String>> getTablistFooterFrames() { return tablistFooterFrames; }
    public static boolean isNametagEnabled() { return nametagEnabled; }
    public static boolean isBelowNameEnabled() { return belowNameEnabled; }
    public static String getBelowNameType() { return belowNameType; }
    public static String getBelowNameSuffix() { return belowNameSuffix; }
    public static int getBelowNameInterval() { return belowNameInterval; }
    public static boolean isSidebarEnabled() { return sidebarEnabled; }
    public static int getSidebarInterval() { return sidebarInterval; }
    public static String getSidebarTitle() { return sidebarTitle; }
    public static List<String> getSidebarLines() { return sidebarLines; }
    public static boolean isBossbarEnabled() { return bossbarEnabled; }
    public static String getBossbarText() { return bossbarText; }
    public static String getBossbarColor() { return bossbarColor; }
    public static String getBossbarOverlay() { return bossbarOverlay; }
    public static int getBossbarUpdateInterval() { return bossbarUpdateInterval; }
    public static boolean isBrandEnabled() { return brandEnabled; }
    public static int getBrandUpdateInterval() { return brandUpdateInterval; }
    public static List<String> getBrandTexts() { return brandTexts; }
    public static boolean isMotdEnabled() { return motdEnabled; }
    public static List<String> getMotdLines() { return motdLines; }
    public static String getMotdIcon() { return motdIcon; }
    public static boolean isRightclickEnabled() { return rightclickEnabled; }
    public static boolean isRightclickRequireSneak() { return rightclickRequireSneak; }
    public static List<String> getRightclickFormat() { return rightclickFormat; }
    public static boolean isNicknameEnabled() { return nicknameEnabled; }
    public static int getNicknameMinLength() { return nicknameMinLength; }
    public static int getNicknameMaxLength() { return nicknameMaxLength; }
    public static String getNicknameAllowedPattern() { return nicknameAllowedPattern; }
    public static boolean isMentionEnabled() { return mentionEnabled; }
    public static String getMentionColor() { return mentionColor; }
    public static String getMentionSound() { return mentionSound; }
    public static boolean isMentionSelf() { return mentionSelf; }
    public static boolean isReplacementEnabled() { return replacementEnabled; }
    public static boolean isReplacementUrl() { return replacementUrl; }
    public static boolean isReplacementSpoiler() { return replacementSpoiler; }
    public static String getReplacementUrlColor() { return replacementUrlColor; }
    public static boolean isObjectsEnabled() { return objectsEnabled; }
    public static boolean isMarkdownEnabled() { return markdownEnabled; }
    public static boolean isChatcolorEnabled() { return chatcolorEnabled; }
    public static boolean isAnvilColorEnabled() { return anvilColorEnabled; }
    public static boolean isBookColorEnabled() { return bookColorEnabled; }
    public static boolean isSignColorEnabled() { return signColorEnabled; }
    public static boolean isStreamEnabled() { return streamEnabled; }
    public static String getStreamFormat() { return streamFormat; }
    public static boolean isAnonEnabled() { return anonEnabled; }
    public static String getAnonFormat() { return anonFormat; }
    public static boolean isQuestionEnabled() { return questionEnabled; }
    public static String getQuestionColor() { return questionColor; }
    public static boolean isNewbieChatEnabled() { return newbieChatEnabled; }
    public static int getNewbieMinPlaytimeMinutes() { return newbieMinPlaytimeMinutes; }
    public static boolean isNewbieBlockLinks() { return newbieBlockLinks; }
    public static boolean isChatLocalEnabled() { return chatLocalEnabled; }
    public static String getChatLocalMode() { return chatLocalMode; }
    public static int getChatLocalRadius() { return chatLocalRadius; }
    public static String getChatGlobalPrefix() { return chatGlobalPrefix; }
    public static Map<String, String> getEmojis() { return emojis; }
    public static Map<String, Animation> getAnimations() { return animations; }
    public static Animation getAnimation(String name) { return animations.get(name); }
    public static boolean isChatModEnabled() { return chatModEnabled; }
    public static boolean isCapsEnabled() { return capsEnabled; }
    public static int getCapsMinLength() { return capsMinLength; }
    public static int getCapsMaxPercent() { return capsMaxPercent; }
    public static String getCapsMode() { return capsMode; }
    public static boolean isFloodEnabled() { return floodEnabled; }
    public static double getFloodCooldownSeconds() { return floodCooldownSeconds; }
    public static boolean isFloodBlockDuplicate() { return floodBlockDuplicate; }
    public static int getFloodMaxRepeatedChars() { return floodMaxRepeatedChars; }
    public static boolean isSwearEnabled() { return swearEnabled; }
    public static String getSwearMode() { return swearMode; }
    public static String getSwearCensorChar() { return swearCensorChar; }
    public static List<String> getSwearWords() { return swearWords; }
    public static int getAutoSaveInterval() { return autoSaveInterval; }
    public static boolean isVanishFakeMessages() { return vanishFakeMessages; }
    public static boolean isBackupOnDeath() { return backupOnDeath; }
    public static boolean isBackupOnJoin() { return backupOnJoin; }
    public static boolean isBackupOnQuit() { return backupOnQuit; }
    public static int getBackupInterval() { return backupInterval; }
    public static int getMaxBackupsPerPlayer() { return maxBackupsPerPlayer; }
    public static String getChatFormat() { return chatFormat; }
    public static Map<String, String> getGroupChatFormats() { return groupChatFormats; }
    
    /**
     * Gets the chat format for a specific player based on their LuckPerms primary group.
     * Falls back to the default chat format if no group-specific format is defined.
     */
    public static String getChatFormatForGroup(String group) {
        if (group != null && groupChatFormats.containsKey(group)) {
            return groupChatFormats.get(group);
        }
        return chatFormat;
    }
    public static boolean isGreetingEnabled() { return greetingEnabled; }
    public static String getGreetingType() { return greetingType; }
    public static boolean isGreetingSkin() { return greetingSkin; }
    public static String getGreetingAvatarUrl() { return greetingAvatarUrl; }
    public static List<String> getGreetingFirstJoin() { return greetingFirstJoin; }
    public static List<String> getGreetingReturning() { return greetingReturning; }
    public static int getGreetingTitleFadeIn() { return greetingTitleFadeIn; }
    public static int getGreetingTitleStay() { return greetingTitleStay; }
    public static int getGreetingTitleFadeOut() { return greetingTitleFadeOut; }
    public static boolean isJoinQuitEnabled() { return joinQuitEnabled; }
    public static String getJoinMessage() { return joinMessage; }
    public static String getQuitMessage() { return quitMessage; }
    public static boolean isBroadcastEnabled() { return broadcastEnabled; }
    public static int getBroadcastInterval() { return broadcastInterval; }
    public static List<String> getBroadcastMessages() { return broadcastMessages; }
    public static String getBroadcastPrefix() { return broadcastPrefix; }
    public static String getBroadcastOrder() { return broadcastOrder; }
    public static List<TextCommand> getTextCommands() { return textCommands; }

    public static String getShadowbanMethod() { return shadowbanMethod; }
    /** Active warns that trigger an automatic tempban (0 = escalation disabled). */
    public static int getMaxWarns() { return maxWarns; }
    public static String getWarnBanDuration() { return warnBanDuration; }

    // Items getters
    public static int getItemDespawnTime() { return itemDespawnTime; }
    public static boolean isItemStacking() { return itemStacking; }
    public static int getItemStackingRadius() { return itemStackingRadius; }
    public static boolean isItemHologramEnabled() { return itemHologramEnabled; }
    public static int getItemSweepInterval() { return itemSweepInterval; }
    public static int getItemSweepWarning() { return itemSweepWarning; }
    public static int getItemMaxStackSize() { return itemMaxStackSize; }
    public static List<String> getItemWhitelist() { return itemWhitelist; }

    public static boolean isCommandEnabled(String commandName) {
        return commandToggles.getOrDefault(commandName, true);
    }

    // Auth & Discord getters
    public static AuthMode getAuthMode() { return authMode; }
    public static int getSessionTimeoutHours() { return sessionTimeoutHours; }
    public static int getMaxLoginAttempts() { return maxLoginAttempts; }
    public static int getLoginTimeoutSeconds() { return loginTimeoutSeconds; }
    public static int getNewbieProtectionMinutes() { return newbieProtectionMinutes; }
    public static boolean isDiscordEnabled() { return discordEnabled; }
    public static String getDiscordBotToken() { return discordBotToken; }
    public static String getDiscordGuildId() { return discordGuildId; }
    public static String getDiscordLinkCommandName() { return discordLinkCommandName; }
    public static String getDiscordLinkedRoleId() { return discordLinkedRoleId; }
    public static boolean isDiscordShowPlayerCount() { return discordShowPlayerCount; }
    public static boolean isDiscordRelayEnabled() { return discordRelayEnabled; }
    public static String getDiscordRelayChannelId() { return discordRelayChannelId; }
    public static String getDiscordRelayWebhookUrl() { return discordRelayWebhookUrl; }
    public static String getDiscordRelayToDiscord() { return discordRelayToDiscord; }
    public static String getDiscordRelayFromDiscord() { return discordRelayFromDiscord; }
    public static boolean isDiscordRelayJoinQuit() { return discordRelayJoinQuit; }
    public static String getDiscordRelayJoin() { return discordRelayJoin; }
    public static String getDiscordRelayQuit() { return discordRelayQuit; }
    public static boolean isWebSyncEnabled() { return webSyncEnabled; }
    public static String getWebSyncUrl() { return webSyncUrl; }
    public static String getWebSyncSecret() { return webSyncSecret; }
    public static boolean isWebSyncBackfillOnStart() { return webSyncBackfillOnStart; }
    public static boolean isWebApiEnabled() { return webApiEnabled; }
    public static String getWebApiBind() { return webApiBind; }
    public static int getWebApiPort() { return webApiPort; }
    /** Sekret web-api; jeśli pusty, używa wspólnego sekretu web-sync. */
    public static String getWebApiSecret() { return webApiSecret.isBlank() ? webSyncSecret : webApiSecret; }

    // Optional mod integrations (auto-detected; toggles turn the hooks off)
    private static boolean integrationTab = true;
    private static boolean integrationMinimotd = true;
    private static boolean integrationSkinsRestorer = true;
    private static boolean integrationPlasmoVoice = true;
    private static boolean integrationSimpleVoice = true;

    public static boolean isIntegrationTab() { return integrationTab; }
    public static boolean isIntegrationMinimotd() { return integrationMinimotd; }
    public static boolean isIntegrationSkinsRestorer() { return integrationSkinsRestorer; }
    public static boolean isIntegrationPlasmoVoice() { return integrationPlasmoVoice; }
    public static boolean isIntegrationSimpleVoice() { return integrationSimpleVoice; }

    // Loaders (called by ConfigManager)
    @SuppressWarnings("unchecked")
    static void loadSettings(Map<String, Object> map) {
        language = ConfigManager.getNestedValue(map, "language", "en_us");
        messagePrefix = ConfigManager.getNestedValue(map, "general.message-prefix", "&8[&6MKT&8] &r");
        maxHomes = ConfigManager.getNestedValue(map, "general.max-homes", 3);
        teleportDelay = ConfigManager.getNestedValue(map, "teleportation.delay", 3);
        teleportCooldown = ConfigManager.getNestedValue(map, "teleportation.cooldown", 10);
        teleportCooldownTpa = ConfigManager.getNestedValue(map, "teleportation.cooldown-tpa", -1);
        teleportCooldownRtp = ConfigManager.getNestedValue(map, "teleportation.cooldown-rtp", -1);
        teleportCooldownWarp = ConfigManager.getNestedValue(map, "teleportation.cooldown-warp", -1);
        teleportEffects = ConfigManager.getNestedValue(map, "teleportation.effects", true);
        tpaTimeout = ConfigManager.getNestedValue(map, "teleportation.tpa-timeout", 60);
        rtpMinDistance = ConfigManager.getNestedValue(map, "rtp.min-distance", 500);
        rtpMaxDistance = ConfigManager.getNestedValue(map, "rtp.max-distance", 5000);
        rtpRelative = ConfigManager.getNestedValue(map, "rtp.relative-to-player", true);
        rtpCenterX = ConfigManager.getNestedValue(map, "rtp.center-x", 0.0);
        rtpCenterZ = ConfigManager.getNestedValue(map, "rtp.center-z", 0.0);
        Object blacklist = ConfigManager.getNestedValue(map, "rtp.biome-blacklist", (Object) List.of("minecraft:ocean", "minecraft:deep_ocean", "minecraft:river"));
        if (blacklist instanceof List<?> list) {
            rtpBiomeBlacklist = list.stream().map(Object::toString).toList();
        }
        afkEnabled = ConfigManager.getNestedValue(map, "afk.enabled", true);
        afkTimeout = ConfigManager.getNestedValue(map, "afk.timeout", 300);
        autoSaveInterval = ConfigManager.getNestedValue(map, "data.auto-save-interval", 300);
        vanishFakeMessages = ConfigManager.getNestedValue(map, "vanish.fake-messages", true);
        backupOnDeath = ConfigManager.getNestedValue(map, "backup.on-death", true);
        backupOnJoin = ConfigManager.getNestedValue(map, "backup.on-join", false);
        backupOnQuit = ConfigManager.getNestedValue(map, "backup.on-quit", true);
        backupInterval = ConfigManager.getNestedValue(map, "backup.interval", 0);
        maxBackupsPerPlayer = ConfigManager.getNestedValue(map, "backup.max-per-player", 10);
        shadowbanMethod = ConfigManager.getNestedValue(map, "moderation.shadowban-method", "timeout");
        maxWarns = ConfigManager.getNestedValue(map, "moderation.max-warns", 3);
        warnBanDuration = ConfigManager.getNestedValue(map, "moderation.warn-ban-duration", "1d");

        // Items
        itemDespawnTime = ConfigManager.getNestedValue(map, "items.despawn-time", 300);
        itemStacking = ConfigManager.getNestedValue(map, "items.stacking", true);
        itemStackingRadius = ConfigManager.getNestedValue(map, "items.stacking-radius", 3);
        itemHologramEnabled = ConfigManager.getNestedValue(map, "items.show-hologram", true);
        itemSweepInterval = ConfigManager.getNestedValue(map, "items.sweep-interval", 0);
        itemSweepWarning = ConfigManager.getNestedValue(map, "items.sweep-warning", 30);
        itemMaxStackSize = Math.max(1, (int) ConfigManager.getNestedValue(map, "items.max-stack-size", 64));
        Object whitelistObj = ConfigManager.getNestedValue(map, "items.whitelist", (Object) itemWhitelist);
        if (whitelistObj instanceof List<?> list) {
            itemWhitelist = list.stream().map(Object::toString).toList();
        }
    }

    /** Parses named animations: {@code name: { interval: <ticks>, frames: [ ... ] }}. */
    private static Map<String, Animation> parseAnimations(Object obj) {
        if (!(obj instanceof Map<?, ?> map)) return Map.of();
        Map<String, Animation> result = new HashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getValue() instanceof Map<?, ?> def)) continue;
            int interval = def.get("interval") instanceof Number n ? Math.max(1, n.intValue()) : 10;
            List<String> frames = new ArrayList<>();
            if (def.get("frames") instanceof List<?> list) {
                list.forEach(f -> frames.add(String.valueOf(f)));
            }
            if (!frames.isEmpty()) {
                result.put(entry.getKey().toString(), new Animation(interval, List.copyOf(frames)));
            }
        }
        return Map.copyOf(result);
    }

    /** Parses a list of tab-list animation frames: each frame is a list of lines (or a single line). */
    private static List<List<String>> parseFrames(Object obj) {
        if (!(obj instanceof List<?> list)) return List.of();
        List<List<String>> frames = new ArrayList<>();
        for (Object frame : list) {
            if (frame instanceof List<?> lines) {
                frames.add(lines.stream().map(Object::toString).toList());
            } else if (frame != null) {
                frames.add(List.of(frame.toString()));
            }
        }
        return List.copyOf(frames);
    }

    /** Loads chat / social / presentation features from chat.yml. */
    static void loadChat(Map<String, Object> map) {
        nicknameEnabled = ConfigManager.getNestedValue(map, "nickname.enabled", true);
        nicknameMinLength = ConfigManager.getNestedValue(map, "nickname.min-length", 1);
        nicknameMaxLength = ConfigManager.getNestedValue(map, "nickname.max-length", 16);
        nicknameAllowedPattern = ConfigManager.getNestedValue(map, "nickname.allowed-pattern", "");

        mentionEnabled = ConfigManager.getNestedValue(map, "mention.enabled", true);
        mentionColor = ConfigManager.getNestedValue(map, "mention.color", "aqua");
        mentionSound = ConfigManager.getNestedValue(map, "mention.sound", "minecraft:block.note_block.pling");
        mentionSelf = ConfigManager.getNestedValue(map, "mention.self", false);

        replacementEnabled = ConfigManager.getNestedValue(map, "replacement.enabled", true);
        replacementUrl = ConfigManager.getNestedValue(map, "replacement.url", true);
        replacementSpoiler = ConfigManager.getNestedValue(map, "replacement.spoiler", true);
        replacementUrlColor = ConfigManager.getNestedValue(map, "replacement.url-color", "blue");

        objectsEnabled = ConfigManager.getNestedValue(map, "objects.enabled", true);
        markdownEnabled = ConfigManager.getNestedValue(map, "markdown.enabled", true);
        chatcolorEnabled = ConfigManager.getNestedValue(map, "chatcolor.enabled", true);
        anvilColorEnabled = ConfigManager.getNestedValue(map, "text-colors.anvil", true);
        bookColorEnabled = ConfigManager.getNestedValue(map, "text-colors.book", true);
        signColorEnabled = ConfigManager.getNestedValue(map, "text-colors.sign", true);
        streamEnabled = ConfigManager.getNestedValue(map, "stream.enabled", true);
        streamFormat = ConfigManager.getNestedValue(map, "stream.announce-format", streamFormat);
        anonEnabled = ConfigManager.getNestedValue(map, "anon.enabled", true);
        anonFormat = ConfigManager.getNestedValue(map, "anon.format", anonFormat);
        questionEnabled = ConfigManager.getNestedValue(map, "questionanswer.enabled", false);
        questionColor = ConfigManager.getNestedValue(map, "questionanswer.color", "yellow");
        newbieChatEnabled = ConfigManager.getNestedValue(map, "chat-moderation.newbie.enabled", false);
        newbieMinPlaytimeMinutes = ConfigManager.getNestedValue(map, "chat-moderation.newbie.min-playtime-minutes", 10);
        newbieBlockLinks = ConfigManager.getNestedValue(map, "chat-moderation.newbie.block-links", true);

        chatLocalEnabled = ConfigManager.getNestedValue(map, "chat-local.enabled", false);
        chatLocalMode = ConfigManager.getNestedValue(map, "chat-local.mode", "range");
        chatLocalRadius = ConfigManager.getNestedValue(map, "chat-local.radius", 100);
        chatGlobalPrefix = ConfigManager.getNestedValue(map, "chat-local.global-prefix", "!");

        Object emojiObj = ConfigManager.getNestedValue(map, "emojis", (Object) null);
        if (emojiObj instanceof Map<?, ?> em) {
            Map<String, String> parsed = new HashMap<>();
            for (Map.Entry<?, ?> e : em.entrySet()) {
                parsed.put(e.getKey().toString(), e.getValue().toString());
            }
            if (!parsed.isEmpty()) emojis = parsed;
        }
        animations = parseAnimations(ConfigManager.getNestedValue(map, "animations", (Object) null));

        chatModEnabled = ConfigManager.getNestedValue(map, "chat-moderation.enabled", true);
        capsEnabled = ConfigManager.getNestedValue(map, "chat-moderation.caps.enabled", true);
        capsMinLength = ConfigManager.getNestedValue(map, "chat-moderation.caps.min-length", 8);
        capsMaxPercent = ConfigManager.getNestedValue(map, "chat-moderation.caps.max-percent", 70);
        capsMode = ConfigManager.getNestedValue(map, "chat-moderation.caps.mode", "lowercase");
        floodEnabled = ConfigManager.getNestedValue(map, "chat-moderation.flood.enabled", true);
        floodCooldownSeconds = ConfigManager.getNestedValue(map, "chat-moderation.flood.cooldown-seconds", 1.5);
        floodBlockDuplicate = ConfigManager.getNestedValue(map, "chat-moderation.flood.block-duplicate", true);
        floodMaxRepeatedChars = ConfigManager.getNestedValue(map, "chat-moderation.flood.max-repeated-chars", 4);
        swearEnabled = ConfigManager.getNestedValue(map, "chat-moderation.swear.enabled", false);
        swearMode = ConfigManager.getNestedValue(map, "chat-moderation.swear.mode", "censor");
        swearCensorChar = ConfigManager.getNestedValue(map, "chat-moderation.swear.censor-char", "*");
        Object swearObj = ConfigManager.getNestedValue(map, "chat-moderation.swear.words", (Object) swearWords);
        if (swearObj instanceof List<?> list) {
            swearWords = list.stream().map(Object::toString).toList();
        }

        tablistEnabled = ConfigManager.getNestedValue(map, "tablist.enabled", true);
        tablistUpdateInterval = ConfigManager.getNestedValue(map, "tablist.update-interval", 40);
        Object headerObj = ConfigManager.getNestedValue(map, "tablist.header", (Object) tablistHeader);
        if (headerObj instanceof List<?> list) {
            tablistHeader = list.stream().map(Object::toString).toList();
        }
        Object footerObj = ConfigManager.getNestedValue(map, "tablist.footer", (Object) tablistFooter);
        if (footerObj instanceof List<?> list) {
            tablistFooter = list.stream().map(Object::toString).toList();
        }
        tablistPingEnabled = ConfigManager.getNestedValue(map, "tablist.ping-number.enabled", false);
        tablistPingInterval = ConfigManager.getNestedValue(map, "tablist.ping-number.update-interval", 40);
        tablistAnimate = ConfigManager.getNestedValue(map, "tablist.animation.enabled", false);
        tablistAnimationInterval = ConfigManager.getNestedValue(map, "tablist.animation.interval", 20);
        tablistHeaderFrames = parseFrames(ConfigManager.getNestedValue(map, "tablist.animation.header", (Object) null));
        tablistFooterFrames = parseFrames(ConfigManager.getNestedValue(map, "tablist.animation.footer", (Object) null));

        nametagEnabled = ConfigManager.getNestedValue(map, "nametag.enabled", true);
        belowNameEnabled = ConfigManager.getNestedValue(map, "belowname.enabled", false);
        belowNameType = ConfigManager.getNestedValue(map, "belowname.type", "health");
        belowNameSuffix = ConfigManager.getNestedValue(map, "belowname.suffix", "&c❤");
        belowNameInterval = ConfigManager.getNestedValue(map, "belowname.update-interval", 40);
        sidebarEnabled = ConfigManager.getNestedValue(map, "sidebar.enabled", false);
        sidebarInterval = ConfigManager.getNestedValue(map, "sidebar.update-interval", 40);
        sidebarTitle = ConfigManager.getNestedValue(map, "sidebar.title", sidebarTitle);
        Object sidebarObj = ConfigManager.getNestedValue(map, "sidebar.lines", (Object) sidebarLines);
        if (sidebarObj instanceof List<?> list) {
            sidebarLines = list.stream().map(Object::toString).toList();
        }

        bossbarEnabled = ConfigManager.getNestedValue(map, "bossbar.enabled", false);
        bossbarText = ConfigManager.getNestedValue(map, "bossbar.text", bossbarText);
        bossbarColor = ConfigManager.getNestedValue(map, "bossbar.color", "purple");
        bossbarOverlay = ConfigManager.getNestedValue(map, "bossbar.overlay", "progress");
        bossbarUpdateInterval = ConfigManager.getNestedValue(map, "bossbar.update-interval", 40);

        brandEnabled = ConfigManager.getNestedValue(map, "brand.enabled", false);
        brandUpdateInterval = ConfigManager.getNestedValue(map, "brand.update-interval", 100);
        Object brandObj = ConfigManager.getNestedValue(map, "brand.texts", (Object) brandTexts);
        if (brandObj instanceof List<?> list) {
            brandTexts = list.stream().map(Object::toString).toList();
        }
        motdEnabled = ConfigManager.getNestedValue(map, "motd.enabled", false);
        Object motdObj = ConfigManager.getNestedValue(map, "motd.lines", (Object) motdLines);
        if (motdObj instanceof List<?> list) {
            motdLines = list.stream().map(Object::toString).toList();
        }
        motdIcon = ConfigManager.getNestedValue(map, "motd.icon", "");

        rightclickEnabled = ConfigManager.getNestedValue(map, "rightclick.enabled", true);
        rightclickRequireSneak = ConfigManager.getNestedValue(map, "rightclick.require-sneak", true);
        Object rcFormat = ConfigManager.getNestedValue(map, "rightclick.format", (Object) rightclickFormat);
        if (rcFormat instanceof List<?> list) {
            rightclickFormat = list.stream().map(Object::toString).toList();
        }

        roleplayEnabled = ConfigManager.getNestedValue(map, "roleplay.enabled", true);
        meFormat = ConfigManager.getNestedValue(map, "roleplay.me-format", meFormat);
        doFormat = ConfigManager.getNestedValue(map, "roleplay.do-format", doFormat);
        tryFormat = ConfigManager.getNestedValue(map, "roleplay.try-format", tryFormat);
        trySuccess = ConfigManager.getNestedValue(map, "roleplay.try-success", trySuccess);
        tryFail = ConfigManager.getNestedValue(map, "roleplay.try-fail", tryFail);

        mailEnabled = ConfigManager.getNestedValue(map, "mail.enabled", true);
        mailMaxPerPlayer = ConfigManager.getNestedValue(map, "mail.max-per-player", 30);

        maintenanceDefault = ConfigManager.getNestedValue(map, "maintenance.enabled-on-start", false);
        maintenanceKick = ConfigManager.getNestedValue(map, "maintenance.kick-message", maintenanceKick);
        Object maintMotd = ConfigManager.getNestedValue(map, "maintenance.motd", (Object) maintenanceMotd);
        if (maintMotd instanceof List<?> list) {
            maintenanceMotd = list.stream().map(Object::toString).toList();
        }
        maintenanceIcon = ConfigManager.getNestedValue(map, "maintenance.icon", "");

        greetingEnabled = ConfigManager.getNestedValue(map, "greeting.enabled", true);
        greetingType = ConfigManager.getNestedValue(map, "greeting.type", "chat");
        greetingSkin = ConfigManager.getNestedValue(map, "greeting.skin-face", true);
        greetingAvatarUrl = ConfigManager.getNestedValue(map, "greeting.avatar-url", greetingAvatarUrl);
        Object firstJoinObj = ConfigManager.getNestedValue(map, "greeting.first-join", (Object) greetingFirstJoin);
        if (firstJoinObj instanceof List<?> list) {
            greetingFirstJoin = list.stream().map(Object::toString).toList();
        }
        Object returningObj = ConfigManager.getNestedValue(map, "greeting.returning", (Object) greetingReturning);
        if (returningObj instanceof List<?> list) {
            greetingReturning = list.stream().map(Object::toString).toList();
        }
        greetingTitleFadeIn = ConfigManager.getNestedValue(map, "greeting.title-fade-in", 10);
        greetingTitleStay = ConfigManager.getNestedValue(map, "greeting.title-stay", 60);
        greetingTitleFadeOut = ConfigManager.getNestedValue(map, "greeting.title-fade-out", 20);
    }

    @SuppressWarnings("unchecked")
    static void loadCommands(Map<String, Object> map) {
        Map<String, Boolean> toggles = new HashMap<>();
        for (Map.Entry<String, Object> category : map.entrySet()) {
            if (category.getValue() instanceof Map<?, ?> commands) {
                for (Map.Entry<?, ?> cmd : commands.entrySet()) {
                    String name = cmd.getKey().toString();
                    boolean enabled = cmd.getValue() instanceof Boolean b ? b : true;
                    toggles.put(name, enabled);
                }
            }
        }
        commandToggles = toggles;
    }

    @SuppressWarnings("unchecked")
    static void loadMessages(Map<String, Object> map) {
        chatFormat = ConfigManager.getNestedValue(map, "chat.format", chatFormat);
        
        // Load per-group chat formats
        Object groupFormatsObj = ConfigManager.getNestedValue(map, "chat.group-formats", (Object) null);
        Map<String, String> formats = new HashMap<>();
        if (groupFormatsObj instanceof Map<?, ?> groupMap) {
            for (Map.Entry<?, ?> entry : groupMap.entrySet()) {
                formats.put(entry.getKey().toString(), entry.getValue().toString());
            }
        }
        groupChatFormats = formats;
        
        joinQuitEnabled = ConfigManager.getNestedValue(map, "join-quit.enabled", true);
        joinMessage = ConfigManager.getNestedValue(map, "join-quit.join-message", joinMessage);
        quitMessage = ConfigManager.getNestedValue(map, "join-quit.quit-message", quitMessage);
        broadcastEnabled = ConfigManager.getNestedValue(map, "broadcast.enabled", true);
        broadcastInterval = ConfigManager.getNestedValue(map, "broadcast.interval", 300);
        broadcastPrefix = ConfigManager.getNestedValue(map, "broadcast.prefix", broadcastPrefix);
        broadcastOrder = ConfigManager.getNestedValue(map, "broadcast.order", "random");
        Object msgs = ConfigManager.getNestedValue(map, "broadcast.messages", (Object) broadcastMessages);
        if (msgs instanceof List<?> list) {
            broadcastMessages = list.stream().map(Object::toString).toList();
        }

        // Text commands: text-commands.<name> → { aliases: [...], messages: [...] }
        List<TextCommand> parsed = new ArrayList<>();
        Object tcObj = ConfigManager.getNestedValue(map, "text-commands", (Object) null);
        if (tcObj instanceof Map<?, ?> tcMap) {
            for (Map.Entry<?, ?> entry : tcMap.entrySet()) {
                String name = entry.getKey().toString();
                if (!(entry.getValue() instanceof Map<?, ?> def)) continue;

                List<String> aliases = new ArrayList<>();
                if (def.get("aliases") instanceof List<?> aliasList) {
                    aliasList.forEach(a -> aliases.add(a.toString()));
                }
                if (aliases.isEmpty()) aliases.add(name);

                List<String> lines = new ArrayList<>();
                if (def.get("messages") instanceof List<?> msgList) {
                    msgList.forEach(m -> lines.add(m.toString()));
                }
                if (lines.isEmpty()) continue;

                parsed.add(new TextCommand(name, List.copyOf(aliases), List.copyOf(lines)));
            }
        }
        textCommands = List.copyOf(parsed);
    }

    static void loadAuth(Map<String, Object> map) {
        String modeStr = ConfigManager.getNestedValue(map, "auth.mode", "disabled");
        authMode = AuthMode.fromString(modeStr);
        sessionTimeoutHours = ConfigManager.getNestedValue(map, "auth.session-timeout-hours", 24);
        maxLoginAttempts = ConfigManager.getNestedValue(map, "auth.max-login-attempts", 5);
        loginTimeoutSeconds = ConfigManager.getNestedValue(map, "auth.login-timeout-seconds", 60);
        newbieProtectionMinutes = ConfigManager.getNestedValue(map, "auth.newbie-protection-minutes", 30);
    }

    /** Loads integrations (Discord, web-sync) from integration.yml. */
    static void loadIntegration(Map<String, Object> map) {
        discordEnabled = ConfigManager.getNestedValue(map, "discord.enabled", false);
        discordBotToken = ConfigManager.getNestedValue(map, "discord.bot-token", "");
        discordGuildId = ConfigManager.getNestedValue(map, "discord.guild-id", "");
        discordLinkCommandName = ConfigManager.getNestedValue(map, "discord.link-command-name", "link");
        discordLinkedRoleId = ConfigManager.getNestedValue(map, "discord.linked-role-id", "");
        discordShowPlayerCount = ConfigManager.getNestedValue(map, "discord.show-player-count", true);

        discordRelayEnabled = ConfigManager.getNestedValue(map, "discord.relay.enabled", false);
        discordRelayChannelId = ConfigManager.getNestedValue(map, "discord.relay.channel-id", "");
        discordRelayWebhookUrl = ConfigManager.getNestedValue(map, "discord.relay.webhook-url", "");
        discordRelayToDiscord = ConfigManager.getNestedValue(map, "discord.relay.format-to-discord", "**<player>**: <message>");
        discordRelayFromDiscord = ConfigManager.getNestedValue(map, "discord.relay.format-from-discord", "&9[Discord] &b<author>&7: &f<message>");
        discordRelayJoinQuit = ConfigManager.getNestedValue(map, "discord.relay.announce-join-quit", true);
        discordRelayJoin = ConfigManager.getNestedValue(map, "discord.relay.join-to-discord", "**<player>** joined the server");
        discordRelayQuit = ConfigManager.getNestedValue(map, "discord.relay.quit-to-discord", "**<player>** left the server");

        webSyncEnabled = ConfigManager.getNestedValue(map, "web-sync.enabled", false);
        webSyncUrl = ConfigManager.getNestedValue(map, "web-sync.url", "");
        webSyncSecret = ConfigManager.getNestedValue(map, "web-sync.secret", "");
        webSyncBackfillOnStart = ConfigManager.getNestedValue(map, "web-sync.backfill-on-start", false);

        webApiEnabled = ConfigManager.getNestedValue(map, "web-sync.api.enabled", false);
        webApiBind = ConfigManager.getNestedValue(map, "web-sync.api.bind", "127.0.0.1");
        webApiPort = ConfigManager.getNestedValue(map, "web-sync.api.port", 8766);
        webApiSecret = ConfigManager.getNestedValue(map, "web-sync.api.secret", "");

        integrationTab = ConfigManager.getNestedValue(map, "mods.tab.enabled", true);
        integrationMinimotd = ConfigManager.getNestedValue(map, "mods.minimotd.enabled", true);
        integrationSkinsRestorer = ConfigManager.getNestedValue(map, "mods.skinsrestorer.enabled", true);
        integrationPlasmoVoice = ConfigManager.getNestedValue(map, "mods.plasmovoice.enabled", true);
        integrationSimpleVoice = ConfigManager.getNestedValue(map, "mods.simplevoicechat.enabled", true);
    }
}

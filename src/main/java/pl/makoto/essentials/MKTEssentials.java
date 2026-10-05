package pl.makoto.essentials;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

import pl.makoto.essentials.config.ConfigManager;
import pl.makoto.essentials.config.Settings;
import pl.makoto.essentials.auth.AuthManager;
import pl.makoto.essentials.auth.AuthMode;
import pl.makoto.essentials.util.BanManager;
import pl.makoto.essentials.util.BackupManager;
import pl.makoto.essentials.util.ItemCleanerManager;
import pl.makoto.essentials.util.LuckPermsHook;
import pl.makoto.essentials.util.Permissions;
import pl.makoto.essentials.util.ShadowBanManager;
import pl.makoto.essentials.data.DataManager;
import pl.makoto.essentials.commands.*;

import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import pl.makoto.essentials.util.MKTPlaceholders;

@Mod(MKTEssentials.MODID)
public class MKTEssentials {
    public static final String MODID = "mktessentials";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static MinecraftServer server;

    public MKTEssentials(IEventBus modEventBus, ModContainer modContainer) {
        // Register only once to the server event bus
        NeoForge.EVENT_BUS.register(this);
    }

    public static MinecraftServer getServer() {
        return server;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        
        // Ensure config is loaded before command registration
        // (RegisterCommandsEvent fires before ServerStartingEvent)
        if (!ConfigManager.isInitialized()) {
            ConfigManager.init();
        }
        
        LOGGER.info("Registering MKT Essentials commands...");

        if (Settings.isCommandEnabled("home")) HomeCommands.register(dispatcher);
        if (Settings.isCommandEnabled("warp")) WarpCommands.register(dispatcher);
        if (Settings.isCommandEnabled("spawn") || Settings.isCommandEnabled("back") || Settings.isCommandEnabled("top") || Settings.isCommandEnabled("rtp")) MiscTeleportCommands.register(dispatcher);
        if (Settings.isCommandEnabled("tpa")) TpaCommands.register(dispatcher);
        if (Settings.isCommandEnabled("msg")) MessagingCommands.register(dispatcher);
        if (Settings.isCommandEnabled("heal") || Settings.isCommandEnabled("feed") || Settings.isCommandEnabled("fly")
                || Settings.isCommandEnabled("god") || Settings.isCommandEnabled("speed")
                || Settings.isCommandEnabled("vanish") || Settings.isCommandEnabled("clearinv")
                || Settings.isCommandEnabled("tpall") || Settings.isCommandEnabled("invsee")
                || Settings.isCommandEnabled("enderchest")) AdminCommands.register(dispatcher);
        if (Settings.isCommandEnabled("kit")) KitCommands.register(dispatcher);
        MKTCommand.register(dispatcher); // Always register /mkt
        if (Settings.isCommandEnabled("mute")) MuteCommands.register(dispatcher);
        MiscCommands.register(dispatcher); // help etc
        RoleplayCommands.register(dispatcher); // /me /do /try
        if (Settings.isMailEnabled()) MailCommands.register(dispatcher);
        FunCommands.register(dispatcher); // /dice /coin /8ball /toponline
        MaintenanceCommands.register(dispatcher);
        PollCommands.register(dispatcher); // /poll /vote
        GeoLocateCommands.register(dispatcher);
        SocialCommands.register(dispatcher); // /stream /anon /online /symbol /chatsetting
        TimeWeatherCommands.register(dispatcher);
        if (Settings.isCommandEnabled("ban")) BanCommands.register(dispatcher);
        if (Settings.isCommandEnabled("banip")) IpBanCommands.register(dispatcher);
        if (Settings.isCommandEnabled("warn")) WarnCommands.register(dispatcher);
        if (Settings.isCommandEnabled("kick")) KickCommand.register(dispatcher);
        if (Settings.isCommandEnabled("repair")) RepairCommand.register(dispatcher);
        if (Settings.isCommandEnabled("enchant")) EnchantCommand.register(dispatcher);
        if (Settings.isCommandEnabled("exp")) ExpCommand.register(dispatcher);
        if (Settings.isCommandEnabled("backup")) BackupCommands.register(dispatcher);
        if (Settings.getAuthMode() != AuthMode.DISABLED) AuthCommands.register(dispatcher);
        if (Settings.isCommandEnabled("shadowban")) ShadowBanCommands.register(dispatcher);
        if (Settings.isCommandEnabled("shortcuts")) ShortcutCommands.register(dispatcher);
        if (Settings.isCommandEnabled("clearitems")) ClearItemsCommand.register(dispatcher);
        if (Settings.isCommandEnabled("whois") || Settings.isCommandEnabled("playtime")) InfoCommands.register(dispatcher);
        if (Settings.isCommandEnabled("stations")) StationCommands.register(dispatcher);
        if (Settings.isCommandEnabled("ptime")) PlayerTimeWeatherCommands.register(dispatcher);
        if (Settings.isCommandEnabled("helpop")) ReportCommands.register(dispatcher);
        if (Settings.isCommandEnabled("queue")) QueueCommands.register(dispatcher);
        if (Settings.isCommandEnabled("itemstore")) ItemStoreCommands.register(dispatcher);
        if (Settings.isCommandEnabled("scheduler")) SchedulerCommands.register(dispatcher);
        if (Settings.isCommandEnabled("tps")) PerformanceCommands.register(dispatcher);
        // Text commands go last so real commands always win alias conflicts
        TextCommands.register(dispatcher);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("MKT Essentials starting...");
        server = event.getServer();
        ConfigManager.init();
        pl.makoto.essentials.util.MaintenanceManager.setActive(Settings.isMaintenanceDefault());
        pl.makoto.essentials.util.FaviconManager.reload();
        DataManager.init(server);
        BanManager.init(server);
        pl.makoto.essentials.util.IpBanManager.init(server);
        pl.makoto.essentials.util.PunishmentManager.init(server);
        pl.makoto.essentials.util.CommandQueueManager.init(server);
        pl.makoto.essentials.util.ItemStorageManager.init(server);
        ShadowBanManager.init(server);
        BackupManager.init(server);
        AuthManager.init(server);
        LuckPermsHook.init();
        Permissions.init();
        pl.makoto.essentials.util.IntegrationsManager.init();
        // MKT placeholders always work internally; this only exposes them to the optional
        // Text Placeholder API so other mods can use %mktessentials:*% too.
        try {
            MKTPlaceholders.register();
        } catch (LinkageError e) {
            LOGGER.info("Text Placeholder API not found or incompatible — MKT placeholders still work internally, just not exposed to other mods.");
        }

        // Clean up orphaned hologram ArmorStands from previous sessions
        ItemCleanerManager.cleanupOrphanedHolograms(server);

        // Log loaded integrations
        logIntegrations();
    }

    @SubscribeEvent
    public void onServerStarted(net.neoforged.neoforge.event.server.ServerStartedEvent event) {
        LOGGER.info("MKT Essentials fully loaded.");
    }

    private void logIntegrations() {
        LOGGER.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        LOGGER.info("  MKT Essentials v0.3.0 — Integrations");
        LOGGER.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        LOGGER.info("  ✓ Config — YAML (config/mktessentials/)");
        LOGGER.info("  ✓ Language — {} loaded", Settings.getLanguage());

        if (net.neoforged.fml.ModList.get().isLoaded("luckperms")) {
            LOGGER.info("  ✓ LuckPerms — Permissions & chat prefixes");
        } else {
            LOGGER.info("  ✗ LuckPerms — Not found (using vanilla OP)");
        }

        try {
            Class.forName("eu.pb4.placeholders.api.Placeholders");
            LOGGER.info("  ✓ Placeholder API — Placeholders active");
        } catch (ClassNotFoundException e) {
            LOGGER.info("  ✗ Placeholder API — Not found");
        }

        if (net.neoforged.fml.ModList.get().isLoaded("curios")) {
            LOGGER.info("  ✓ Curios API — /invsee shows curios slots");
        }

        if (pl.makoto.essentials.util.IntegrationsManager.isTabPresent()) {
            LOGGER.info("  ✓ TAB — native tablist/nametags {}",
                    Settings.isIntegrationTab() ? "deferred to TAB" : "kept (hook off)");
        }
        if (pl.makoto.essentials.util.IntegrationsManager.isMiniMotdPresent()) {
            LOGGER.info("  ✓ MiniMOTD — MOTD {}",
                    Settings.isIntegrationMinimotd() ? "deferred to MiniMOTD" : "kept (hook off)");
        }
        if (pl.makoto.essentials.util.IntegrationsManager.isSkinsRestorerPresent()) {
            LOGGER.info("  ✓ SkinsRestorer — greeting face uses player's skin");
        }
        if (pl.makoto.essentials.util.IntegrationsManager.isPlasmoVoicePresent()) {
            LOGGER.info("  ✓ PlasmoVoice — /mute also silences voice {}",
                    Settings.isIntegrationPlasmoVoice() ? "(on)" : "(hook off)");
        }
        if (pl.makoto.essentials.util.IntegrationsManager.isSimpleVoicePresent()) {
            LOGGER.info("  ✓ Simple Voice Chat — /mute also silences voice {}",
                    Settings.isIntegrationSimpleVoice() ? "(on)" : "(hook off)");
        }

        if (Settings.getAuthMode() != AuthMode.DISABLED) {
            LOGGER.info("  ✓ Auth System — Mode: {}", Settings.getAuthMode().name().toLowerCase());
            if (Settings.isDiscordEnabled()) {
                LOGGER.info("  ✓ Discord Bot — Connecting...");
            }
        } else {
            LOGGER.info("  ✗ Auth System — Disabled");
        }

        LOGGER.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // Logout events save each online player, but an explicit save guards against
        // paths where they don't fire (e.g. watchdog-initiated stop)
        DataManager.saveAll();
        AuthManager.shutdown();
    }
}

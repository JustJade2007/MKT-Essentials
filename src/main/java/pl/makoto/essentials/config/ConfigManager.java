package pl.makoto.essentials.config;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import pl.makoto.essentials.MKTEssentials;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public final class ConfigManager {
    private static Path configDir;
    private static Path langDir;
    private static Path iconDir;
    private static boolean initialized = false;

    public static boolean isInitialized() { return initialized; }

    public static void init() {
        try {
            configDir = net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get().resolve("mktessentials");
        } catch (Throwable t) {
            configDir = Path.of("config", "mktessentials");
        }
        langDir = configDir.resolve("lang");
        iconDir = configDir.resolve("icon");
        ensureDirectories();
        writeDefaultIfMissing(configDir.resolve("settings.yml"), DefaultTemplates.SETTINGS_YML);
        writeDefaultIfMissing(configDir.resolve("chat.yml"), DefaultTemplates.CHAT_YML);
        writeDefaultIfMissing(configDir.resolve("commands.yml"), DefaultTemplates.COMMANDS_YML);
        writeDefaultIfMissing(configDir.resolve("messages.yml"), DefaultTemplates.MESSAGES_YML);
        writeDefaultIfMissing(configDir.resolve("scheduler.yml"), DefaultTemplates.SCHEDULER_YML);
        // One-time migration: move existing Discord/web-sync config out of settings.yml into
        // integration.yml before the default is written, so upgraders keep their bot token.
        migrateToIntegration();
        writeDefaultIfMissing(configDir.resolve("integration.yml"), DefaultTemplates.INTEGRATION_YML);
        mergeMissingKeys(configDir.resolve("integration.yml"), DefaultTemplates.INTEGRATION_YML);
        // dopisz brakujące klucze do istniejących settings.yml/chat.yml (po aktualizacji moda),
        // zachowując wartości i własne sekcje użytkownika
        mergeMissingKeys(configDir.resolve("settings.yml"), DefaultTemplates.SETTINGS_YML);
        mergeMissingKeys(configDir.resolve("chat.yml"), DefaultTemplates.CHAT_YML);
        mergeMissingKeys(configDir.resolve("commands.yml"), DefaultTemplates.COMMANDS_YML);
        writeDefaultIfMissing(langDir.resolve("en_us.yml"), DefaultTemplates.LANG_EN_US);
        writeDefaultIfMissing(langDir.resolve("pl_pl.yml"), DefaultTemplates.LANG_PL_PL);
        loadAll();
        initialized = true;
    }

    public static boolean reload() {
        try {
            loadAll();
            if (MKTEssentials.getServer() != null && (!Settings.isAfkEnabled() || !Settings.isCommandEnabled("afk"))) {
                pl.makoto.essentials.util.CommandUtils.removeRootCommand(
                        MKTEssentials.getServer().getCommands().getDispatcher(), "afk");
            }
            return true;
        } catch (Exception e) {
            MKTEssentials.LOGGER.error("Failed to reload configuration", e);
            return false;
        }
    }

    private static void loadAll() {
        Map<String, Object> settings = parseYaml(configDir.resolve("settings.yml"));
        Map<String, Object> chat = parseYaml(configDir.resolve("chat.yml"));
        Map<String, Object> integration = parseYaml(configDir.resolve("integration.yml"));
        Map<String, Object> commands = parseYaml(configDir.resolve("commands.yml"));
        Map<String, Object> messages = parseYaml(configDir.resolve("messages.yml"));
        Map<String, Object> scheduler = parseYaml(configDir.resolve("scheduler.yml"));

        Settings.loadSettings(settings != null ? settings : Map.of());
        Settings.loadChat(chat != null ? chat : Map.of());
        Settings.loadCommands(commands != null ? commands : Map.of());
        Settings.loadMessages(messages != null ? messages : Map.of());
        Settings.loadAuth(settings != null ? settings : Map.of());
        Settings.loadIntegration(integration != null ? integration : Map.of());
        pl.makoto.essentials.util.SchedulerManager.load(scheduler != null ? scheduler : Map.of());

        I18n.init(Settings.getLanguage());

        if (!Settings.isAfkEnabled() || !Settings.isCommandEnabled("afk")) {
            pl.makoto.essentials.util.AFKManager.clearAll();
        }
    }

    private static void ensureDirectories() {
        try {
            Files.createDirectories(configDir);
            Files.createDirectories(langDir);
            Files.createDirectories(iconDir);
        } catch (IOException e) {
            MKTEssentials.LOGGER.error("Failed to create config directories", e);
        }
    }

    /** Folder where mktessentials configs live. */
    public static Path getConfigDir() {
        if (configDir == null) init();
        return configDir;
    }

    /** Folder where server-list icons live: {@code config/mktessentials/icon/}. */
    public static Path getIconDir() {
        if (iconDir == null) {
            Path base = getConfigDir();
            iconDir = base.resolve("icon");
        }
        return iconDir;
    }

    /**
     * One-time migration: if integration.yml doesn't exist yet but the (legacy) settings.yml
     * still carries a {@code discord} section or {@code auth.web-sync} subtree, seed integration.yml
     * from those values so upgrading servers keep their configuration. Runs before the default
     * integration.yml is written; a no-op on fresh installs and once integration.yml exists.
     */
    @SuppressWarnings("unchecked")
    private static void migrateToIntegration() {
        Path integrationFile = configDir.resolve("integration.yml");
        if (Files.exists(integrationFile)) return;

        Path settingsFile = configDir.resolve("settings.yml");
        if (!Files.exists(settingsFile)) return;

        Map<String, Object> settings = parseYaml(settingsFile);
        if (settings == null) return;

        Object discord = settings.get("discord");
        Object webSync = settings.get("auth") instanceof Map<?, ?> authMap ? authMap.get("web-sync") : null;
        if (discord == null && webSync == null) return; // nothing to migrate

        Object template;
        try {
            template = new Yaml().load(DefaultTemplates.INTEGRATION_YML);
        } catch (Exception e) {
            return;
        }
        if (!(template instanceof Map<?, ?> templateMap)) return;

        Map<String, Object> merged = new LinkedHashMap<>((Map<String, Object>) templateMap);
        if (discord != null) merged.put("discord", discord);
        if (webSync != null) merged.put("web-sync", webSync);

        try {
            DumperOptions opts = new DumperOptions();
            opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            opts.setPrettyFlow(true);
            opts.setIndent(2);
            String out = new Yaml(opts).dump(merged);
            Files.writeString(integrationFile, out, StandardCharsets.UTF_8);
            MKTEssentials.LOGGER.info("Migrated Discord/web-sync settings from settings.yml to integration.yml.");
        } catch (IOException e) {
            MKTEssentials.LOGGER.error("Failed to write migrated integration.yml", e);
        }
    }

    private static void writeDefaultIfMissing(Path file, String content) {
        if (!Files.exists(file)) {
            try {
                Files.writeString(file, content, StandardCharsets.UTF_8);
            } catch (IOException e) {
                MKTEssentials.LOGGER.error("Failed to write default config: {}", file, e);
            }
        }
    }

    /**
     * Dopisuje do istniejącego pliku klucze obecne w szablonie, a brakujące w pliku
     * użytkownika (rekurencyjnie). Istniejące wartości i własne sekcje użytkownika
     * zostają nietknięte. Zapisuje tylko gdy coś dodano.
     * UWAGA: zapis przez snakeyaml nie zachowuje komentarzy — przy aktualizacji
     * dodającej klucze plik traci komentarze (klucze i wartości pozostają).
     */
    private static void mergeMissingKeys(Path file, String templateContent) {
        if (!Files.exists(file)) return;
        Map<String, Object> user = parseYaml(file);
        if (user == null) return; // nie nadpisujemy uszkodzonego pliku
        Object tpl;
        try {
            tpl = new Yaml().load(templateContent);
        } catch (Exception e) {
            return;
        }
        if (!(tpl instanceof Map<?, ?> templateMap)) return;

        Map<String, Object> merged = new LinkedHashMap<>(user);
        @SuppressWarnings("unchecked")
        boolean changed = deepMergeMissing(merged, (Map<String, Object>) templateMap);
        if (!changed) return;

        try {
            DumperOptions opts = new DumperOptions();
            opts.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            opts.setPrettyFlow(true);
            opts.setIndent(2);
            String out = new Yaml(opts).dump(merged);
            Files.writeString(file, out, StandardCharsets.UTF_8);
            MKTEssentials.LOGGER.info("Uzupełniono brakujące klucze w {} (wartości zachowane).", file.getFileName());
        } catch (IOException e) {
            MKTEssentials.LOGGER.error("Nie udało się zapisać scalonego configu: {}", file, e);
        }
    }

    /** Dodaje brakujące klucze z defaults do user (rekurencyjnie po mapach). Zwraca true gdy coś dodano. */
    @SuppressWarnings("unchecked")
    static boolean deepMergeMissing(Map<String, Object> user, Map<String, Object> defaults) {
        boolean changed = false;
        for (Map.Entry<String, Object> e : defaults.entrySet()) {
            String key = e.getKey();
            Object def = e.getValue();
            if (!user.containsKey(key)) {
                user.put(key, def);
                changed = true;
            } else if (user.get(key) instanceof Map<?, ?> uSub && def instanceof Map<?, ?> dSub) {
                // scalaj tylko gdy oba są mapami; w razie potrzeby zamień na mutowalną
                Map<String, Object> mutable = new LinkedHashMap<>((Map<String, Object>) uSub);
                if (deepMergeMissing(mutable, (Map<String, Object>) dSub)) {
                    user.put(key, mutable);
                    changed = true;
                }
            }
            // w przeciwnym razie: użytkownik ma wartość (skalar/lista) → zostawiamy bez zmian
        }
        return changed;
    }

    static Map<String, Object> parseYaml(Path file) {
        if (!Files.exists(file)) return null;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Yaml yaml = new Yaml();
            Object result = yaml.load(reader);
            if (result instanceof Map<?, ?> map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> typedMap = (Map<String, Object>) map;
                return typedMap;
            }
            return Map.of();
        } catch (Exception e) {
            MKTEssentials.LOGGER.error("Failed to parse YAML file: {}", file, e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static <T> T getNestedValue(Map<String, Object> map, String path, T defaultValue) {
        if (map == null) return defaultValue;
        String[] parts = path.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (current instanceof Map<?, ?> m) {
                current = m.get(part);
            } else {
                return defaultValue;
            }
        }
        if (current == null) return defaultValue;

        // For scalar defaults, fall back to the default (with a warning) when the config value has
        // the wrong type — a bad edit (e.g. a boolean written as a string) must never crash loading.
        if (defaultValue instanceof Integer) {
            return current instanceof Number n ? (T) Integer.valueOf(n.intValue()) : mismatch(path, defaultValue, current);
        }
        if (defaultValue instanceof Double) {
            return current instanceof Number n ? (T) Double.valueOf(n.doubleValue()) : mismatch(path, defaultValue, current);
        }
        if (defaultValue instanceof Boolean) {
            return current instanceof Boolean ? (T) current : mismatch(path, defaultValue, current);
        }
        if (defaultValue instanceof String) {
            return current instanceof String ? (T) current : mismatch(path, defaultValue, current);
        }
        // List / Object / null defaults: return the raw value; the caller checks its type.
        return (T) current;
    }

    private static <T> T mismatch(String path, T defaultValue, Object current) {
        MKTEssentials.LOGGER.warn("Config value '{}' has the wrong type ({}); using default '{}'.",
                path, current.getClass().getSimpleName(), defaultValue);
        return defaultValue;
    }

    static Path getLangDir() {
        return langDir;
    }
}

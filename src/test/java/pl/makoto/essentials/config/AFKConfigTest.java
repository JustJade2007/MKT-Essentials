package pl.makoto.essentials.config;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AFKConfigTest {

    @Test
    void testAfkSettingsEnabled() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> afkSection = new HashMap<>();
        afkSection.put("enabled", true);
        afkSection.put("timeout", 120);
        map.put("afk", afkSection);

        Settings.loadSettings(map);
        assertTrue(Settings.isAfkEnabled());
        assertEquals(120, Settings.getAfkTimeout());
    }

    @Test
    void testAfkSettingsDisabled() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> afkSection = new HashMap<>();
        afkSection.put("enabled", false);
        afkSection.put("timeout", 0);
        map.put("afk", afkSection);

        Settings.loadSettings(map);
        assertFalse(Settings.isAfkEnabled());
        assertEquals(0, Settings.getAfkTimeout());
    }

    @Test
    void testAfkCommandToggleInCommandsYml() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> utilitySection = new HashMap<>();
        utilitySection.put("afk", false);
        map.put("utility", utilitySection);

        Settings.loadCommands(map);
        assertFalse(Settings.isCommandEnabled("afk"));

        utilitySection.put("afk", true);
        Settings.loadCommands(map);
        assertTrue(Settings.isCommandEnabled("afk"));
    }

    @Test
    void testDefaultTemplatesIncludeAfkSettings() {
        Yaml yaml = new Yaml();
        Map<String, Object> settings = yaml.load(DefaultTemplates.SETTINGS_YML);
        assertNotNull(settings);
        assertEquals(Boolean.TRUE, ConfigManager.getNestedValue(settings, "afk.enabled", false));
        assertEquals(300, ConfigManager.getNestedValue(settings, "afk.timeout", 0));

        Map<String, Object> commands = yaml.load(DefaultTemplates.COMMANDS_YML);
        assertNotNull(commands);
        assertEquals(Boolean.TRUE, ConfigManager.getNestedValue(commands, "utility.afk", false));

        Map<String, Object> enUs = yaml.load(DefaultTemplates.LANG_EN_US);
        assertNotNull(enUs);
        assertNotNull(ConfigManager.getNestedValue(enUs, "afk.disabled", (String) null));

        Map<String, Object> plPl = yaml.load(DefaultTemplates.LANG_PL_PL);
        assertNotNull(plPl);
        assertNotNull(ConfigManager.getNestedValue(plPl, "afk.disabled", (String) null));
    }

    @Test
    void testAfkDirectBooleanInSettings() {
        Map<String, Object> map = new HashMap<>();
        map.put("afk", false);
        Settings.loadSettings(map);
        assertFalse(Settings.isAfkEnabled());
        assertFalse(Settings.isCommandEnabled("afk"));

        map.put("afk", "false");
        Settings.loadSettings(map);
        assertFalse(Settings.isAfkEnabled());
        assertFalse(Settings.isCommandEnabled("afk"));

        map.put("afk", "off");
        Settings.loadSettings(map);
        assertFalse(Settings.isAfkEnabled());

        map.put("afk", true);
        Settings.loadSettings(map);
        assertTrue(Settings.isAfkEnabled());
    }

    @Test
    void testAfkTopLevelToggleInCommandsYml() {
        Map<String, Object> map = new HashMap<>();
        map.put("afk", false);
        Settings.loadCommands(map);
        assertFalse(Settings.isCommandEnabled("afk"));
        assertFalse(Settings.isCommandEnabled("AFK"));

        map.put("afk", "off");
        Settings.loadCommands(map);
        assertFalse(Settings.isCommandEnabled("afk"));

        map.put("afk", 0);
        Settings.loadCommands(map);
        assertFalse(Settings.isCommandEnabled("afk"));
    }

    @Test
    void testAfkDottedKeyInCommandsYml() {
        Map<String, Object> map = new HashMap<>();
        map.put("utility.afk", false);
        Settings.loadCommands(map);
        assertFalse(Settings.isCommandEnabled("afk"));
    }

    @Test
    void testAfkDisabledInSettingsOverridesCommandsEnabled() {
        Map<String, Object> settingsMap = new HashMap<>();
        settingsMap.put("afk", false);
        Settings.loadSettings(settingsMap);

        Map<String, Object> commandsMap = new HashMap<>();
        Map<String, Object> utilitySection = new HashMap<>();
        utilitySection.put("afk", true);
        commandsMap.put("utility", utilitySection);
        Settings.loadCommands(commandsMap);

        assertFalse(Settings.isCommandEnabled("afk"));
    }

    @Test
    void testDeepMergeAddsMissingAfkKeys() {
        Yaml yaml = new Yaml();
        Map<String, Object> defaultCommands = yaml.load(DefaultTemplates.COMMANDS_YML);
        Map<String, Object> userCommands = new HashMap<>();
        userCommands.put("utility", new HashMap<>(Map.of("repair", true)));

        assertTrue(ConfigManager.deepMergeMissing(userCommands, defaultCommands));
        assertEquals(Boolean.TRUE, ConfigManager.getNestedValue(userCommands, "utility.afk", false));
    }
}

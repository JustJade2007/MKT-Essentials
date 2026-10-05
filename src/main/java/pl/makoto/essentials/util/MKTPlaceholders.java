package pl.makoto.essentials.util;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import pl.makoto.essentials.MKTEssentials;
import pl.makoto.essentials.data.DataManager;
import pl.makoto.essentials.data.PlayerData;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MKT's own placeholders.
 * <p>
 * The values are resolved <b>internally</b> ({@link #resolve}) so MKT never depends on an external
 * mod for its own tokens — the Text Placeholder API ({@code eu.pb4.placeholders}) is only an
 * <i>optional</i> integration used to expose these tokens to other mods ({@link #register}). This
 * mirrors how FlectonePulse resolves its own tokens with internal resolvers and treats external
 * placeholder systems purely as integrations.
 */
public final class MKTPlaceholders {

    @FunctionalInterface
    private interface Handler {
        String resolve(ServerPlayer player, String argument);
    }

    // %mktessentials:key%  or  %mktessentials:key/arg%
    private static final Pattern PATTERN = Pattern.compile("%mktessentials:([a-zA-Z_]+)(?:/([^%]*))?%");
    private static final Map<String, Handler> HANDLERS = buildHandlers();
    private static boolean registered;

    private MKTPlaceholders() {}

    private static Map<String, Handler> buildHandlers() {
        Map<String, Handler> h = new LinkedHashMap<>();
        h.put("full_name", (player, arg) -> PlayerListener.getFullDisplayName(player, !"safe".equalsIgnoreCase(arg)));
        h.put("tab_full_name", (player, arg) -> PlayerListener.getFullDisplayNameForTab(player));
        h.put("prefix", (player, arg) -> PlayerListener.getPrefixForTab(player));
        h.put("suffix", (player, arg) -> PlayerListener.getSuffixForTab(player));
        h.put("name", (player, arg) -> getName(player));
        h.put("username", (player, arg) -> getName(player));
        h.put("real_name", (player, arg) -> player.getScoreboardName());
        h.put("nick", (player, arg) -> {
            String nickname = DataManager.getPlayerData(player.getUUID()).getNickname();
            return nickname != null ? LegacyCodeConverter.fromMiniMessage(nickname) : "";
        });
        h.put("uuid", (player, arg) -> player.getUUID().toString());
        h.put("world", (player, arg) -> worldName(player));
        h.put("dot", (player, arg) -> {
            PlayerData data = DataManager.getPlayerData(player.getUUID());
            if (data.isRecording()) return "&c● &r";
            if (data.isStreaming()) return "&d● &r";
            return "";
        });
        h.put("recording", (player, arg) -> String.valueOf(DataManager.getPlayerData(player.getUUID()).isRecording()));
        h.put("streaming", (player, arg) -> String.valueOf(DataManager.getPlayerData(player.getUUID()).isStreaming()));
        return h;
    }

    /** Friendly dimension name for the player's current world. */
    private static String worldName(ServerPlayer player) {
        String id = player.level().dimension().location().toString();
        return switch (id) {
            case "minecraft:overworld" -> "Overworld";
            case "minecraft:the_nether" -> "Nether";
            case "minecraft:the_end" -> "End";
            default -> {
                String path = player.level().dimension().location().getPath();
                yield path.isEmpty() ? id : Character.toUpperCase(path.charAt(0)) + path.substring(1).replace('_', ' ');
            }
        };
    }

    /**
     * Resolves all {@code %mktessentials:*%} placeholders in the text for the given player.
     * Unknown MKT tokens and non-MKT placeholders are left untouched. Requires no external mod.
     */
    public static String resolve(ServerPlayer player, String text) {
        if (player == null || text == null || text.isEmpty() || text.indexOf('%') == -1) {
            return text == null ? "" : text;
        }

        Matcher matcher = PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            Handler handler = HANDLERS.get(matcher.group(1));
            String value = handler != null ? safe(handler.resolve(player, matcher.group(2))) : matcher.group();
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Optionally exposes MKT placeholders to the Text Placeholder API so other mods can use
     * {@code %mktessentials:*%}. No-op (logged) when that API is not installed.
     */
    public static void register() {
        if (registered) return;
        registered = true;

        HANDLERS.forEach((key, handler) ->
                Placeholders.registerServer(ResourceLocation.fromNamespaceAndPath(MKTEssentials.MODID, key),
                        (context, argument) -> {
                            ServerPlayer player = context != null ? context.serverPlayer() : null;
                            return PlaceholderResult.value(player != null ? safe(handler.resolve(player, argument)) : "");
                        }));

        MKTEssentials.LOGGER.info("Exposed MKT placeholders to the Text Placeholder API.");
    }

    private static String getName(ServerPlayer player) {
        PlayerData data = DataManager.getPlayerData(player.getUUID());
        String nickname = data.getNickname();
        if (nickname != null && !nickname.isBlank()) {
            return LegacyCodeConverter.fromMiniMessage(nickname);
        }
        String lpUser = LuckPermsHook.getUsername(player);
        if (lpUser != null && !lpUser.isBlank()) {
            return lpUser;
        }
        return player.getScoreboardName();
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

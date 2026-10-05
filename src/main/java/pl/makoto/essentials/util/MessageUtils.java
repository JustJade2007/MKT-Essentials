package pl.makoto.essentials.util;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.ServerPlaceholderContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import pl.makoto.essentials.config.Settings;

public class MessageUtils {

    /**
     * Maximum visible character length for player-authored content after formatting codes are stripped.
     */
    private static final int MAX_VISIBLE_LENGTH = 256;

    public static MutableComponent format(String text) {
        if (text == null || text.isEmpty()) return Component.literal("");
        return formatBypass(text);
    }

    public static MutableComponent format(ServerPlayer player, String text) {
        if (player == null) return format(AnimationManager.apply(text));
        // 0. Resolve <animation:name> tags to the current frame (server-controlled).
        text = AnimationManager.apply(text);
        // 1. Resolve MKT's own placeholders internally — works without any external mod.
        String resolved = MKTPlaceholders.resolve(player, normalizeLegacyPlaceholders(text));
        // 2. Optionally resolve any remaining third-party placeholders via the Text Placeholder API.
        resolved = tryExternalPlaceholders(player, resolved);
        return format(resolved);
    }

    /** Resolves non-MKT placeholders via the optional Text Placeholder API; a no-op if it's absent. */
    private static String tryExternalPlaceholders(ServerPlayer player, String text) {
        if (text == null || text.indexOf('%') == -1) return text;
        try {
            Component parsed = Placeholders.parseText(text, ServerPlaceholderContext.of(player));
            return parsed.getString();
        } catch (LinkageError e) {
            // Placeholder API absent, or an older build with a different API surface.
            return text;
        }
    }

    /**
     * Formats player-authored text with MiniMessage + legacy codes, applying permission filtering.
     * Pipeline: LegacyCodeConverter → PermissionFilter → MiniMessageParser
     * Enforces a 256-character visible text limit after formatting codes are stripped.
     *
     * @param player the player whose permissions are checked
     * @param text   raw player input text
     * @return styled MutableComponent
     */
    public static MutableComponent formatWithPermissions(ServerPlayer player, String text) {
        return formatWithPermissions(player, text, null);
    }

    /**
     * Same as {@link #formatWithPermissions(ServerPlayer, String)}, but applies an optional
     * server-controlled transform to the string AFTER permission filtering and BEFORE parsing.
     * This lets callers inject markup the player itself is not allowed to use (e.g. mention
     * highlighting) without it being stripped by the permission filter.
     *
     * @param postFilter transform applied to the filtered MiniMessage string (nullable)
     */
    public static MutableComponent formatWithPermissions(ServerPlayer player, String text,
                                                         java.util.function.UnaryOperator<String> postFilter) {
        if (text == null || text.isEmpty()) return Component.literal("");

        // Step 1: Convert legacy & codes to MiniMessage equivalents
        String converted = LegacyCodeConverter.convert(text);

        // Step 2: Filter tags by player permissions
        String filtered = PermissionFilter.filter(player, converted);

        // Step 3: Optional server-controlled transform (e.g. mention highlighting)
        if (postFilter != null) filtered = postFilter.apply(filtered);

        // Step 3a: Interactive replacements (clickable links, spoilers, emoji) — server-controlled
        filtered = ReplacementManager.apply(filtered);

        // Step 3b: Resolve <animation:name> tags (server-controlled, after permission filtering)
        filtered = AnimationManager.apply(filtered);

        // Step 4: Enforce 256-character visible text limit
        filtered = enforceVisibleLimit(filtered);

        // Step 5: Parse MiniMessage into a styled Component
        return MiniMessageParser.parse(filtered);
    }

    /**
     * Formats text with MiniMessage + legacy codes, bypassing permission checks.
     * Pipeline: LegacyCodeConverter → MiniMessageParser (no permission filter, no truncation)
     * Used for admin/system messages (broadcasts, config-defined messages).
     *
     * @param text raw text input
     * @return styled MutableComponent
     */
    /**
     * Permission-filtered legacy section-code string (for contexts rendered from a plain string,
     * e.g. an anvil item name). Disallowed color/format tags are stripped. Hex colors degrade.
     */
    public static String legacyColors(ServerPlayer player, String text) {
        if (text == null || text.isEmpty()) return text == null ? "" : text;
        String converted = LegacyCodeConverter.convert(text);
        if (Settings.isMarkdownEnabled()) converted = ReplacementManager.markdown(converted);
        String filtered = PermissionFilter.filter(player, converted);
        return toLegacySection(filtered);
    }

    public static String toLegacySection(String text) {
        if (text == null || text.isEmpty()) return text == null ? "" : text;
        String legacy = LegacyCodeConverter.fromMiniMessage(text);
        // Convert &#RRGGBB to §x§r§r§g§g§b§b (vanilla Minecraft 1.16+ legacy hex format)
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(
                "(?i)&#([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f])").matcher(legacy);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "§x§" + matcher.group(1).toLowerCase() + "§" + matcher.group(2).toLowerCase()
                    + "§" + matcher.group(3).toLowerCase() + "§" + matcher.group(4).toLowerCase()
                    + "§" + matcher.group(5).toLowerCase() + "§" + matcher.group(6).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString().replace('&', '§');
    }

    /** Same as {@link #formatBypass(String)} but resolves MKT placeholders for the given player first. */
    public static MutableComponent formatBypass(ServerPlayer player, String text) {
        if (player == null) return formatBypass(text);
        return formatBypass(MKTPlaceholders.resolve(player, normalizeLegacyPlaceholders(text)));
    }

    public static MutableComponent formatBypass(String text) {
        if (text == null || text.isEmpty()) return Component.literal("");

        // Step 0: Resolve <animation:name> tags + interactive replacements (links/spoiler/emoji)
        text = AnimationManager.apply(text);
        text = ReplacementManager.apply(text);

        // Step 1: Convert legacy & codes to MiniMessage equivalents
        String converted = LegacyCodeConverter.convert(text);

        // Step 2: Parse MiniMessage into a styled Component (no permission filtering, no truncation)
        return MiniMessageParser.parse(converted);
    }

    public static MutableComponent prefixed(String text) {
        String prefix = Settings.getMessagePrefix();
        return format(prefix + text);
    }

    /**
     * Enforces the 256-character visible text limit on a MiniMessage-formatted string.
     * Strips all MiniMessage tags to count visible characters, then truncates the input
     * at the point where visible characters exceed the limit while preserving tag structure.
     *
     * @param input MiniMessage-formatted string
     * @return truncated string if visible text exceeds 256 chars, otherwise unchanged
     */
    private static String enforceVisibleLimit(String input) {
        if (input == null || input.isEmpty()) return input;

        // Count visible characters (everything outside of < > tags)
        String visibleText = stripTags(input);
        if (visibleText.length() <= MAX_VISIBLE_LENGTH) {
            return input;
        }

        // Truncate: walk through the input, counting visible chars, and cut at the limit
        StringBuilder result = new StringBuilder();
        int visibleCount = 0;
        int i = 0;

        while (i < input.length() && visibleCount < MAX_VISIBLE_LENGTH) {
            if (input.charAt(i) == '<') {
                // Find the closing '>'
                int closeIdx = input.indexOf('>', i);
                if (closeIdx == -1) {
                    // No closing '>' — rest is visible text
                    int remaining = MAX_VISIBLE_LENGTH - visibleCount;
                    int end = Math.min(i + remaining, input.length());
                    result.append(input, i, end);
                    visibleCount += (end - i);
                    i = end;
                } else {
                    // Append the entire tag (doesn't count as visible)
                    result.append(input, i, closeIdx + 1);
                    i = closeIdx + 1;
                }
            } else {
                result.append(input.charAt(i));
                visibleCount++;
                i++;
            }
        }

        return result.toString();
    }

    /**
     * Strips all MiniMessage tags from the input, returning only visible text.
     */
    private static String stripTags(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (input.charAt(i) == '<') {
                int closeIdx = input.indexOf('>', i);
                if (closeIdx == -1) {
                    // No closing '>' — rest is literal text
                    sb.append(input, i, input.length());
                    break;
                } else {
                    i = closeIdx + 1;
                }
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    private static String normalizeLegacyPlaceholders(String text) {
        if (text == null || text.isEmpty()) return "";
        return text
                .replace("{player}", "%mktessentials:full_name/safe%")
                .replace("{username}", "%mktessentials:username%")
                .replace("{dot}", "%mktessentials:dot%")
                .replace("{prefix}", "%mktessentials:prefix%")
                .replace("{name}", "%mktessentials:name%")
                .replace("{suffix}", "%mktessentials:suffix%");
    }
}

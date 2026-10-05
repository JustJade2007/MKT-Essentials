package pl.makoto.essentials.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converts legacy ampersand (&) color/decoration codes into MiniMessage tag equivalents.
 * <p>
 * Supports:
 * <ul>
 *   <li>&0–&9, &a–&f (color codes, case-insensitive)</li>
 *   <li>&l, &o, &n, &m, &k (decoration codes, case-insensitive)</li>
 *   <li>&r (reset, case-insensitive)</li>
 *   <li>&#RRGGBB (hex color codes)</li>
 * </ul>
 */
public class LegacyCodeConverter {

    private static final Map<Character, String> COLOR_MAP = Map.ofEntries(
            Map.entry('0', "<black>"),
            Map.entry('1', "<dark_blue>"),
            Map.entry('2', "<dark_green>"),
            Map.entry('3', "<dark_aqua>"),
            Map.entry('4', "<dark_red>"),
            Map.entry('5', "<dark_purple>"),
            Map.entry('6', "<gold>"),
            Map.entry('7', "<gray>"),
            Map.entry('8', "<dark_gray>"),
            Map.entry('9', "<blue>"),
            Map.entry('a', "<green>"),
            Map.entry('b', "<aqua>"),
            Map.entry('c', "<red>"),
            Map.entry('d', "<light_purple>"),
            Map.entry('e', "<yellow>"),
            Map.entry('f', "<white>")
    );

    private static final Map<Character, String> DECORATION_MAP = Map.of(
            'l', "<bold>",
            'o', "<italic>",
            'n', "<underlined>",
            'm', "<strikethrough>",
            'k', "<obfuscated>"
    );

    /**
     * Pattern to match hex color codes: &#RRGGBB or §#RRGGBB (6 hex digits).
     */
    private static final Pattern HEX_PATTERN = Pattern.compile("(?i)[&§]#([0-9a-f]{6})");

    /**
     * Pattern to match BungeeCord/Spigot legacy hex codes:
     * &x&r&r&g&g&b&b or §x§r§r§g§g§b§b (or any combination of & and §).
     */
    private static final Pattern BUNGEE_HEX_PATTERN = Pattern.compile(
            "(?i)[&§]x[&§]([0-9a-f])[&§]([0-9a-f])[&§]([0-9a-f])[&§]([0-9a-f])[&§]([0-9a-f])[&§]([0-9a-f])");

    /**
     * Pattern to match MiniMessage shorthand hex open tags: <#RRGGBB>.
     */
    private static final Pattern MINIMESSAGE_HEX_OPEN = Pattern.compile("(?i)<#([0-9a-f]{6})>");

    /**
     * Pattern to match MiniMessage shorthand hex close tags: </#RRGGBB>.
     */
    private static final Pattern MINIMESSAGE_HEX_CLOSE = Pattern.compile("(?i)</#([0-9a-f]{6})>");

    /**
     * Converts all legacy & and § codes, BungeeCord hex, and &#RRGGBB hex codes in the input
     * to their MiniMessage tag equivalents.
     *
     * @param input raw text with potential color/decoration codes
     * @return text with codes replaced by MiniMessage tags
     */
    public static String convert(String input) {
        if (input == null || input.isEmpty()) {
            return input == null ? "" : input;
        }

        // First pass: convert all hex formats → <color:#RRGGBB>
        String result = convertHexCodes(input);

        // Second pass: convert standard &X and §X codes
        result = convertStandardCodes(result);

        return result;
    }

    /** Reverse of the color/decoration maps: MiniMessage tag name → legacy code character. */
    private static final Map<String, Character> REVERSE_MAP = buildReverseMap();

    private static Map<String, Character> buildReverseMap() {
        Map<String, Character> map = new java.util.HashMap<>();
        COLOR_MAP.forEach((code, tag) -> map.put(tag.substring(1, tag.length() - 1), code));
        DECORATION_MAP.forEach((code, tag) -> map.put(tag.substring(1, tag.length() - 1), code));
        map.put("b", 'l');
        map.put("i", 'o');
        map.put("em", 'o');
        map.put("u", 'n');
        map.put("st", 'm');
        map.put("obf", 'k');
        map.put("reset", 'r');
        return Map.copyOf(map);
    }

    // <tag>, </tag>, <#RRGGBB>, </#RRGGBB>, <color:name> or <color:#RRGGBB>
    private static final Pattern MINI_TAG = Pattern.compile("(?i)<(/?)(#?[a-z0-9_]+)(?::#?([0-9a-f]{6}|[a-z_]+))?>");

    /**
     * Converts MiniMessage color/decoration tags back into legacy {@code &} codes so a nickname
     * or LuckPerms string renders correctly through MKT's legacy display pipeline and TAB integration.
     * Unrecognized tags are left untouched; recognized closing tags are dropped.
     */
    public static String fromMiniMessage(String input) {
        if (input == null || input.isEmpty()) return input == null ? "" : input;
        if (input.indexOf('<') == -1) return input; // fast path: no tags

        Matcher matcher = MINI_TAG.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            boolean closing = !matcher.group(1).isEmpty();
            String name = matcher.group(2).toLowerCase();
            String arg = matcher.group(3);
            String replacement;

            if (name.startsWith("#") && name.length() == 7 && name.substring(1).matches("[0-9a-f]{6}")) {
                replacement = closing ? "" : "&#" + name.substring(1).toUpperCase();
            } else if (closing) {
                replacement = (name.equals("color") || REVERSE_MAP.containsKey(name)) ? "" : matcher.group();
            } else if (name.equals("color") && arg != null) {
                if (arg.matches("(?i)[0-9a-f]{6}")) {
                    replacement = "&#" + arg.toUpperCase();
                } else {
                    Character code = REVERSE_MAP.get(arg.toLowerCase());
                    replacement = code != null ? "&" + code : matcher.group();
                }
            } else if (REVERSE_MAP.containsKey(name)) {
                replacement = "&" + REVERSE_MAP.get(name);
            } else {
                replacement = matcher.group(); // unknown tag — keep literal
            }

            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String convertHexCodes(String input) {
        // 1. MiniMessage shorthand closing tags: </#RRGGBB> → </color>
        input = MINIMESSAGE_HEX_CLOSE.matcher(input).replaceAll("</color>");

        // 2. MiniMessage shorthand opening tags: <#RRGGBB> → <color:#RRGGBB>
        Matcher tagMatcher = MINIMESSAGE_HEX_OPEN.matcher(input);
        StringBuilder sbTag = new StringBuilder();
        while (tagMatcher.find()) {
            tagMatcher.appendReplacement(sbTag, "<color:#" + tagMatcher.group(1).toUpperCase() + ">");
        }
        tagMatcher.appendTail(sbTag);
        input = sbTag.toString();

        // 3. BungeeCord / Spigot hex: &x&r&r&g&g&b&b or §x§r§r§g§g§b§b
        Matcher bungeeMatcher = BUNGEE_HEX_PATTERN.matcher(input);
        StringBuilder sbBungee = new StringBuilder();
        while (bungeeMatcher.find()) {
            String hex = (bungeeMatcher.group(1) + bungeeMatcher.group(2) + bungeeMatcher.group(3)
                    + bungeeMatcher.group(4) + bungeeMatcher.group(5) + bungeeMatcher.group(6)).toUpperCase();
            bungeeMatcher.appendReplacement(sbBungee, "<color:#" + hex + ">");
        }
        bungeeMatcher.appendTail(sbBungee);
        input = sbBungee.toString();

        // 4. Standard hex: &#RRGGBB or §#RRGGBB
        Matcher matcher = HEX_PATTERN.matcher(input);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hex = matcher.group(1).toUpperCase();
            matcher.appendReplacement(sb, "<color:#" + hex + ">");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String convertStandardCodes(String input) {
        StringBuilder result = new StringBuilder(input.length());
        int i = 0;

        while (i < input.length()) {
            char c = input.charAt(i);
            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(i + 1));

                if (code == 'r') {
                    result.append("<reset>");
                    i += 2;
                } else if (COLOR_MAP.containsKey(code)) {
                    result.append(COLOR_MAP.get(code));
                    i += 2;
                } else if (DECORATION_MAP.containsKey(code)) {
                    result.append(DECORATION_MAP.get(code));
                    i += 2;
                } else {
                    // Not a recognized code, keep the marker as-is
                    result.append(c);
                    i++;
                }
            } else {
                result.append(c);
                i++;
            }
        }

        return result.toString();
    }
}

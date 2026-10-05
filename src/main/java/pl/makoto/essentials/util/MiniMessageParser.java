package pl.makoto.essentials.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * Parses a MiniMessage-formatted string (after permission filtering) into a Minecraft {@link MutableComponent}.
 * <p>
 * Supported tags:
 * <ul>
 *   <li>Decorations: bold/b, italic/i/em, underlined/u, strikethrough/st, obfuscated/obf</li>
 *   <li>Colors: named colors (red, blue, etc.) and hex colors (color:#RRGGBB)</li>
 *   <li>Gradient: gradient:color1:color2[:color3...], rainbow, pride:flag, transition:color1:color2</li>
 *   <li>Events: hover:show_text:content, click:action:value, insertion:text</li>
 *   <li>Special: score:name:objective, selector:pattern, font:namespace:key, translatable/lang:key, keybind:key, nbt</li>
 *   <li>Shadow: pass-through (not natively supported in 1.21.1)</li>
 *   <li>Reset: clears all formatting</li>
 *   <li>Newline/br: inserts a newline character</li>
 * </ul>
 * <p>
 * Behaviors:
 * <ul>
 *   <li>Tag names are case-insensitive</li>
 *   <li>Unrecognized or malformed tags are rendered as literal text</li>
 *   <li>Unmatched closing tags are silently ignored (not rendered)</li>
 * </ul>
 */
public class MiniMessageParser {

    // --- Internal AST node types ---

    sealed interface Node permits TextNode, TagNode {}

    record TextNode(String content) implements Node {}

    record TagNode(String name, String args, List<Node> children) implements Node {}

    // --- Decoration alias mapping ---

    private static final Map<String, String> DECORATION_CANONICAL = Map.ofEntries(
            Map.entry("bold", "bold"),
            Map.entry("b", "bold"),
            Map.entry("italic", "italic"),
            Map.entry("i", "italic"),
            Map.entry("em", "italic"),
            Map.entry("underlined", "underlined"),
            Map.entry("u", "underlined"),
            Map.entry("strikethrough", "strikethrough"),
            Map.entry("st", "strikethrough"),
            Map.entry("obfuscated", "obfuscated"),
            Map.entry("obf", "obfuscated")
    );

    // --- Named color mapping ---

    private static final Map<String, ChatFormatting> NAMED_COLORS = Map.ofEntries(
            Map.entry("black", ChatFormatting.BLACK),
            Map.entry("dark_blue", ChatFormatting.DARK_BLUE),
            Map.entry("dark_green", ChatFormatting.DARK_GREEN),
            Map.entry("dark_aqua", ChatFormatting.DARK_AQUA),
            Map.entry("dark_red", ChatFormatting.DARK_RED),
            Map.entry("dark_purple", ChatFormatting.DARK_PURPLE),
            Map.entry("gold", ChatFormatting.GOLD),
            Map.entry("gray", ChatFormatting.GRAY),
            Map.entry("dark_gray", ChatFormatting.DARK_GRAY),
            Map.entry("blue", ChatFormatting.BLUE),
            Map.entry("green", ChatFormatting.GREEN),
            Map.entry("aqua", ChatFormatting.AQUA),
            Map.entry("red", ChatFormatting.RED),
            Map.entry("light_purple", ChatFormatting.LIGHT_PURPLE),
            Map.entry("yellow", ChatFormatting.YELLOW),
            Map.entry("white", ChatFormatting.WHITE)
    );

    /**
     * Parses MiniMessage tags into a styled Component tree.
     * Unrecognized or malformed tags are rendered as literal text.
     *
     * @param input MiniMessage-formatted string
     * @return styled MutableComponent
     */
    public static MutableComponent parse(String input) {
        if (input == null || input.isEmpty()) {
            return Component.literal("");
        }

        List<Token> tokens = tokenize(input);
        List<Node> tree = buildTree(tokens);
        return renderNodes(tree, Style.EMPTY);
    }

    // ========================
    // Tokenization
    // ========================

    private sealed interface Token permits TextToken, OpenTagToken, CloseTagToken {}

    private record TextToken(String text) implements Token {}

    private record OpenTagToken(String name, String args, String raw) implements Token {}

    private record CloseTagToken(String name, String raw) implements Token {}

    /**
     * Tokenizes the input string into text segments, opening tags, and closing tags.
     * Malformed tags (unclosed &lt;, etc.) are treated as literal text.
     */
    private static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder textBuffer = new StringBuilder();
        int i = 0;

        while (i < input.length()) {
            if (input.charAt(i) == '<') {
                // Try to find the closing '>'
                int closeIdx = input.indexOf('>', i);
                if (closeIdx == -1) {
                    // No closing '>' found — treat rest as literal text
                    textBuffer.append(input, i, input.length());
                    i = input.length();
                } else {
                    String tagContent = input.substring(i + 1, closeIdx);
                    String rawTag = input.substring(i, closeIdx + 1);

                    if (tagContent.isEmpty()) {
                        // Empty tag "<>" — treat as literal
                        textBuffer.append(rawTag);
                        i = closeIdx + 1;
                    } else if (tagContent.startsWith("/")) {
                        // Closing tag
                        String closeName = tagContent.substring(1).trim().toLowerCase();
                        if (closeName.isEmpty()) {
                            // "</>" — treat as literal
                            textBuffer.append(rawTag);
                        } else if (isRecognizedTag(closeName)) {
                            // Flush text buffer
                            if (!textBuffer.isEmpty()) {
                                tokens.add(new TextToken(textBuffer.toString()));
                                textBuffer.setLength(0);
                            }
                            tokens.add(new CloseTagToken(closeName, rawTag));
                        } else {
                            // Unrecognized closing tag — render as literal text
                            textBuffer.append(rawTag);
                        }
                        i = closeIdx + 1;
                    } else {
                        // Opening tag — parse name and args
                        String normalized = tagContent.toLowerCase();
                        String tagName;
                        String tagArgs = "";

                        int colonIdx = normalized.indexOf(':');
                        if (colonIdx != -1) {
                            tagName = normalized.substring(0, colonIdx).trim();
                            tagArgs = tagContent.substring(colonIdx + 1).trim(); // preserve original case for args
                        } else {
                            tagName = normalized.trim();
                        }

                        if (isRecognizedOpenTag(tagName, tagArgs)) {
                            // Flush text buffer
                            if (!textBuffer.isEmpty()) {
                                tokens.add(new TextToken(textBuffer.toString()));
                                textBuffer.setLength(0);
                            }
                            tokens.add(new OpenTagToken(tagName, tagArgs, rawTag));
                        } else {
                            // Unrecognized tag — render as literal text
                            textBuffer.append(rawTag);
                        }
                        i = closeIdx + 1;
                    }
                }
            } else {
                textBuffer.append(input.charAt(i));
                i++;
            }
        }

        // Flush remaining text
        if (!textBuffer.isEmpty()) {
            tokens.add(new TextToken(textBuffer.toString()));
        }

        return tokens;
    }

    // --- Advanced tag names ---

    private static final Set<String> ADVANCED_TAGS = Set.of(
            "gradient", "rainbow", "pride", "transition", "shadow",
            "hover", "click", "insertion",
            "score", "selector", "font", "translatable", "lang", "keybind", "nbt"
    );

    // --- Predefined pride flag color palettes ---

    private static final Map<String, int[]> PRIDE_PALETTES = Map.of(
            "rainbow", new int[]{0xE40303, 0xFF8C00, 0xFFED00, 0x008026, 0x24408E, 0x732982},
            "trans", new int[]{0x5BCEFA, 0xF5A9B8, 0xFFFFFF, 0xF5A9B8, 0x5BCEFA},
            "bi", new int[]{0xD60270, 0x9B4F96, 0x0038A8},
            "pan", new int[]{0xFF218C, 0xFFD800, 0x21B1FF},
            "nb", new int[]{0xFCF434, 0xFFFFFF, 0x9C59D1, 0x2C2C2C},
            "lesbian", new int[]{0xD52D00, 0xFF9A56, 0xFFFFFF, 0xD362A4, 0xA30262},
            "ace", new int[]{0x000000, 0xA3A3A3, 0xFFFFFF, 0x800080}
    );

    /**
     * Checks if a tag name (with optional args) is a recognized opening tag.
     */
    private static boolean isRecognizedOpenTag(String tagName, String tagArgs) {
        // Decorations
        if (DECORATION_CANONICAL.containsKey(tagName)) return true;
        // Named colors (direct tag name)
        if (NAMED_COLORS.containsKey(tagName)) return true;
        // Shorthand hex color tag: <#RRGGBB>
        if (tagName.startsWith("#") && isValidHex(tagName.substring(1))) return true;
        // color:arg syntax
        if (tagName.equals("color") && !tagArgs.isEmpty()) {
            return isValidColorArg(tagArgs.toLowerCase());
        }
        // Reset
        if (tagName.equals("reset")) return true;
        // Newline/br
        if (tagName.equals("newline") || tagName.equals("br")) return true;
        // Advanced tags
        if (ADVANCED_TAGS.contains(tagName)) return true;

        return false;
    }

    /**
     * Checks if a closing tag name is recognized (matches any known tag or alias).
     */
    private static boolean isRecognizedTag(String name) {
        if (DECORATION_CANONICAL.containsKey(name)) return true;
        if (NAMED_COLORS.containsKey(name)) return true;
        if (name.startsWith("#") && isValidHex(name.substring(1))) return true;
        if (name.equals("color") || name.startsWith("color:")) return true;
        if (name.equals("reset")) return true;
        if (name.equals("newline") || name.equals("br")) return true;
        if (ADVANCED_TAGS.contains(name)) return true;
        return false;
    }

    /**
     * Validates a color argument (after "color:").
     */
    private static boolean isValidColorArg(String arg) {
        // Hex: #RRGGBB
        if (arg.startsWith("#") && arg.length() == 7) {
            return isValidHex(arg.substring(1));
        }
        // Named color
        return NAMED_COLORS.containsKey(arg);
    }

    private static boolean isValidHex(String hex) {
        if (hex == null || hex.length() != 6) return false;
        for (int i = 0; i < 6; i++) {
            char c = hex.charAt(i);
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        return true;
    }

    // ========================
    // Tree Building
    // ========================

    /**
     * Builds a tree of Nodes from the flat token list.
     * Uses a stack to handle nesting. Unmatched closing tags are silently ignored.
     */
    private static List<Node> buildTree(List<Token> tokens) {
        // Stack of (tagName, children) for open tags
        Deque<TagBuildContext> stack = new ArrayDeque<>();
        List<Node> currentChildren = new ArrayList<>();

        for (Token token : tokens) {
            switch (token) {
                case TextToken t -> currentChildren.add(new TextNode(t.text()));

                case OpenTagToken t -> {
                    // Push current context onto stack
                    stack.push(new TagBuildContext(t.name(), t.args(), currentChildren));
                    currentChildren = new ArrayList<>();
                }

                case CloseTagToken t -> {
                    // Find matching open tag on the stack
                    String closeName = t.name();
                    String canonicalClose = getCanonicalName(closeName);

                    // Search the stack for a matching open tag
                    TagBuildContext matched = null;
                    Deque<TagBuildContext> tempStack = new ArrayDeque<>();

                    for (TagBuildContext ctx : stack) {
                        String canonicalOpen = getCanonicalName(ctx.name());
                        if (canonicalOpen.equals(canonicalClose) ||
                                (ctx.name().equals("color") && closeName.equals("color"))) {
                            matched = ctx;
                            break;
                        }
                        tempStack.push(ctx);
                    }

                    if (matched != null) {
                        // Pop everything up to and including the matched context
                        // First, collapse any intermediate open tags that weren't closed
                        List<Node> innerChildren = currentChildren;

                        // Pop intermediate contexts (they become unclosed — wrap their children)
                        while (!stack.isEmpty()) {
                            TagBuildContext top = stack.pop();
                            if (top == matched) {
                                // This is our match — create the TagNode
                                TagNode tagNode = new TagNode(top.name(), top.args(), innerChildren);
                                currentChildren = top.parentChildren();
                                currentChildren.add(tagNode);
                                break;
                            } else {
                                // Intermediate unclosed tag — wrap its children into a TagNode too
                                TagNode intermediateNode = new TagNode(top.name(), top.args(), innerChildren);
                                innerChildren = top.parentChildren();
                                innerChildren.add(intermediateNode);
                            }
                        }
                    }
                    // If no match found, silently ignore the closing tag (don't render it)
                }
            }
        }

        // Close any remaining open tags on the stack (unclosed tags at end of input)
        while (!stack.isEmpty()) {
            TagBuildContext ctx = stack.pop();
            TagNode tagNode = new TagNode(ctx.name(), ctx.args(), currentChildren);
            currentChildren = ctx.parentChildren();
            currentChildren.add(tagNode);
        }

        return currentChildren;
    }

    private record TagBuildContext(String name, String args, List<Node> parentChildren) {}

    /**
     * Returns the canonical name for a tag (resolves aliases).
     * Used for matching closing tags to opening tags.
     */
    private static String getCanonicalName(String name) {
        // Shorthand hex color
        if (name.startsWith("#") && isValidHex(name.substring(1))) return "color";
        if (name.startsWith("color:")) return "color";
        // Decoration aliases
        String decoration = DECORATION_CANONICAL.get(name);
        if (decoration != null) return decoration;
        // Newline aliases
        if (name.equals("br")) return "newline";
        // Translatable alias
        if (name.equals("lang")) return "translatable";
        // Named colors and other tags are their own canonical name
        return name;
    }

    // ========================
    // Rendering
    // ========================

    /**
     * Renders a list of nodes into a MutableComponent with the given inherited style.
     */
    private static MutableComponent renderNodes(List<Node> nodes, Style parentStyle) {
        MutableComponent result = Component.empty();

        for (Node node : nodes) {
            switch (node) {
                case TextNode t -> {
                    if (!t.content().isEmpty()) {
                        MutableComponent textComp = Component.literal(t.content());
                        textComp.setStyle(parentStyle);
                        result.append(textComp);
                    }
                }
                case TagNode t -> {
                    MutableComponent tagComponent = renderTagNode(t, parentStyle);
                    result.append(tagComponent);
                }
            }
        }

        return result;
    }

    /**
     * Renders a single TagNode by applying its style modification and recursing into children.
     */
    private static MutableComponent renderTagNode(TagNode tagNode, Style parentStyle) {
        String name = tagNode.name();
        String args = tagNode.args();

        // Handle reset — clears all formatting for children
        if (name.equals("reset")) {
            return renderNodes(tagNode.children(), Style.EMPTY);
        }

        // Handle newline/br — insert newline character, then render children
        if (name.equals("newline") || name.equals("br")) {
            MutableComponent result = Component.empty();
            MutableComponent newline = Component.literal("\n");
            newline.setStyle(parentStyle);
            result.append(newline);
            // If there are children (unlikely for newline but handle gracefully)
            if (!tagNode.children().isEmpty()) {
                result.append(renderNodes(tagNode.children(), parentStyle));
            }
            return result;
        }

        // Handle gradient tag: <gradient:color1:color2[:color3...]>
        if (name.equals("gradient")) {
            return renderGradient(tagNode, parentStyle);
        }

        // Handle rainbow tag: <rainbow>
        if (name.equals("rainbow")) {
            return renderRainbow(tagNode, parentStyle);
        }

        // Handle pride tag: <pride:flag> or <pride> (defaults to rainbow flag)
        if (name.equals("pride")) {
            return renderPride(tagNode, parentStyle);
        }

        // Handle transition tag: <transition:color1:color2[:color3...]>
        // Similar to gradient but with predefined behavior
        if (name.equals("transition")) {
            return renderTransition(tagNode, parentStyle);
        }

        // Handle shadow tag: not natively supported in 1.21.1 Component system
        // Pass through text with a slightly darker color variant as a best-effort
        if (name.equals("shadow")) {
            return renderShadow(tagNode, parentStyle);
        }

        // Handle hover tag: <hover:show_text:content>
        if (name.equals("hover")) {
            return renderHover(tagNode, parentStyle);
        }

        // Handle click tag: <click:action:value>
        if (name.equals("click")) {
            return renderClick(tagNode, parentStyle);
        }

        // Handle insertion tag: <insertion:text>
        if (name.equals("insertion")) {
            return renderInsertion(tagNode, parentStyle);
        }

        // Handle score tag: <score:name:objective>
        if (name.equals("score")) {
            return renderScore(tagNode, parentStyle);
        }

        // Handle selector tag: <selector:pattern>
        if (name.equals("selector")) {
            return renderSelector(tagNode, parentStyle);
        }

        // Handle font tag: <font:namespace:key>
        if (name.equals("font")) {
            return renderFont(tagNode, parentStyle);
        }

        // Handle translatable/lang tag: <translatable:key> or <lang:key>
        if (name.equals("translatable") || name.equals("lang")) {
            return renderTranslatable(tagNode, parentStyle);
        }

        // Handle keybind tag: <keybind:key>
        if (name.equals("keybind")) {
            return renderKeybind(tagNode, parentStyle);
        }

        // Handle nbt tag: <nbt:...> — simplified no-op, renders children as-is
        if (name.equals("nbt")) {
            // NBT tag is complex and rarely used in chat; render children without modification
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Determine the new style for this tag
        Style newStyle = applyTag(name, args, parentStyle);

        // Render children with the new style
        return renderNodes(tagNode.children(), newStyle);
    }

    /**
     * Applies a tag's formatting to the parent style and returns the new style.
     */
    private static Style applyTag(String name, String args, Style parentStyle) {
        // Decorations
        String decoration = DECORATION_CANONICAL.get(name);
        if (decoration != null) {
            return applyDecoration(decoration, parentStyle);
        }

        // Named colors (direct tag name like <red>)
        ChatFormatting namedColor = NAMED_COLORS.get(name);
        if (namedColor != null) {
            TextColor color = TextColor.fromLegacyFormat(namedColor);
            return parentStyle.withColor(color);
        }

        // Shorthand hex color: <#RRGGBB>
        if (name.startsWith("#") && isValidHex(name.substring(1))) {
            try {
                int rgb = Integer.parseInt(name.substring(1), 16);
                return parentStyle.withColor(TextColor.fromRgb(rgb));
            } catch (NumberFormatException e) {
                return parentStyle;
            }
        }

        // color:arg syntax
        if (name.equals("color") && !args.isEmpty()) {
            String colorArg = args.toLowerCase();
            if (colorArg.startsWith("#") && colorArg.length() == 7) {
                // Hex color
                try {
                    int rgb = Integer.parseInt(colorArg.substring(1), 16);
                    return parentStyle.withColor(TextColor.fromRgb(rgb));
                } catch (NumberFormatException e) {
                    return parentStyle;
                }
            } else {
                // Named color via color:name
                ChatFormatting fmt = NAMED_COLORS.get(colorArg);
                if (fmt != null) {
                    return parentStyle.withColor(TextColor.fromLegacyFormat(fmt));
                }
            }
        }

        // Fallback — no style change
        return parentStyle;
    }

    /**
     * Applies a decoration (bold, italic, etc.) to the given style.
     */
    private static Style applyDecoration(String decoration, Style style) {
        return switch (decoration) {
            case "bold" -> style.withBold(true);
            case "italic" -> style.withItalic(true);
            case "underlined" -> style.withUnderlined(true);
            case "strikethrough" -> style.withStrikethrough(true);
            case "obfuscated" -> style.withObfuscated(true);
            default -> style;
        };
    }

    // ========================
    // Advanced Tag Rendering
    // ========================

    /**
     * Extracts all plain text content from a list of nodes recursively.
     */
    private static String extractPlainText(List<Node> nodes) {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            switch (node) {
                case TextNode t -> sb.append(t.content());
                case TagNode t -> sb.append(extractPlainText(t.children()));
            }
        }
        return sb.toString();
    }

    /**
     * Renders a gradient tag by interpolating colors across the inner text characters.
     * <gradient:color1:color2[:color3...]>
     */
    private static MutableComponent renderGradient(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            // No colors specified, render children normally
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Parse color stops from args (colon-separated)
        String[] colorArgs = args.split(":");
        int[] colors = parseColorStops(colorArgs);
        if (colors.length < 2) {
            // Need at least 2 colors for a gradient
            return renderNodes(tagNode.children(), parentStyle);
        }

        return applyGradientColors(tagNode.children(), parentStyle, colors);
    }

    /**
     * Renders a rainbow tag by applying a hue rotation gradient across inner text.
     */
    private static MutableComponent renderRainbow(TagNode tagNode, Style parentStyle) {
        String plainText = extractPlainText(tagNode.children());
        int length = plainText.length();
        if (length == 0) {
            return Component.empty();
        }

        MutableComponent result = Component.empty();
        for (int i = 0; i < plainText.length(); i++) {
            float hue = (float) i / length;
            int rgb = hsbToRgb(hue, 1.0f, 1.0f);
            Style charStyle = parentStyle.withColor(TextColor.fromRgb(rgb));
            MutableComponent charComp = Component.literal(String.valueOf(plainText.charAt(i)));
            charComp.setStyle(charStyle);
            result.append(charComp);
        }
        return result;
    }

    /**
     * Renders a pride tag using predefined flag color palettes.
     * <pride:flag> where flag is rainbow, trans, bi, pan, nb, lesbian, ace
     * Defaults to rainbow if no flag specified.
     */
    private static MutableComponent renderPride(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        String flag = args.isEmpty() ? "rainbow" : args.toLowerCase().split(":")[0];
        int[] colors = PRIDE_PALETTES.getOrDefault(flag, PRIDE_PALETTES.get("rainbow"));
        return applyGradientColors(tagNode.children(), parentStyle, colors);
    }

    /**
     * Renders a transition tag — similar to gradient with multiple color stops.
     * <transition:color1:color2[:color3...]>
     */
    private static MutableComponent renderTransition(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        String[] colorArgs = args.split(":");
        int[] colors = parseColorStops(colorArgs);
        if (colors.length < 2) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        return applyGradientColors(tagNode.children(), parentStyle, colors);
    }

    /**
     * Renders a shadow tag. Shadow is not natively supported as a Component style in 1.21.1.
     * Best-effort: render children with a slightly darker color variant if a color is set,
     * otherwise just pass through.
     */
    private static MutableComponent renderShadow(TagNode tagNode, Style parentStyle) {
        // Shadow effect is not directly representable in the Component system for 1.21.1.
        // We render children as-is. A future Minecraft version may support text-shadow natively.
        return renderNodes(tagNode.children(), parentStyle);
    }

    /**
     * Renders a hover tag: <hover:show_text:content>
     * Adds a HoverEvent to the component style for all children.
     */
    private static MutableComponent renderHover(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Parse action and value: show_text:content
        int colonIdx = args.indexOf(':');
        String action;
        String value;
        if (colonIdx != -1) {
            action = args.substring(0, colonIdx).toLowerCase().trim();
            value = args.substring(colonIdx + 1).trim();
        } else {
            action = args.toLowerCase().trim();
            value = "";
        }

        if (action.equals("show_text") && !value.isEmpty()) {
            // Parse the hover content as MiniMessage too
            MutableComponent hoverContent = parse(value);
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverContent);
            Style hoverStyle = parentStyle.withHoverEvent(hoverEvent);
            return renderNodes(tagNode.children(), hoverStyle);
        }

        // Unsupported hover action — render children normally
        return renderNodes(tagNode.children(), parentStyle);
    }

    /**
     * Renders a click tag: <click:action:value>
     * Adds a ClickEvent to the component style for all children.
     * Actions: open_url, run_command, suggest_command, copy_to_clipboard
     */
    private static MutableComponent renderClick(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Parse action and value
        int colonIdx = args.indexOf(':');
        String action;
        String value;
        if (colonIdx != -1) {
            action = args.substring(0, colonIdx).toLowerCase().trim();
            value = args.substring(colonIdx + 1).trim();
        } else {
            action = args.toLowerCase().trim();
            value = "";
        }

        ClickEvent.Action clickAction = switch (action) {
            case "open_url" -> ClickEvent.Action.OPEN_URL;
            case "run_command" -> ClickEvent.Action.RUN_COMMAND;
            case "suggest_command" -> ClickEvent.Action.SUGGEST_COMMAND;
            case "copy_to_clipboard" -> ClickEvent.Action.COPY_TO_CLIPBOARD;
            default -> null;
        };

        if (clickAction != null && !value.isEmpty()) {
            ClickEvent clickEvent = new ClickEvent(clickAction, value);
            Style clickStyle = parentStyle.withClickEvent(clickEvent);
            return renderNodes(tagNode.children(), clickStyle);
        }

        // Unknown action or empty value — render children normally
        return renderNodes(tagNode.children(), parentStyle);
    }

    /**
     * Renders an insertion tag: <insertion:text>
     * Sets the insertion string on the style (shift-click to insert text).
     */
    private static MutableComponent renderInsertion(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        Style insertionStyle = parentStyle.withInsertion(args);
        return renderNodes(tagNode.children(), insertionStyle);
    }

    /**
     * Renders a score tag: <score:name:objective>
     * Creates a score component.
     */
    private static MutableComponent renderScore(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Parse name and objective from args
        String[] parts = args.split(":");
        if (parts.length >= 2) {
            String name = parts[0].trim();
            String objective = parts[1].trim();
            MutableComponent scoreComp = Component.score(name, objective);
            scoreComp.setStyle(parentStyle);
            // If there are children, append them after the score component
            if (!tagNode.children().isEmpty()) {
                MutableComponent result = Component.empty();
                result.append(scoreComp);
                result.append(renderNodes(tagNode.children(), parentStyle));
                return result;
            }
            return scoreComp;
        }

        return renderNodes(tagNode.children(), parentStyle);
    }

    /**
     * Renders a selector tag: <selector:pattern>
     * Creates a selector component.
     */
    private static MutableComponent renderSelector(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        MutableComponent selectorComp = Component.selector(args, Optional.empty());
        selectorComp.setStyle(parentStyle);
        // If there are children, append them after the selector component
        if (!tagNode.children().isEmpty()) {
            MutableComponent result = Component.empty();
            result.append(selectorComp);
            result.append(renderNodes(tagNode.children(), parentStyle));
            return result;
        }
        return selectorComp;
    }

    /**
     * Renders a font tag: <font:namespace:key>
     * Sets the font on the style via ResourceLocation.
     */
    private static MutableComponent renderFont(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        // Parse namespace:key from args
        String[] parts = args.split(":");
        ResourceLocation fontLocation;
        if (parts.length >= 2) {
            fontLocation = ResourceLocation.fromNamespaceAndPath(parts[0].trim(), parts[1].trim());
        } else {
            // Default to minecraft namespace
            fontLocation = ResourceLocation.fromNamespaceAndPath("minecraft", parts[0].trim());
        }

        Style fontStyle = parentStyle.withFont(fontLocation);
        return renderNodes(tagNode.children(), fontStyle);
    }

    /**
     * Renders a translatable/lang tag: <translatable:key> or <lang:key>
     * Creates a translatable component.
     */
    private static MutableComponent renderTranslatable(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        MutableComponent translatableComp = Component.translatable(args);
        translatableComp.setStyle(parentStyle);
        // If there are children, append them after the translatable component
        if (!tagNode.children().isEmpty()) {
            MutableComponent result = Component.empty();
            result.append(translatableComp);
            result.append(renderNodes(tagNode.children(), parentStyle));
            return result;
        }
        return translatableComp;
    }

    /**
     * Renders a keybind tag: <keybind:key>
     * Creates a keybind component.
     */
    private static MutableComponent renderKeybind(TagNode tagNode, Style parentStyle) {
        String args = tagNode.args();
        if (args.isEmpty()) {
            return renderNodes(tagNode.children(), parentStyle);
        }

        MutableComponent keybindComp = Component.keybind(args);
        keybindComp.setStyle(parentStyle);
        // If there are children, append them after the keybind component
        if (!tagNode.children().isEmpty()) {
            MutableComponent result = Component.empty();
            result.append(keybindComp);
            result.append(renderNodes(tagNode.children(), parentStyle));
            return result;
        }
        return keybindComp;
    }

    // ========================
    // Gradient Utilities
    // ========================

    /**
     * Parses an array of color arguments into RGB int values.
     * Supports hex (#RRGGBB) and named colors.
     */
    private static int[] parseColorStops(String[] colorArgs) {
        List<Integer> colors = new ArrayList<>();
        for (String arg : colorArgs) {
            String trimmed = arg.trim().toLowerCase();
            if (trimmed.startsWith("#") && trimmed.length() == 7) {
                try {
                    colors.add(Integer.parseInt(trimmed.substring(1), 16));
                } catch (NumberFormatException e) {
                    // Skip invalid hex
                }
            } else {
                ChatFormatting fmt = NAMED_COLORS.get(trimmed);
                if (fmt != null && fmt.getColor() != null) {
                    colors.add(fmt.getColor());
                }
            }
        }
        return colors.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Applies a multi-stop gradient across the plain text content of the given nodes.
     * Each character gets an interpolated color between the color stops.
     */
    private static MutableComponent applyGradientColors(List<Node> children, Style parentStyle, int[] colors) {
        String plainText = extractPlainText(children);
        int length = plainText.length();
        if (length == 0) {
            return Component.empty();
        }
        if (length == 1) {
            MutableComponent charComp = Component.literal(plainText);
            charComp.setStyle(parentStyle.withColor(TextColor.fromRgb(colors[0])));
            return charComp;
        }

        MutableComponent result = Component.empty();
        for (int i = 0; i < length; i++) {
            float progress = (float) i / (length - 1);
            int rgb = interpolateMultiColor(colors, progress);
            Style charStyle = parentStyle.withColor(TextColor.fromRgb(rgb));
            MutableComponent charComp = Component.literal(String.valueOf(plainText.charAt(i)));
            charComp.setStyle(charStyle);
            result.append(charComp);
        }
        return result;
    }

    /**
     * Interpolates between multiple color stops based on progress (0.0 to 1.0).
     */
    private static int interpolateMultiColor(int[] colors, float progress) {
        if (colors.length == 1) return colors[0];
        if (progress <= 0f) return colors[0];
        if (progress >= 1f) return colors[colors.length - 1];

        // Determine which segment we're in
        float segmentSize = 1.0f / (colors.length - 1);
        int segmentIndex = (int) (progress / segmentSize);
        if (segmentIndex >= colors.length - 1) segmentIndex = colors.length - 2;

        float segmentProgress = (progress - segmentIndex * segmentSize) / segmentSize;
        return interpolateColor(colors[segmentIndex], colors[segmentIndex + 1], segmentProgress);
    }

    /**
     * Linearly interpolates between two RGB colors.
     */
    private static int interpolateColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = Math.round(r1 + (r2 - r1) * ratio);
        int g = Math.round(g1 + (g2 - g1) * ratio);
        int b = Math.round(b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    /**
     * Converts HSB (Hue, Saturation, Brightness) to an RGB integer.
     * Avoids dependency on java.awt.Color for server environments.
     *
     * @param hue        hue component (0.0 to 1.0)
     * @param saturation saturation component (0.0 to 1.0)
     * @param brightness brightness component (0.0 to 1.0)
     * @return packed RGB integer (0xRRGGBB)
     */
    private static int hsbToRgb(float hue, float saturation, float brightness) {
        int r = 0, g = 0, b = 0;
        if (saturation == 0) {
            r = g = b = (int) (brightness * 255.0f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6.0f;
            float f = h - (float) Math.floor(h);
            float p = brightness * (1.0f - saturation);
            float q = brightness * (1.0f - saturation * f);
            float t = brightness * (1.0f - (saturation * (1.0f - f)));
            switch ((int) h) {
                case 0 -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (t * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
                case 1 -> { r = (int) (q * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (p * 255.0f + 0.5f); }
                case 2 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (brightness * 255.0f + 0.5f); b = (int) (t * 255.0f + 0.5f); }
                case 3 -> { r = (int) (p * 255.0f + 0.5f); g = (int) (q * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
                case 4 -> { r = (int) (t * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (brightness * 255.0f + 0.5f); }
                case 5 -> { r = (int) (brightness * 255.0f + 0.5f); g = (int) (p * 255.0f + 0.5f); b = (int) (q * 255.0f + 0.5f); }
            }
        }
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}

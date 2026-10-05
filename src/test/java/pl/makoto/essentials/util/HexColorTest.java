package pl.makoto.essentials.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HexColorTest {

    @Test
    void testHexColorParsing() {
        String input = "&#AC0ED3&l&o[&#B611D9&l&oO&#C014DF&l&ow&#CA18E6&l&on&#D41BEC&l&oe&#DE1EF2&l&or&#E821F8&l&o]JustJade2007";
        
        // Test direct LegacyCodeConverter + MiniMessageParser
        String converted = LegacyCodeConverter.convert(input);
        Component comp = MiniMessageParser.parse(converted);
        assertEquals("[Owner]JustJade2007", comp.getString());

        // Test MessageUtils.format (used by chat prefixes, tablist, nametags, placeholders)
        Component formatted = MessageUtils.format(input);
        assertEquals("[Owner]JustJade2007", formatted.getString());

        // Verify specific color stops and decoration styles
        List<Component> leaves = formatted.toFlatList();
        assertFalse(leaves.isEmpty());

        // Check '[' has #AC0ED3, bold, italic
        Component bracketOpen = leaves.stream()
                .filter(c -> c.getString().equals("["))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xAC0ED3), bracketOpen.getStyle().getColor());
        assertTrue(bracketOpen.getStyle().isBold());
        assertTrue(bracketOpen.getStyle().isItalic());

        // Check 'O' has #B611D9, bold, italic
        Component letterO = leaves.stream()
                .filter(c -> c.getString().equals("O"))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xB611D9), letterO.getStyle().getColor());
        assertTrue(letterO.getStyle().isBold());
        assertTrue(letterO.getStyle().isItalic());

        // Check ']JustJade2007' has #E821F8, bold, italic
        Component namePart = leaves.stream()
                .filter(c -> c.getString().contains("JustJade2007"))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xE821F8), namePart.getStyle().getColor());
        assertTrue(namePart.getStyle().isBold());
        assertTrue(namePart.getStyle().isItalic());
    }

    @Test
    void testSectionSignHexParsing() {
        String input = "§#AC0ED3§l§o[§#B611D9§l§oO§#C014DF§l§ow§#CA18E6§l§on§#D41BEC§l§oe§#DE1EF2§l§or§#E821F8§l§o]JustJade2007";
        Component formatted = MessageUtils.format(input);
        assertEquals("[Owner]JustJade2007", formatted.getString());

        List<Component> leaves = formatted.toFlatList();
        Component bracketOpen = leaves.stream()
                .filter(c -> c.getString().equals("["))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xAC0ED3), bracketOpen.getStyle().getColor());
        assertTrue(bracketOpen.getStyle().isBold());
        assertTrue(bracketOpen.getStyle().isItalic());
    }

    @Test
    void testBungeeCordHexParsing() {
        String input = "&x&a&c&0&e&d&3&l&o[&x&b&6&1&1&d&9&l&oO]JustJade2007";
        Component formatted = MessageUtils.format(input);
        assertEquals("[O]JustJade2007", formatted.getString());

        List<Component> leaves = formatted.toFlatList();
        Component bracketOpen = leaves.stream()
                .filter(c -> c.getString().equals("["))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xAC0ED3), bracketOpen.getStyle().getColor());
    }

    @Test
    void testMiniMessageShorthandHexParsing() {
        String input = "<#AC0ED3><bold><italic>[Owner]</italic></bold></#AC0ED3>JustJade2007";
        Component formatted = MessageUtils.format(input);
        assertEquals("[Owner]JustJade2007", formatted.getString());

        List<Component> leaves = formatted.toFlatList();
        Component ownerPart = leaves.stream()
                .filter(c -> c.getString().equals("[Owner]"))
                .findFirst()
                .orElseThrow();
        assertEquals(TextColor.fromRgb(0xAC0ED3), ownerPart.getStyle().getColor());
        assertTrue(ownerPart.getStyle().isBold());
        assertTrue(ownerPart.getStyle().isItalic());
    }

    @Test
    void testFromMiniMessageRoundTrip() {
        String input = "&#AC0ED3&l&o[&#B611D9&l&oO&#C014DF&l&ow&#CA18E6&l&on&#D41BEC&l&oe&#DE1EF2&l&or&#E821F8&l&o]JustJade2007";
        String converted = LegacyCodeConverter.convert(input);
        String back = LegacyCodeConverter.fromMiniMessage(converted);
        assertEquals(input, back);
    }

    @Test
    void testLegacySectionHexConversion() {
        String input = "&#AC0ED3[Owner]";
        String sectionHex = MessageUtils.toLegacySection(input);
        assertEquals("§x§a§c§0§e§d§3[Owner]", sectionHex);
    }
}

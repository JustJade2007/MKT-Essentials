package pl.makoto.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import pl.makoto.essentials.config.I18n;
import pl.makoto.essentials.config.Settings;
import pl.makoto.essentials.data.DataManager;
import pl.makoto.essentials.data.PlayerData;
import pl.makoto.essentials.util.LegacyCodeConverter;
import pl.makoto.essentials.util.MessageUtils;
import pl.makoto.essentials.util.MiniMessageParser;
import pl.makoto.essentials.util.PermissionFilter;
import pl.makoto.essentials.util.PlayerListener;
import pl.makoto.essentials.util.Permissions;

public class MiscCommands {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("kickme")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.kickme", 0))
                .executes(context -> kickme(context.getSource())));

        dispatcher.register(Commands.literal("hat")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.hat", 0))
                .executes(context -> hat(context.getSource())));

        dispatcher.register(Commands.literal("ping")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.ping", 0))
                .executes(context -> ping(context.getSource())));

        dispatcher.register(nickCommand("nick"));
        dispatcher.register(nickCommand("nickname"));

        dispatcher.register(Commands.literal("realname")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.nick.see", 2))
                .then(Commands.argument("query", StringArgumentType.greedyString())
                        .executes(context -> realname(context.getSource(), StringArgumentType.getString(context, "query")))));

        dispatcher.register(Commands.literal("recording")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.recording", 0))
                .executes(context -> recording(context.getSource())));

        dispatcher.register(Commands.literal("streaming")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.streaming", 0))
                .executes(context -> streaming(context.getSource())));

        if (Settings.isAfkEnabled() && Settings.isCommandEnabled("afk")) {
            dispatcher.register(Commands.literal("afk")
                    .requires(source -> Permissions.hasPermission(source, "mktessentials.command.afk", 0))
                    .executes(context -> afk(context.getSource())));
        }

        dispatcher.register(Commands.literal("clearchat")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.admin.clearchat", 2))
                .executes(context -> clearchat(context.getSource())));

        dispatcher.register(Commands.literal("chatcolor")
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.chatcolor", 0))
                .then(Commands.argument("color", StringArgumentType.word())
                        .executes(context -> chatcolor(context.getSource(), StringArgumentType.getString(context, "color")))));
    }

    private static int chatcolor(CommandSourceStack source, String color) {
        if (!Settings.isChatcolorEnabled()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cChat color is disabled."), false);
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PlayerData data = DataManager.getPlayerData(player.getUUID());
        if (color.equalsIgnoreCase("reset") || color.equalsIgnoreCase("none") || color.equalsIgnoreCase("clear")) {
            data.setChatColor(null);
            DataManager.savePlayerData(player.getUUID());
            source.sendSuccess(() -> MessageUtils.prefixed("&7Chat color reset."), false);
            return 1;
        }

        net.minecraft.ChatFormatting formatting = net.minecraft.ChatFormatting.getByName(color.toLowerCase(java.util.Locale.ROOT));
        if (formatting == null || !formatting.isColor()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cUnknown color. Use a named color like &6red&c, &6aqua&c, &6gold&c..."), false);
            return 0;
        }

        data.setChatColor(formatting.getName());
        DataManager.savePlayerData(player.getUUID());
        source.sendSuccess(() -> MessageUtils.prefixed("&7Your chat color is now " + legacyOf(formatting) + formatting.getName() + "&7."), false);
        return 1;
    }

    private static String legacyOf(net.minecraft.ChatFormatting formatting) {
        return "§" + formatting.getChar();
    }

    private static int clearchat(CommandSourceStack source) {
        var server = source.getServer();
        if (server == null) return 0;

        Component blank = Component.literal("");
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            // Staff with the bypass permission keep their chat history
            if (Permissions.hasPermission(player, "mktessentials.chat.clearchat.bypass", 2)) continue;
            for (int i = 0; i < 100; i++) {
                player.sendSystemMessage(blank);
            }
        }

        String who = source.getTextName();
        server.getPlayerList().broadcastSystemMessage(
                MessageUtils.prefixed("&7Chat was cleared by &6" + who + "&7."), false);
        return 1;
    }

    private static int afk(CommandSourceStack source) {
        if (!Settings.isAfkEnabled() || !Settings.isCommandEnabled("afk")) {
            source.sendFailure(MessageUtils.prefixed(I18n.get("afk.disabled")));
            return 0;
        }
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        boolean nowAfk = pl.makoto.essentials.util.AFKManager.toggleAfk(player);
        source.sendSuccess(() -> MessageUtils.prefixed(nowAfk
                ? "&7You are now &eAFK&7."
                : "&7You are no longer AFK."), false);
        return 1;
    }

    private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> nickCommand(String name) {
        return Commands.literal(name)
                .requires(source -> Permissions.hasPermission(source, "mktessentials.command.nick", 0))
                .then(Commands.literal("reset")
                        .executes(context -> nick(context.getSource(), context.getSource().getPlayer(), null)))
                .then(Commands.argument("player", EntityArgument.player())
                        .requires(source -> Permissions.hasPermission(source, "mktessentials.admin.nick", 2))
                        .then(Commands.literal("reset")
                                .executes(context -> nick(context.getSource(), EntityArgument.getPlayer(context, "player"), null)))
                        .then(Commands.argument("nickname", StringArgumentType.greedyString())
                                .executes(context -> nick(context.getSource(), EntityArgument.getPlayer(context, "player"), StringArgumentType.getString(context, "nickname")))))
                .then(Commands.argument("nickname", StringArgumentType.greedyString())
                        .executes(context -> nick(context.getSource(), context.getSource().getPlayer(), StringArgumentType.getString(context, "nickname"))));
    }

    private static int kickme(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        player.connection.disconnect(Component.literal("You kicked yourself!"));
        return 1;
    }

    private static int hat(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);

        if (hand.isEmpty() && head.isEmpty()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cYou must be holding an item or wearing a helmet!"), false);
            return 0;
        }

        player.setItemSlot(EquipmentSlot.HEAD, hand.copy());
        player.setItemInHand(InteractionHand.MAIN_HAND, head.copy());
        
        source.sendSuccess(() -> MessageUtils.prefixed("&aEnjoy your new hat!"), true);
        return 1;
    }

    private static int recording(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PlayerData data = DataManager.getPlayerData(player.getUUID());
        boolean newState = !data.isRecording();
        
        data.setRecording(newState);
        if (newState) data.setStreaming(false);
        
        DataManager.savePlayerData(player.getUUID());
        PlayerListener.refreshNickname(player);

        source.sendSuccess(() -> MessageUtils.format("&8[&6MKT&8] &7Recording mode " + (newState ? "&aenabled" : "&cdisabled") + "&7."), true);
        return 1;
    }

    private static int nick(CommandSourceStack source, ServerPlayer target, String nickname) {
        if (target == null) return 0;

        if (!Settings.isNicknameEnabled()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cThe nickname system is disabled."), false);
            return 0;
        }

        PlayerData data = DataManager.getPlayerData(target.getUUID());
        String normalized = normalizeNickname(nickname);

        // reset / clear / off
        if (normalized == null) {
            data.setNickname(null);
            DataManager.savePlayerData(target.getUUID());
            PlayerListener.refreshNickname(target);
            source.sendSuccess(() -> MessageUtils.prefixed("&7Nickname reset for &6" + target.getScoreboardName() + "&7."), true);
            return 1;
        }

        // Apply formatting pipeline: convert legacy codes and filter tags by permissions
        String converted = LegacyCodeConverter.convert(normalized);
        final String filteredNick = PermissionFilter.filter(target, converted);

        // Validate the VISIBLE nickname (color/format codes stripped), FlectonePulse-style.
        String visible = MiniMessageParser.parse(filteredNick).getString();
        int visibleLength = visible.length();
        if (visibleLength < Settings.getNicknameMinLength()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cNickname must be at least &6"
                    + Settings.getNicknameMinLength() + "&c visible character(s)."), false);
            return 0;
        }
        if (visibleLength > Settings.getNicknameMaxLength()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cNickname can be at most &6"
                    + Settings.getNicknameMaxLength() + "&c visible character(s)."), false);
            return 0;
        }
        String pattern = Settings.getNicknameAllowedPattern();
        if (pattern != null && !pattern.isBlank()) {
            try {
                if (!visible.matches(pattern)) {
                    source.sendSuccess(() -> MessageUtils.prefixed("&cThat nickname contains characters that are not allowed."), false);
                    return 0;
                }
            } catch (java.util.regex.PatternSyntaxException e) {
                // Invalid regex in config: don't block players, just skip the pattern check.
                pl.makoto.essentials.MKTEssentials.LOGGER.warn("Invalid nickname.allowed-pattern regex: {}", e.getMessage());
            }
        }

        data.setNickname(filteredNick);
        DataManager.savePlayerData(target.getUUID());
        PlayerListener.refreshNickname(target);

        String legacyNick = LegacyCodeConverter.fromMiniMessage(filteredNick);
        if (source.getPlayer() != null && source.getPlayer().getUUID().equals(target.getUUID())) {
            source.sendSuccess(() -> MessageUtils.prefixed("&7Your nickname is now &r" + legacyNick + "&7."), true);
        } else {
            source.sendSuccess(() -> MessageUtils.prefixed("&7Nickname for &6" + target.getScoreboardName() + " &7is now &r" + legacyNick + "&7."), true);
        }
        return 1;
    }

    /**
     * Staff tool: resolves an online player's nickname (visible text, case-insensitive) back to
     * their real account name(s). Complements the {@code mktessentials.nick.see} hover.
     */
    private static int realname(CommandSourceStack source, String query) {
        ServerPlayer viewer = source.getPlayer();
        var server = source.getServer();
        if (server == null) return 0;

        String needle = query.trim().toLowerCase(java.util.Locale.ROOT);
        java.util.List<ServerPlayer> matches = new java.util.ArrayList<>();
        for (ServerPlayer online : server.getPlayerList().getPlayers()) {
            String realName = online.getScoreboardName();
            String nick = DataManager.getPlayerData(online.getUUID()).getNickname();
            String visibleNick = nick != null && !nick.isBlank()
                    ? MiniMessageParser.parse(nick).getString()
                    : realName;
            if (realName.toLowerCase(java.util.Locale.ROOT).contains(needle)
                    || visibleNick.toLowerCase(java.util.Locale.ROOT).contains(needle)) {
                matches.add(online);
            }
        }

        if (matches.isEmpty()) {
            source.sendSuccess(() -> MessageUtils.prefixed("&cNo online player found matching &6" + query + "&c."), false);
            return 0;
        }

        source.sendSuccess(() -> MessageUtils.prefixed("&7Matches for &6" + query + "&7:"), false);
        for (ServerPlayer match : matches) {
            String nick = DataManager.getPlayerData(match.getUUID()).getNickname();
            String display = nick != null && !nick.isBlank()
                    ? MiniMessageParser.parse(nick).getString()
                    : match.getScoreboardName();
            source.sendSuccess(() -> MessageUtils.format("&8- &f" + match.getScoreboardName() + " &7(&r" + display + "&7)"), false);
        }
        return matches.size();
    }

    private static int streaming(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        PlayerData data = DataManager.getPlayerData(player.getUUID());
        boolean newState = !data.isStreaming();
        
        data.setStreaming(newState);
        if (newState) data.setRecording(false);

        DataManager.savePlayerData(player.getUUID());
        PlayerListener.refreshNickname(player);

        source.sendSuccess(() -> MessageUtils.format("&8[&6MKT&8] &7Streaming mode " + (newState ? "&aenabled" : "&cdisabled") + "&7."), true);
        return 1;
    }

    private static int ping(CommandSourceStack source) {
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        int ping = player.connection.latency();
        String color = ping < 100 ? "&a" : (ping < 200 ? "&e" : "&c");
        source.sendSuccess(() -> MessageUtils.prefixed("&7Your ping: " + color + ping + "ms"), false);
        return 1;
    }

    private static String normalizeNickname(String nickname) {
        if (nickname == null) return null;

        String trimmed = nickname.trim();
        if (trimmed.isEmpty()
                || trimmed.equalsIgnoreCase("reset")
                || trimmed.equalsIgnoreCase("clear")
                || trimmed.equalsIgnoreCase("off")) {
            return null;
        }

        return trimmed.length() > 48 ? trimmed.substring(0, 48) : trimmed;
    }
}

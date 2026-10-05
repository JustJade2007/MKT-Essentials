package pl.makoto.essentials.util;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import pl.makoto.essentials.config.Settings;
import pl.makoto.essentials.config.I18n;
import pl.makoto.essentials.MKTEssentials;
import pl.makoto.essentials.commands.MessagingCommands;
import pl.makoto.essentials.data.DataManager;
import pl.makoto.essentials.data.PlayerData;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.HoverEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;

import net.neoforged.bus.api.EventPriority;

import net.minecraft.server.TickTask;

import pl.makoto.essentials.data.BanEntry;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@EventBusSubscriber(modid = MKTEssentials.MODID)
public class PlayerListener {
    private static final Set<UUID> READY_PLAYERS = new HashSet<>();

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLoginBanCheck(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();

        // Maintenance mode: only players with the bypass permission may join
        if (MaintenanceManager.isActive()
                && !Permissions.hasPermission(player, "mktessentials.maintenance.bypass", 2)) {
            player.connection.disconnect(MessageUtils.format(Settings.getMaintenanceKick()));
            return;
        }

        // IP ban check
        String ip = IpBanManager.getPlayerIp(player);
        if (IpBanManager.isBanned(ip)) {
            IpBanManager.IpBanEntry ipBan = IpBanManager.getBan(ip);
            player.connection.disconnect(MessageUtils.format(
                "&c&lYou are IP banned from this server.\n\n&7Reason: &f" + ipBan.reason
                + "\n&7Banned by: &f" + ipBan.issuer
            ));
            return;
        }

        // Shadowban check — BEFORE ban check so shadowban takes priority
        if (ShadowBanManager.isShadowBanned(uuid)) {
            String method = Settings.getShadowbanMethod();
            switch (method) {
                case "timeout" -> {
                    player.connection.disconnect(Component.literal("io.netty.channel.ConnectTimeoutException: connection timed out"));
                    return;
                }
                case "full" -> {
                    player.connection.disconnect(Component.literal("Disconnected"));
                    return;
                }
                case "internal-error" -> {
                    // Let them join, then kick after 40-60 ticks (2-3 seconds)
                    int delay = 40 + player.getServer().overworld().getRandom().nextInt(21);
                    player.getServer().tell(new TickTask(player.getServer().getTickCount() + delay, () -> {
                        ServerPlayer target = player.getServer().getPlayerList().getPlayer(uuid);
                        if (target != null) {
                            target.connection.disconnect(Component.literal(
                                    "Internal Exception: io.netty.handler.codec.DecoderException: java.lang.IndexOutOfBoundsException: readerIndex(47) + length(1) exceeds writerIndex(47)"));
                        }
                    }));
                }
                case "phantom" -> {
                    // Let them join, add to phantom set — handled in onPlayerJoin and onChat
                    ShadowBanManager.addPhantom(uuid);
                }
            }
        }

        if (BanManager.isBanned(uuid)) {
            BanEntry ban = BanManager.getBan(uuid);
            if (ban == null) return; // Race condition safety

            Component disconnectMessage;
            if (ban.isPermanent()) {
                disconnectMessage = MessageUtils.format(
                    "&c&lYou are banned from this server.\n\n&7Reason: &f" + ban.getReason()
                    + "\n&7Banned by: &f" + ban.getIssuer()
                );
            } else {
                long remaining = ban.getExpiresAt() - System.currentTimeMillis();
                String duration = DurationParser.format(remaining);
                disconnectMessage = MessageUtils.format(
                    "&c&lYou are temporarily banned.\n\n&7Reason: &f" + ban.getReason()
                    + "\n&7Banned by: &f" + ban.getIssuer()
                    + "\n&7Expires in: &f" + duration
                );
            }

            player.connection.disconnect(disconnectMessage);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // Delay processing by 1 tick to ensure stability
        player.getServer().tell(new TickTask(player.getServer().getTickCount() + 1, () -> {
            if (player.getServer().getPlayerList().getPlayer(player.getUUID()) == null) return;

            READY_PLAYERS.add(player.getUUID());

            // Restore persisted states from PlayerData
            PlayerData data = DataManager.getPlayerData(player.getUUID());
            // Detect a brand-new player before startSession stamps firstJoinAt
            boolean firstJoin = data.getFirstJoinAt() == 0;
            // Playtime session + last known IP (used by /whois, /playtime, /banip)
            data.startSession(System.currentTimeMillis(), IpBanManager.getPlayerIp(player));
            if (data.isGodMode()) {
                player.setInvulnerable(true);
            }
            if (data.isFlyEnabled()) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
            }
            if (data.getFlySpeed() > 0) {
                player.getAbilities().setFlyingSpeed(data.getFlySpeed());
                player.onUpdateAbilities();
            }
            if (data.getWalkSpeed() > 0) {
                player.getAbilities().setWalkingSpeed(data.getWalkSpeed());
                player.onUpdateAbilities();
            }
            if (data.isVanished()) {
                AdminManager.restoreVanish(player);
            }

            refreshNickname(player);

            // Hide vanished players from this joining player's tab list
            AdminManager.hideVanishedFromJoiningPlayer(player);

            // Full-isolation shadowban: hide the phantom↔others both ways (tab + entities).
            PhantomIsolation.onJoin(player);

            // Keep the Discord bot's "N players online" status current.
            pl.makoto.essentials.auth.DiscordBot.updatePlayerCount(player.getServer().getPlayerList().getPlayerCount());

            // Native tab list header/footer for this player
            TabListManager.refresh(player);

            // Personal greeting (first join vs returning) — shown only to this player
            GreetingManager.send(player, firstJoin);

            // Notify about unread mail
            if (Settings.isMailEnabled()) {
                int mailCount = data.getMail().size();
                if (mailCount > 0) {
                    player.sendSystemMessage(MessageUtils.prefixed(
                            "&eYou have &6" + mailCount + "&e unread mail message(s). Use &6/mail read&e."));
                }
            }

            if (Settings.isJoinQuitEnabled()) {
                if (AdminManager.isVanished(player.getUUID())) return;
                // Don't broadcast join message for phantom players
                if (ShadowBanManager.isPhantom(player.getUUID())) return;

                player.getServer().getPlayerList().broadcastSystemMessage(MessageUtils.format(player, Settings.getJoinMessage()), false);
                pl.makoto.essentials.integration.DiscordRelay.join(player);
            }
        }));
    }

    @SubscribeEvent
    public static void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        READY_PLAYERS.remove(player.getUUID());

        // Close the playtime session before the data is saved/evicted below
        DataManager.getPlayerData(player.getUUID()).endSession(System.currentTimeMillis());

        // Clean up player-specific state to prevent memory leaks
        AFKManager.removePlayer(player.getUUID());
        MessagingCommands.cleanupPlayer(player.getUUID());
        TpaManager.cleanupPlayer(player.getUUID());
        TeleportManager.cleanupPlayer(player.getUUID());
        PlayerEnvironmentManager.cleanupPlayer(player.getUUID());
        pl.makoto.essentials.commands.ReportCommands.cleanupPlayer(player.getUUID());
        AdminManager.cleanupOnDisconnect(player.getUUID());
        ShadowBanManager.removePhantom(player.getUUID());
        ChatModerationManager.cleanupPlayer(player.getUUID());
        PollManager.cleanupPlayer(player.getUUID());
        BossbarManager.remove(player);
        NametagManager.remove(player);

        if (Settings.isJoinQuitEnabled()) {
            if (AdminManager.isVanished(player.getUUID())) {
                DataManager.evictPlayer(player.getUUID());
                return;
            }
            // Don't broadcast quit message for phantom players
            if (ShadowBanManager.isPhantom(player.getUUID())) {
                DataManager.evictPlayer(player.getUUID());
                return;
            }

            event.getEntity().getServer().getPlayerList().broadcastSystemMessage(MessageUtils.format(player, Settings.getQuitMessage()), false);
            pl.makoto.essentials.integration.DiscordRelay.quit(player);
        }

        DataManager.evictPlayer(player.getUUID());

        // Update the Discord bot status; deferred a tick so the leaving player is already removed.
        var srv = event.getEntity().getServer();
        if (srv != null) srv.execute(() ->
                pl.makoto.essentials.auth.DiscordBot.updatePlayerCount(srv.getPlayerList().getPlayerCount()));
    }

    public static String getFullDisplayNameForTab(ServerPlayer player) {
        return getFullDisplayName(player, true);
    }

    public static String getPrefixForTab(ServerPlayer player) {
        return LuckPermsHook.getPrefix(player);
    }

    public static String getSuffixForTab(ServerPlayer player) {
        return LuckPermsHook.getSuffix(player);
    }

    public static String getFullDisplayName(ServerPlayer player, boolean forceIncludeLuckPerms) {
        boolean includeLuckPerms = forceIncludeLuckPerms && READY_PLAYERS.contains(player.getUUID());
        
        PlayerData data = DataManager.getPlayerData(player.getUUID());
        String name = data.getNickname() != null && !data.getNickname().isBlank()
                ? LegacyCodeConverter.fromMiniMessage(data.getNickname())
                : player.getScoreboardName();
        String dot = "";
        if (data.isRecording()) dot = "&c\u25cf &r";
        else if (data.isStreaming()) dot = "&d\u25cf &r";

        String prefix = includeLuckPerms ? LuckPermsHook.getPrefix(player) : "";
        String suffix = includeLuckPerms ? LuckPermsHook.getSuffix(player) : "";

        String afkPrefix = AFKManager.isAFK(player.getUUID()) ? "&7[AFK] " : "";
        return afkPrefix + dot + prefix + name + suffix;
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();

        // Phantom shadowban: cancel the message but echo it back to the sender
        if (ShadowBanManager.isPhantom(player.getUUID())) {
            event.setCanceled(true);
            // Build the message as if it was sent normally, so the player thinks it went through
            String group = LuckPermsHook.getPrimaryGroup(player);
            String chatFormat = Settings.getChatFormatForGroup(group);
            Component prefix = MessageUtils.format(player, chatFormat.replace("{message}", ""));
            String messageText = event.getMessage().getString();
            Component formattedMessage = MessageUtils.formatWithPermissions(player, messageText);
            Component finalMsg = prefix.copy().append(formattedMessage);
            player.sendSystemMessage(finalMsg);
            return;
        }

        // Mute check: prevent muted players from chatting
        PlayerData playerData = DataManager.getPlayerData(player.getUUID());
        long muteExpiration = playerData.getMuteExpiration();

        if (muteExpiration == -1) {
            // Permanently muted
            event.setCanceled(true);
            player.sendSystemMessage(MessageUtils.prefixed(I18n.get("moderation.permanently-muted")));
            return;
        } else if (muteExpiration > 0) {
            long now = System.currentTimeMillis();
            if (muteExpiration > now) {
                // Timed mute still active
                event.setCanceled(true);
                String remaining = formatRemainingTime(muteExpiration - now);
                player.sendSystemMessage(MessageUtils.prefixed(I18n.get("moderation.already-muted", "remaining", remaining)));
                return;
            } else {
                // Timed mute has expired, clear it
                playerData.setMuteExpiration(0);
                DataManager.savePlayerData(player.getUUID());
            }
        }
        // muteExpiration == 0: not muted, continue normally

        // Cancel original message to remove <PlayerName> brackets
        event.setCanceled(true);

        // Chat moderation (anti-flood, caps, swear). Staff with the bypass permission skip filters.
        String moderatedText = event.getMessage().getString();
        if (!Permissions.hasPermission(player, "mktessentials.chat.moderation.bypass", 2)) {
            ChatModerationManager.Result mod = ChatModerationManager.process(player, moderatedText);
            if (mod.blocked()) {
                player.sendSystemMessage(MessageUtils.prefixed(mod.blockMessage()));
                return;
            }
            moderatedText = mod.message();
        }

        // Local (range-based) chat: a message reaches only nearby players unless it starts with
        // the global prefix. Prefixed messages are sent globally with the prefix stripped.
        boolean localOnly = false;
        if (Settings.isChatLocalEnabled()) {
            String globalPrefix = Settings.getChatGlobalPrefix();
            if (globalPrefix != null && !globalPrefix.isEmpty() && moderatedText.startsWith(globalPrefix)) {
                moderatedText = moderatedText.substring(globalPrefix.length()).trim();
            } else {
                localOnly = true;
            }
        }
        final boolean isLocal = localOnly;

        // Get per-group chat format (falls back to default if no group format defined)
        String group = LuckPermsHook.getPrimaryGroup(player);
        String chatFormat = Settings.getChatFormatForGroup(group);

        // Format the chat prefix (player name, rank, etc.) using placeholders
        Component prefix = MessageUtils.format(player, chatFormat.replace("{message}", ""));

        // Build base hover text with rank, ping, and UUID (shared by all viewers)
        MutableComponent baseHover = Component.empty();
        String rank = LuckPermsHook.getPrimaryGroup(player);
        if (rank != null) {
            baseHover.append(MessageUtils.format("&7Rank: &f" + rank + "\n"));
        }
        baseHover.append(MessageUtils.format("&7Ping: &f" + player.connection.latency() + "ms\n"));
        baseHover.append(MessageUtils.format("&7UUID: &f" + player.getUUID().toString()));

        // Real name is exposed only to staff with mktessentials.nick.see, and only when nicknamed
        String senderNick = playerData.getNickname();
        boolean senderNicked = senderNick != null && !senderNick.isBlank();
        String realNameLine = "\n&7Real name: &f" + player.getScoreboardName();

        // Format the player's message content with permission-based MiniMessage/legacy code
        // filtering, plus @mention highlighting and collection of mentioned players.
        ObjectManager.Result mention = ObjectManager.process(player, moderatedText);
        Component formattedMessage = mention.message();

        // Per-player chat color (/chatcolor) as the base color of the message text
        String chatColor = playerData.getChatColor();
        if (Settings.isChatcolorEnabled() && chatColor != null && !chatColor.isBlank()) {
            net.minecraft.ChatFormatting fmt = net.minecraft.ChatFormatting.getByName(chatColor);
            if (fmt != null && fmt.isColor()) {
                formattedMessage = Component.empty().withStyle(s -> s.withColor(fmt)).append(formattedMessage);
            }
        }

        // Send to each player individually so /ignore can filter chat and /nick.see can reveal the real name
        for (ServerPlayer viewer : player.getServer().getPlayerList().getPlayers()) {
            // Full-isolation shadowban: a phantom neither sends to nor receives from others.
            if (PhantomIsolation.hidden(player, viewer)) continue;
            if (!viewer.getUUID().equals(player.getUUID())
                    && DataManager.getPlayerData(viewer.getUUID()).isIgnoring(player.getUUID())) {
                continue;
            }
            // Local chat range filter (sender + nearby + staff with bypass always see it)
            if (isLocal && !viewer.getUUID().equals(player.getUUID())
                    && !Permissions.hasPermission(viewer, "mktessentials.chat.local.bypass", 2)
                    && !isNearby(player, viewer)) {
                continue;
            }

            MutableComponent hover = baseHover.copy();
            if (senderNicked && Permissions.hasPermission(viewer, "mktessentials.nick.see", 2)) {
                hover.append(MessageUtils.format(realNameLine));
            }
            Component prefixWithHover = prefix.copy().withStyle(style ->
                style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hover))
            );
            viewer.sendSystemMessage(prefixWithHover.copy().append(formattedMessage));
        }

        // Ping mentioned players (skip if they ignore the sender)
        for (ServerPlayer target : mention.mentioned()) {
            if (DataManager.getPlayerData(target.getUUID()).isIgnoring(player.getUUID())) continue;
            MentionManager.playPing(target);
        }

        // Mirror global chat to Discord (local/range chat stays in-game).
        if (!isLocal) pl.makoto.essentials.integration.DiscordRelay.mcChat(player, moderatedText);

        // Log to console manually since we cancelled the event
        MKTEssentials.LOGGER.info("[Chat] " + prefix.copy().append(formattedMessage).getString());
    }

    /**
     * Records the death location so /back can return the player to where they died.
     */
    @SubscribeEvent
    public static void onPlayerDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        PlayerData data = DataManager.getPlayerData(player.getUUID());
        data.pushBackLocation(new PlayerData.SavedLocation(
                player.level().dimension().location().toString(),
                player.position(), player.getYRot(), player.getXRot()
        ));
        player.sendSystemMessage(MessageUtils.prefixed("&7Use &6/back &7to return to your death location."));
    }

    @SubscribeEvent
    public static void onTabListFormat(PlayerEvent.TabListNameFormat event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        event.setDisplayName(MessageUtils.format(player, "%mktessentials:full_name%"));
    }

    public static void refreshTabName(ServerPlayer player) {
        if (player.getServer() == null) return;
        var list = player.getServer().getPlayerList();
        list.broadcastAll(new ClientboundPlayerInfoUpdatePacket(
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME,
                player));
    }

    public static void refreshNickname(ServerPlayer player) {
        // Native above-head nametag (rank prefix/suffix via scoreboard teams)
        NametagManager.refresh(player);

        // Re-send the player-info entry so BOTH the tab display name and the above-head profile name
        // pick up the nickname (the entry is rewritten by PlayerInfoEntryMixin). A plain
        // UPDATE_DISPLAY_NAME only refreshes the tab name, not the profile name used above the head,
        // so we remove and re-add the entry.
        var list = player.getServer().getPlayerList();
        list.broadcastAll(new net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket(
                java.util.List.of(player.getUUID())));
        list.broadcastAll(new ClientboundPlayerInfoUpdatePacket(java.util.EnumSet.of(
                ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE,
                ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME), java.util.List.of(player)));

        // Re-spawn the entity for observers so the above-head name picks up the new profile
        NicknameService.respawnEntity(player);

        // Handle Vanish Tab visibility via AdminManager
        if (AdminManager.isVanished(player.getUUID())) {
            AdminManager.hideFromTabList(player);
        }

        // Re-hide phantom (shadowban) players: the ADD_PLAYER broadcast above re-exposes them in
        // everyone's tab list, so a nick refresh would otherwise blow their cover.
        if (ShadowBanManager.isPhantom(player.getUUID())) {
            PhantomIsolation.hideFromOthersTab(player);
        }
    }

    /**
     * Whether the viewer should receive the sender's local chat: always requires the same dimension;
     * in "range" mode also within the configured radius, in "world" mode the whole dimension.
     */
    private static boolean isNearby(ServerPlayer sender, ServerPlayer viewer) {
        if (sender.level() != viewer.level()) return false;
        if ("world".equalsIgnoreCase(Settings.getChatLocalMode())) return true;
        double radius = Settings.getChatLocalRadius();
        return sender.distanceToSqr(viewer) <= radius * radius;
    }

    private static String formatRemainingTime(long millis) {
        long seconds = millis / 1000;
        long days = seconds / 86400;
        seconds %= 86400;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0) sb.append(minutes).append("m ");
        if (seconds > 0 || sb.isEmpty()) sb.append(seconds).append("s");
        return sb.toString().trim();
    }
}

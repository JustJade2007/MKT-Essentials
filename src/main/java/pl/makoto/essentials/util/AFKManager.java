package pl.makoto.essentials.util;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import pl.makoto.essentials.config.I18n;
import pl.makoto.essentials.config.Settings;
import pl.makoto.essentials.MKTEssentials;

import java.util.*;

@EventBusSubscriber(modid = MKTEssentials.MODID)
public class AFKManager {
    private static final Map<UUID, Long> lastActivity = new HashMap<>();
    private static final Set<UUID> afkPlayers = new HashSet<>();
    private static final Map<UUID, double[]> lastPositions = new HashMap<>();
    private static long tickCounter = 0;

    public static void recordActivity(UUID uuid) {
        lastActivity.put(uuid, System.currentTimeMillis());
        if (afkPlayers.remove(uuid)) {
            // Player returned from AFK
            var server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            ServerPlayer player = server.getPlayerList().getPlayer(uuid);
            if (player != null) {
                // Announcing AFK for vanished/phantom players would reveal their presence
                if (!AdminManager.isVanished(uuid) && !ShadowBanManager.isPhantom(uuid)) {
                    player.getServer().getPlayerList().broadcastSystemMessage(
                        MessageUtils.prefixed(I18n.get("afk.no-longer-afk", "player", player.getScoreboardName())), false);
                }
                PlayerListener.refreshTabName(player);
            }
        }
    }

    public static boolean isAFK(UUID uuid) {
        return afkPlayers.contains(uuid);
    }

    /** Manual /afk toggle. @return true if the player is now AFK */
    public static boolean toggleAfk(ServerPlayer player) {
        if (!Settings.isAfkEnabled() || !Settings.isCommandEnabled("afk")) {
            return false;
        }
        UUID uuid = player.getUUID();
        if (afkPlayers.contains(uuid)) {
            recordActivity(uuid); // announces "no longer AFK" and refreshes tab name
            return false;
        }
        afkPlayers.add(uuid);
        // Sync the stored position so the movement detector un-AFKs them on their next move
        lastPositions.put(uuid, new double[]{player.getX(), player.getY(), player.getZ()});
        lastActivity.put(uuid, System.currentTimeMillis());
        if (!AdminManager.isVanished(uuid) && !ShadowBanManager.isPhantom(uuid)) {
            player.getServer().getPlayerList().broadcastSystemMessage(
                MessageUtils.prefixed(I18n.get("afk.now-afk", "player", player.getScoreboardName())), false);
        }
        PlayerListener.refreshTabName(player);
        return true;
    }

    public static void removePlayer(UUID uuid) {
        lastActivity.remove(uuid);
        afkPlayers.remove(uuid);
        lastPositions.remove(uuid);
    }

    public static void clearAll() {
        var server = ServerLifecycleHooks.getCurrentServer();
        for (UUID uuid : new ArrayList<>(afkPlayers)) {
            if (server != null) {
                ServerPlayer player = server.getPlayerList().getPlayer(uuid);
                if (player != null) {
                    PlayerListener.refreshTabName(player);
                }
            }
        }
        afkPlayers.clear();
        lastPositions.clear();
        lastActivity.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (!Settings.isAfkEnabled()) return;
        int timeout = Settings.getAfkTimeout();
        if (timeout <= 0) return;

        tickCounter++;
        if (tickCounter < 20) return; // Check once per second
        tickCounter = 0;

        long now = System.currentTimeMillis();
        long timeoutMs = timeout * 1000L;

        for (Map.Entry<UUID, Long> entry : new ArrayList<>(lastActivity.entrySet())) {
            UUID uuid = entry.getKey();
            if (afkPlayers.contains(uuid)) continue; // Already AFK

            if (now - entry.getValue() >= timeoutMs) {
                afkPlayers.add(uuid);
                ServerPlayer player = event.getServer().getPlayerList().getPlayer(uuid);
                if (player != null) {
                    // Announcing AFK for vanished/phantom players would reveal their presence
                    if (!AdminManager.isVanished(uuid) && !ShadowBanManager.isPhantom(uuid)) {
                        player.getServer().getPlayerList().broadcastSystemMessage(
                            MessageUtils.prefixed(I18n.get("afk.now-afk", "player", player.getScoreboardName())), false);
                    }
                    PlayerListener.refreshTabName(player);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!Settings.isAfkEnabled() || Settings.getAfkTimeout() <= 0) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        double[] lastPos = lastPositions.get(uuid);
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        if (lastPos == null) {
            lastPositions.put(uuid, new double[]{x, y, z});
            lastActivity.putIfAbsent(uuid, System.currentTimeMillis());
            return;
        }

        // Check if player moved (with small threshold to ignore floating point drift)
        if (Math.abs(x - lastPos[0]) > 0.01 || Math.abs(y - lastPos[1]) > 0.01 || Math.abs(z - lastPos[2]) > 0.01) {
            lastPositions.put(uuid, new double[]{x, y, z});
            recordActivity(uuid);
        }
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        if (!Settings.isAfkEnabled() || Settings.getAfkTimeout() <= 0) return;
        recordActivity(event.getPlayer().getUUID());
    }
}

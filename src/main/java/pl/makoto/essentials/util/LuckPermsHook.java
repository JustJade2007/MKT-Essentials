package pl.makoto.essentials.util;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import net.minecraft.server.level.ServerPlayer;

public class LuckPermsHook {
    private static boolean available = false;
    private static LuckPerms api = null;

    public static void init() {
        try {
            api = LuckPermsProvider.get();
            available = true;
        } catch (NoClassDefFoundError | IllegalStateException e) {
            available = false;
        }
    }

    public static String getPrefix(ServerPlayer player) {
        if (!available || player == null) return "";
        try {
            User user = api.getUserManager().getUser(player.getUUID());
            if (user == null) return "";
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String prefix = metaData.getPrefix();
            return prefix != null ? prefix : "";
        } catch (NoClassDefFoundError e) {
            available = false;
            return "";
        }
    }

    public static String getSuffix(ServerPlayer player) {
        if (!available || player == null) return "";
        try {
            User user = api.getUserManager().getUser(player.getUUID());
            if (user == null) return "";
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String suffix = metaData.getSuffix();
            return suffix != null ? suffix : "";
        } catch (NoClassDefFoundError e) {
            available = false;
            return "";
        }
    }

    public static String getPrimaryGroup(ServerPlayer player) {
        if (!available || player == null) return null;
        try {
            User user = api.getUserManager().getUser(player.getUUID());
            if (user == null) return null;
            return user.getPrimaryGroup();
        } catch (NoClassDefFoundError e) {
            available = false;
            return null;
        }
    }

    public static String getUsername(ServerPlayer player) {
        if (!available || player == null) return "";
        try {
            User user = api.getUserManager().getUser(player.getUUID());
            if (user == null) return "";
            CachedMetaData metaData = user.getCachedData().getMetaData();
            String username = metaData.getMetaValue("username");
            if (username != null && !username.isBlank()) return username;
            String displayName = metaData.getMetaValue("displayname");
            if (displayName != null && !displayName.isBlank()) return displayName;
            String displayName2 = metaData.getMetaValue("display-name");
            if (displayName2 != null && !displayName2.isBlank()) return displayName2;
            String nick = metaData.getMetaValue("nickname");
            if (nick != null && !nick.isBlank()) return nick;
            return "";
        } catch (NoClassDefFoundError e) {
            available = false;
            return "";
        }
    }
}

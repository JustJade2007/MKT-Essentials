package pl.makoto.essentials.auth;

import pl.makoto.essentials.MKTEssentials;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class AccountDatabase {

    private static Connection connection;

    private AccountDatabase() {}

    // ─── Records ───────────────────────────────────────────────────────────────

    public record AccountRecord(String mcUuid, String passwordHash, String discordId,
                                String mcName, long registeredAt, long lastLogin, String lastIp) {}

    public record SessionRecord(String mcUuid, String sessionIp, long expiresAt) {}

    public record LinkCodeRecord(String code, String mcUuid, long createdAt, long expiresAt) {}

    public record LinkedAccount(String mcUuid, String mcName, String discordId) {}

    // ─── Lifecycle ─────────────────────────────────────────────────────────────

    public static void init() {
        try {
            Path dbDir = pl.makoto.essentials.config.ConfigManager.getConfigDir();
            Files.createDirectories(dbDir);
            String url = "jdbc:sqlite:" + dbDir.resolve("accounts.db").toAbsolutePath();
            connection = DriverManager.getConnection(url);

            try (Statement stmt = connection.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS accounts (
                        mc_uuid TEXT PRIMARY KEY,
                        password_hash TEXT,
                        discord_id TEXT,
                        mc_name TEXT,
                        registered_at INTEGER,
                        last_login INTEGER,
                        last_ip TEXT
                    )""");
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS sessions (
                        mc_uuid TEXT PRIMARY KEY,
                        session_ip TEXT NOT NULL,
                        expires_at INTEGER NOT NULL
                    )""");
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS link_codes (
                        code TEXT PRIMARY KEY,
                        mc_uuid TEXT NOT NULL,
                        created_at INTEGER NOT NULL,
                        expires_at INTEGER NOT NULL
                    )""");
            }
            MKTEssentials.LOGGER.info("AccountDatabase initialized successfully.");
        } catch (Exception e) {
            MKTEssentials.LOGGER.error("Failed to initialize AccountDatabase", e);
        }
    }

    public static void shutdown() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                MKTEssentials.LOGGER.info("AccountDatabase connection closed.");
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to close AccountDatabase connection", e);
        }
    }

    // Init may have failed (e.g. unwritable config dir) — guard every query so a null
    // connection degrades to a logged error instead of an NPE that breaks join handling.
    private static boolean unavailable() {
        if (connection == null) {
            MKTEssentials.LOGGER.error("AccountDatabase is not initialized — query skipped.");
            return true;
        }
        return false;
    }

    // ─── Account CRUD ──────────────────────────────────────────────────────────

    public static AccountRecord getAccount(UUID uuid) {
        if (unavailable()) return null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT mc_uuid, password_hash, discord_id, mc_name, registered_at, last_login, last_ip FROM accounts WHERE mc_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AccountRecord(
                        rs.getString("mc_uuid"),
                        rs.getString("password_hash"),
                        rs.getString("discord_id"),
                        rs.getString("mc_name"),
                        rs.getLong("registered_at"),
                        rs.getLong("last_login"),
                        rs.getString("last_ip")
                );
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to get account for {}", uuid, e);
        }
        return null;
    }

    public static void createAccount(UUID uuid, String passwordHash, String mcName, String ip) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO accounts (mc_uuid, password_hash, discord_id, mc_name, registered_at, last_login, last_ip) VALUES (?, ?, NULL, ?, ?, ?, ?)")) {
            long now = System.currentTimeMillis();
            ps.setString(1, uuid.toString());
            ps.setString(2, passwordHash);
            ps.setString(3, mcName);
            ps.setLong(4, now);
            ps.setLong(5, now);
            ps.setString(6, ip);
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to create account for {}", uuid, e);
        }
    }

    public static void updatePasswordHash(UUID uuid, String newHash) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE accounts SET password_hash = ? WHERE mc_uuid = ?")) {
            ps.setString(1, newHash);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to update password hash for {}", uuid, e);
        }
    }

    public static void updateDiscordId(UUID uuid, String discordId) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE accounts SET discord_id = ? WHERE mc_uuid = ?")) {
            ps.setString(1, discordId);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to update discord_id for {}", uuid, e);
        }
    }

    public static void updateMcName(UUID uuid, String mcName) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE accounts SET mc_name = ? WHERE mc_uuid = ?")) {
            ps.setString(1, mcName);
            ps.setString(2, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to update mc_name for {}", uuid, e);
        }
    }

    public static void updateLastLogin(UUID uuid, String ip) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE accounts SET last_login = ?, last_ip = ? WHERE mc_uuid = ?")) {
            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, ip);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to update last login for {}", uuid, e);
        }
    }

    public static AccountRecord getAccountByDiscordId(String discordId) {
        if (unavailable()) return null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT mc_uuid, password_hash, discord_id, mc_name, registered_at, last_login, last_ip FROM accounts WHERE discord_id = ?")) {
            ps.setString(1, discordId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AccountRecord(
                        rs.getString("mc_uuid"),
                        rs.getString("password_hash"),
                        rs.getString("discord_id"),
                        rs.getString("mc_name"),
                        rs.getLong("registered_at"),
                        rs.getLong("last_login"),
                        rs.getString("last_ip")
                );
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to get account by discord_id {}", discordId, e);
        }
        return null;
    }

    /** Wszystkie konta z połączonym Discordem — do backfillu web-sync. */
    public static List<LinkedAccount> getLinkedAccounts() {
        if (unavailable()) return List.of();
        List<LinkedAccount> out = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT mc_uuid, mc_name, discord_id FROM accounts WHERE discord_id IS NOT NULL AND discord_id <> ''")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(new LinkedAccount(rs.getString("mc_uuid"), rs.getString("mc_name"), rs.getString("discord_id")));
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to list linked accounts", e);
        }
        return out;
    }

    public static void deleteAccount(UUID uuid) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM accounts WHERE mc_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to delete account for {}", uuid, e);
        }
    }

    // ─── Session CRUD ──────────────────────────────────────────────────────────

    public static SessionRecord getSession(UUID uuid) {
        if (unavailable()) return null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT mc_uuid, session_ip, expires_at FROM sessions WHERE mc_uuid = ? AND expires_at > ?")) {
            ps.setString(1, uuid.toString());
            ps.setLong(2, System.currentTimeMillis());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new SessionRecord(
                        rs.getString("mc_uuid"),
                        rs.getString("session_ip"),
                        rs.getLong("expires_at")
                );
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to get session for {}", uuid, e);
        }
        return null;
    }

    public static void createOrUpdateSession(UUID uuid, String ip, long expiresAt) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT OR REPLACE INTO sessions (mc_uuid, session_ip, expires_at) VALUES (?, ?, ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, ip);
            ps.setLong(3, expiresAt);
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to create/update session for {}", uuid, e);
        }
    }

    public static void deleteExpiredSessions() {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM sessions WHERE expires_at <= ?")) {
            ps.setLong(1, System.currentTimeMillis());
            int deleted = ps.executeUpdate();
            if (deleted > 0) {
                MKTEssentials.LOGGER.info("Cleaned up {} expired sessions.", deleted);
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to delete expired sessions", e);
        }
    }

    public static void deleteSession(UUID uuid) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM sessions WHERE mc_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to delete session for {}", uuid, e);
        }
    }

    // ─── Link Code CRUD ────────────────────────────────────────────────────────

    public static void createLinkCode(String code, UUID uuid, long expiresAt) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO link_codes (code, mc_uuid, created_at, expires_at) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, code);
            ps.setString(2, uuid.toString());
            ps.setLong(3, System.currentTimeMillis());
            ps.setLong(4, expiresAt);
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to create link code for {}", uuid, e);
        }
    }

    public static LinkCodeRecord getLinkCode(String code) {
        if (unavailable()) return null;
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT code, mc_uuid, created_at, expires_at FROM link_codes WHERE code = ? AND expires_at > ?")) {
            ps.setString(1, code);
            ps.setLong(2, System.currentTimeMillis());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new LinkCodeRecord(
                        rs.getString("code"),
                        rs.getString("mc_uuid"),
                        rs.getLong("created_at"),
                        rs.getLong("expires_at")
                );
            }
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to get link code {}", code, e);
        }
        return null;
    }

    public static void deleteLinkCode(String code) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM link_codes WHERE code = ?")) {
            ps.setString(1, code);
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to delete link code {}", code, e);
        }
    }

    public static void deleteCodesForPlayer(UUID uuid) {
        if (unavailable()) return;
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM link_codes WHERE mc_uuid = ?")) {
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            MKTEssentials.LOGGER.error("Failed to delete link codes for {}", uuid, e);
        }
    }
}

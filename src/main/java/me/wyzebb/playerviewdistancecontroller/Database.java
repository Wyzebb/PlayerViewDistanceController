package me.wyzebb.playerviewdistancecontroller;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public final class Database {
    private static final Database INSTANCE = new Database();
    private Connection connection;

    public boolean createUser(Player player) {
        synchronized (this) {
            try {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT IGNORE INTO users (player_uuid, world_independent) " +
                                "VALUES (?, ?)");

                statement.setString(1, player.getUniqueId().toString());
                statement.setBoolean(2, plugin.getPluginConfig().isWorldIndependent());

                final int rows = statement.executeUpdate();
                statement.close();

                return rows > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void updateVdRowInt(String field, OfflinePlayer player, UUID world, int value) {
        synchronized (this) {
            try {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO vd_data (player_uuid, world, " + field + ") " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE " +
                        field + " = VALUES(" + field + ")");

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, plugin.getPluginConfig().isWorldIndependent() ? world.toString() : "DEFAULT");
                statement.setInt(3, value);

                statement.executeUpdate();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateUserBoolean(String field, OfflinePlayer player, boolean value) {
        synchronized (this) {
            try {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (player_uuid, " + field + ") " +
                                "VALUES (?, ?) " +
                                "ON DUPLICATE KEY UPDATE " +
                                field + " = VALUES(" + field + ")");

                statement.setString(1, player.getUniqueId().toString());
                statement.setBoolean(2, value);

                statement.executeUpdate();
                statement.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void load() {
        this.connect();
        this.createTables();
    }

    private void connect() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                return;
            }

            if (plugin.getPluginConfig().getDatabaseType() == StorageType.MYSQL) {
                Class.forName("com.mysql.cj.jdbc.Driver");

                final String jdbcUrl = "jdbc:mysql://" + plugin.getPluginConfig().getDatabaseHost() + ":" + plugin.getPluginConfig().getDatabasePort() + "/" + plugin.getPluginConfig().getDatabaseDatabase() + "?useSSL=false&autoReconnect=true";

                plugin.getLogger().info("Connecting to MySQL database: " + jdbcUrl + " with user " + plugin.getPluginConfig().getDatabaseUsername());

                connection = DriverManager.getConnection(jdbcUrl, plugin.getPluginConfig().getDatabaseUsername(), plugin.getPluginConfig().getDatabasePassword());

                plugin.getLogger().info("Successfully connected to MySQL database");

            } else {
                Class.forName("org.sqlite.JDBC");

                final File file = new File(plugin.getDataFolder(), plugin.getPluginConfig().getSqliteFile());

                if (!file.exists()) {
                    file.createNewFile();
                }

                final String jdbcUrl = "jdbc:sqlite:" + file.getAbsolutePath();

                connection = DriverManager.getConnection(jdbcUrl);

                plugin.getLogger().info("Successfully connected to SQLite database");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() {
        try {
            final Statement statement = connection.createStatement();

            statement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "player_uuid VARCHAR(36) NOT NULL PRIMARY KEY," +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "world_independent BOOLEAN NOT NULL," +
                    "ping_mode BOOLEAN NOT NULL DEFAULT FALSE" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS vd_data (" +
                    "player_uuid VARCHAR(36) NOT NULL," +
                    "world VARCHAR(36) NOT NULL," +
                    "vd INTEGER NOT NULL DEFAULT 0," +
                    "vd_admin INTEGER NOT NULL DEFAULT 0," +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (player_uuid, world)," +
                    "FOREIGN KEY (player_uuid) REFERENCES users(player_uuid) ON DELETE CASCADE" +
                    ")");

            statement.close();

        } catch (Exception e) {
            plugin.getLogger().severe("Failed to connect to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();

                plugin.getLogger().info("Disconnected from the database");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum StorageType {
        MYSQL,
        SQLITE,
        YAML
    }

    public static Database getInstance() {
        return INSTANCE;
    }

    public static Connection getConnection() {
        return INSTANCE.connection;
    }
}
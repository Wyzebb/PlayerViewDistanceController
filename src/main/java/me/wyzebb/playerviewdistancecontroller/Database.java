package me.wyzebb.playerviewdistancecontroller;

import com.google.gson.Gson;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public final class Database {
    private static final Database INSTANCE = new Database();
    private static final Gson GSON = new Gson();
    private Connection connection;

    public boolean updateInt(String field, OfflinePlayer player, String world, int vd) {
        synchronized (this.connection) {
            try {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (player_uuid, world_independent, ping_mode) " +
                                "VALUES (?, ?, ?)");

                statement.setString(1, player.getUniqueId().toString());
                statement.setBoolean(2, plugin.getPluginConfig().isWorldIndependent());
                statement.setBoolean(3, plugin.getPingOptimiserConfig().getBoolean("enabled"));

                final PreparedStatement vdStatement = connection.prepareStatement(
                        "INSERT INTO vdData (player_uuid, world, " + field + ") " +
                                "VALUES (?, ?, ?)");

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, plugin.getPluginConfig().isWorldIndependent() ? world : "default");
                statement.setInt(3, vd);

                final int rows = statement.executeUpdate();
                statement.close();

                final int vdRows = vdStatement.executeUpdate();
                vdStatement.close();

                return rows > 0 && vdRows > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public boolean updateBoolean(String field, OfflinePlayer player, boolean value) {
        synchronized (this.connection) {
            try {
                final PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO users (player_uuid, " + field + ") " +
                                "VALUES (?, ?)");

                statement.setString(1, player.getUniqueId().toString());
                statement.setBoolean(2, value);

                final int rows = statement.executeUpdate();
                statement.close();

                return rows > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
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
                    "player_uuid TEXT NOT NULL PRIMARY KEY," +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "world_independent BOOLEAN NOT NULL," +
                    "ping_mode BOOLEAN NOT NULL," +
                    ")");

            statement.execute("CREATE TABLE vdData (" +
                    "user_id TEXT NOT NULL," +
                    "world TEXT," +
                    "vd INTEGER NOT NULL DEFAULT 0," +
                    "vd_admin INTEGER NOT NULL DEFAULT 0," +
                    "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES users(player_uuid) ON DELETE CASCADE" +
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
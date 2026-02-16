package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.Database;
import me.wyzebb.playerviewdistancecontroller.models.WorldDataRow;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class Storage {
    private static final boolean sqlDb = plugin.getPluginConfig().getDatabaseType() == Database.StorageType.MYSQL
            || plugin.getPluginConfig().getDatabaseType() == Database.StorageType.SQLITE;

    public static boolean isSqlDb() {
        return sqlDb;
    }

    public static void setChunks(OfflinePlayer player, UUID world, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateInt("vd", player, world, chunks));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setChunks(chunks);
        }
    }

    public static void setAdminChunks(OfflinePlayer player, UUID world, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateInt("vd_admin", player, world, chunks));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setAdminChunks(chunks);
        }
    }

    public static void setPingMode(OfflinePlayer player, boolean pingMode) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateBoolean("ping_mode", player, pingMode));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setPingMode(pingMode);
        }
    }

    public static int getChunks(OfflinePlayer player, UUID world) {
        if (sqlDb) {
            final int[] value = {0};
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String query = "SELECT vd FROM vd_data WHERE player_uuid = ?";

                    if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getName());
                    if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world.toString());

                    final ResultSet resultSet = statement.executeQuery();
                    value[0] = resultSet.getInt(0);
                    //TODO Issue when multiple rows and world independence off

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });

            return value[0];
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.getChunks();
        }
    }

    public static int getAdminChunks(OfflinePlayer player, UUID world) {
        if (sqlDb) {
            final int[] value = {0};
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String query = "SELECT vd_admin FROM vd_data WHERE player_uuid = ?";

                    if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getName());
                    if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world.toString());

                    final ResultSet resultSet = statement.executeQuery();
                    value[0] = resultSet.getInt(0);
                    //TODO Issue when multiple rows and world independence off

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });

            return value[0];
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.getAdminChunks();
        }
    }

    public static boolean isPingMode(OfflinePlayer player) {
        if (sqlDb) {
            final boolean[] value = {false};
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    String query = "SELECT ping_mode FROM users WHERE player_uuid = ?";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getName());

                    final ResultSet resultSet = statement.executeQuery();
                    value[0] = resultSet.getBoolean(0);

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });

            return value[0];
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.isPingMode();
        }
    }

    public static List<WorldDataRow> getRows(OfflinePlayer player) {
        if (sqlDb) {
            final List<WorldDataRow> rows = new ArrayList<>();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    final String query = "SELECT * FROM vd_data WHERE player_uuid = ?";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getName());

                    final ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        rows.add(WorldDataRow.fromResultSet(resultSet));
                    }

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });
            return rows;
        }

        return List.of();
    }
}

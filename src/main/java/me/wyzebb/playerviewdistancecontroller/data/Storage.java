package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.Database;
import me.wyzebb.playerviewdistancecontroller.models.WorldDataRow;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PingModeHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class Storage {
    private static final boolean sqlDb = plugin.getPluginConfig().getDatabaseType() == Database.StorageType.MYSQL
            || plugin.getPluginConfig().getDatabaseType() == Database.StorageType.SQLITE;

    public static boolean isSqlDb() {
        return sqlDb;
    }

    public static void setChunks(OfflinePlayer player, String world, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateVdRowInt("vd", player, world, chunks));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setChunks(chunks);
        }
    }

    public static void setAdminChunks(OfflinePlayer player, String world, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateVdRowInt("vd_admin", player, world, chunks));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setAdminChunks(chunks);
        }
    }

    public static void setPingMode(OfflinePlayer player, boolean pingMode) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateUserBoolean("ping_mode", player, pingMode));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setPingMode(pingMode);
        }

        if (pingMode) {
            PingModeHandler.optimisePing((Player) player);
        }
    }

    public static int getColumn(OfflinePlayer player, String column) {
        if (sqlDb) {
            try {
                String query = "SELECT " + column + " FROM vd_data WHERE player_uuid = ?";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());

                final ResultSet resultSet = statement.executeQuery();

                int result = 0;
                if (resultSet.next()) {
                    result = resultSet.getInt(column);
                }

                resultSet.close();
                statement.close();

                return result;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);

            switch (column) {
                case "vd":
                    return handler.getChunks();
                case "vd_admin":
                    return handler.getAdminChunks();
                default:
                    plugin.getLogger().severe("Could not get chunk data from player handler!");
                    break;
            }
        }
        return 0;
    }

    public static int getChunks(OfflinePlayer player, String world) {
        if (sqlDb) {
            try {
                String query = "SELECT vd FROM vd_data WHERE player_uuid = ?";

                if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());
                if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world.toString());

                final ResultSet resultSet = statement.executeQuery();

                int result = 0;
                if (resultSet.next()) {
                    result = resultSet.getInt("vd");
                }
                //TODO Issue when multiple rows and world independence off

                resultSet.close();
                statement.close();

                return result;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.getChunks();
        }
        return 0;
    }

    public static ArrayList<String> getWorlds(OfflinePlayer player) {
        if (sqlDb) {
            try {
                String query = "SELECT world FROM vd_data WHERE player_uuid = ?";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());

                final ResultSet resultSet = statement.executeQuery();

                ArrayList<String> worlds = new ArrayList<>();
                while (resultSet.next()) {
                    worlds.add(resultSet.getString("world"));
                }

                resultSet.close();
                statement.close();

                return worlds;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }

        return null;
    }

    public static Map<String, Integer> getIntegersAndWorlds(OfflinePlayer player, String column) {
        if (sqlDb) {
            try {
                String query = "SELECT " + column + ", world FROM vd_data WHERE player_uuid = ?";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());

                final ResultSet resultSet = statement.executeQuery();

                HashMap<String, Integer> values = new HashMap<>();
                while (resultSet.next()) {
                    values.put(resultSet.getString("world"), resultSet.getInt(column));
                }

                resultSet.close();
                statement.close();

                return values;
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return Map.of("DEFAULT", handler.getChunks());
        }

        return Map.of("DEFAULT", 0);
    }

    public static void setChunksToWorld(OfflinePlayer player, String world, String column, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                    Database.getInstance().updateVdRowInt(column, player, world, chunks));
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);

            switch (column) {
                case "vd":
                    handler.setChunks(chunks);
                    break;
                case "vd_admin":
                    handler.setAdminChunks(chunks);
                    break;
                default:
                    plugin.getLogger().severe("Could not save chunk data to player handler!");
                    break;
            }
        }
    }

    public static int getAdminChunks(OfflinePlayer player, String world) {
        if (sqlDb) {
            try {
                String query = "SELECT vd_admin FROM vd_data WHERE player_uuid = ?";

                if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());
                if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world.toString());

                final ResultSet resultSet = statement.executeQuery();

                int result = 0;
                if (resultSet.next()) {
                    result = resultSet.getInt("vd_admin");
                }
                //TODO Issue when multiple rows and world independence off

                resultSet.close();
                statement.close();

                return result;
            } catch (final SQLException e) {
                e.printStackTrace();
            }

            return 0;
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.getAdminChunks();
        }
    }

    public static boolean isPingMode(OfflinePlayer player) {
        if (sqlDb) {
            try {
                String query = "SELECT ping_mode FROM users WHERE player_uuid = ?";

                final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                statement.setString(1, player.getUniqueId().toString());

                final ResultSet resultSet = statement.executeQuery();

                boolean result = false;
                if (resultSet.next()) {
                    result = resultSet.getBoolean("ping_mode");
                }

                resultSet.close();
                statement.close();

                return result;
            } catch (final SQLException e) {
                e.printStackTrace();
            }

            return false;
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.isPingMode();
        }
    }

    public static List<WorldDataRow> getRows(OfflinePlayer player) {
        if (sqlDb) {
            final List<WorldDataRow> rows = new ArrayList<>();
                try {
                    final String query = "SELECT * FROM vd_data WHERE player_uuid = ?";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getUniqueId().toString());

                    final ResultSet resultSet = statement.executeQuery();

                    while (resultSet.next()) {
                        rows.add(WorldDataRow.fromResultSet(resultSet));
                    }

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            return rows;
        }

        return List.of();
    }
}

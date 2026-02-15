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

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class Storage {
    static final boolean sqlDb = plugin.getPluginConfig().getDatabaseType() == Database.StorageType.MYSQL
            || plugin.getPluginConfig().getDatabaseType() == Database.StorageType.SQLITE;

    public static void setChunks(OfflinePlayer player, String world, int chunks) {
        if (sqlDb) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Database.getInstance().updateVd(player, world, chunks);
            });
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            handler.setChunks(chunks);
        }
    }

    public static int getChunks(OfflinePlayer player, String world) {
        if (sqlDb) {
            final int value = 0;
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    final String query = "SELECT vd FROM vdData WHERE player_uuid = ?";

                    final PreparedStatement statement = Database.getConnection().prepareStatement(query);
                    statement.setString(1, player.getName());

                    final ResultSet resultSet = statement.executeQuery();
                    value = resultSet.getInt(0);

                    resultSet.close();
                    statement.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            });

            return value;
        } else {
            PlayerDataHandler handler = DataHandlerHandler.getPlayerDataHandler(player);
            return handler.getChunks();
        }

        return 0;
    }

    public static List<WorldDataRow> getRows(OfflinePlayer player) {
        if (sqlDb) {
            final List<WorldDataRow> rows = new ArrayList<>();
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    final String query = "SELECT * FROM vdData WHERE player_uuid = ?";

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

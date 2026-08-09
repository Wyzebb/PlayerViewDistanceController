package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.Database;
import me.wyzebb.playerviewdistancecontroller.models.WorldDataRow;
import me.wyzebb.playerviewdistancecontroller.utility.PingModeHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class Storage {
    
    public static void setChunks(OfflinePlayer player, String world, int chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                Database.getInstance().updateVdRowInt("vd", player, world, chunks));
    }

    public static void setAdminChunks(OfflinePlayer player, String world, int chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                Database.getInstance().updateVdRowInt("vd_admin", player, world, chunks));
    }

    public static void setPingMode(OfflinePlayer player, boolean pingMode) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                Database.getInstance().updateUserBoolean("ping_mode", player, pingMode));

        if (pingMode && player instanceof Player) {
            PingModeHandler.optimisePing((Player) player);
        }
    }

    public static int getColumn(OfflinePlayer player, String column) {
        try {
            String query = "SELECT " + column + " FROM vd_data WHERE player_uuid = ?";
            
            if (plugin.getPluginConfig().isWorldIndependent()) {
                query += " AND world = ?";
            } else {
                query += " AND world = 'DEFAULT'";
            }

            final PreparedStatement statement = Database.getConnection().prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            
            if (plugin.getPluginConfig().isWorldIndependent() && player instanceof Player) {
                statement.setString(2, ((Player) player).getWorld().getUID().toString());
            }

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
        return 0;
    }

    public static int getChunks(OfflinePlayer player, String world) {
        try {
            String query = "SELECT vd FROM vd_data WHERE player_uuid = ?";

            if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

            final PreparedStatement statement = Database.getConnection().prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world);

            final ResultSet resultSet = statement.executeQuery();

            int result = 0;
            if (resultSet.next()) {
                result = resultSet.getInt("vd");
            }

            resultSet.close();
            statement.close();

            return result;
        } catch (final SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<String> getWorlds(OfflinePlayer player) {
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

        return new ArrayList<>();
    }

    public static Map<String, Integer> getIntegersAndWorlds(OfflinePlayer player, String column) {
        try {
            final String query = "SELECT " + column + ", world FROM vd_data WHERE player_uuid = ?";

            final PreparedStatement statement = Database.getConnection().prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());

            final ResultSet resultSet = statement.executeQuery();

            final HashMap<String, Integer> values = new HashMap<>();
            while (resultSet.next()) {
                values.put(resultSet.getString("world"), resultSet.getInt(column));
            }

            resultSet.close();
            statement.close();

            return values;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return Map.of("DEFAULT", 0);
    }

    public static void setChunksToWorld(OfflinePlayer player, String world, String column, int chunks) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () ->
                Database.getInstance().updateVdRowInt(column, player, world, chunks));
    }

    public static int getAdminChunks(OfflinePlayer player, String world) {
        try {
            String query = "SELECT vd_admin FROM vd_data WHERE player_uuid = ?";

            if (plugin.getPluginConfig().isWorldIndependent()) query += " AND world = ? ";

            final PreparedStatement statement = Database.getConnection().prepareStatement(query);
            statement.setString(1, player.getUniqueId().toString());
            if (plugin.getPluginConfig().isWorldIndependent()) statement.setString(2, world);

            final ResultSet resultSet = statement.executeQuery();

            int result = 0;
            if (resultSet.next()) {
                result = resultSet.getInt("vd_admin");
            }

            resultSet.close();
            statement.close();

            return result;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static boolean isPingMode(OfflinePlayer player) {
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
    }

    public static List<WorldDataRow> getRows(OfflinePlayer player) {
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
}

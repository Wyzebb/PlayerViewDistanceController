package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.IntegrationManager;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Manages player data operations including reset and query functionality.
 */
public class PlayerDataManager {

    /**
     * Ensures player data is loaded from file into memory for an online player.
     * Should be called on join to load saved preferences into the DataHandler.
     * 
     * @param player The online player whose data should be loaded
     */
    public static void ensureDataLoaded(Player player) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);
        
        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
            dataHandler.setAdminChunks(cfg.getInt("chunksOthers"));
            dataHandler.setPingMode(cfg.getBoolean("pingMode"));
        }
    }

    /**
     * Resets all view distance data for a player to default values.
     * Handles both online and offline players appropriately.
     * 
     * @param player The player whose data should be reset
     */
    public static void resetPlayerData(OfflinePlayer player) {
        File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

        if (player.isOnline()) {
            dataHandler.setChunks(ClampAmountUtility.getMaxPossible());
            dataHandler.setAdminChunks(0);
            dataHandler.setPingMode(false);

            DataHandlerHandler.setPlayerDataHandler(player, dataHandler);
        } else if (plugin.getPluginConfig().savePlayerData()) {
            cfg.set("chunks", ClampAmountUtility.getMaxPossible());
            cfg.set("chunksOthers", 0);
            cfg.set("pingMode", false);

            try {
                cfg.save(playerDataFile);
            } catch (Exception ex) {
                plugin.getLogger().severe("An error occurred when resetting view distance data for " + player.getName() + ": " + ex.getMessage());
            }
        }

        if (player.isOnline()) {
            // Clear AFK state and transition to active
            plugin.getStateManager().transitionState(player.getPlayer(), PlayerState.ACTIVE);
            
            // Apply view distance reset using factory
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer == null) {
                plugin.getLogger().warning("Player went offline during reset operation for " + player.getName());
                return;
            }
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createResetContext(onlinePlayer);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }

    /**
     * Gets the current effective view distance for a player.
     * Considers permissions, saved data, and override settings.
     * Handles both online and offline players appropriately.
     * 
     * @param player The player to get view distance for
     * @return The current effective view distance
     */
    public static int getCurrentViewDistance(OfflinePlayer player) {
        int luckpermsDistance = IntegrationManager.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);
        PlayerDataHandler playerDataHandler;

        if (!player.isOnline()) {
            File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);

            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

            if (playerDataFile.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
                dataHandler.setAdminChunks(cfg.getInt("chunksOthers"));
                dataHandler.setPingMode(cfg.getBoolean("pingMode"));
            }

            playerDataHandler = dataHandler;
        } else {
            playerDataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        }

        int finalChunks = Math.min(playerDataHandler.getChunks(), luckpermsDistance);

        if (playerDataHandler.getAdminChunks() != 0 && playerDataHandler.getAdminChunks() != -1) {
            finalChunks = DataHandlerHandler.getPlayerDataHandler(player).getAdminChunks();
        }

        return finalChunks;
    }
}
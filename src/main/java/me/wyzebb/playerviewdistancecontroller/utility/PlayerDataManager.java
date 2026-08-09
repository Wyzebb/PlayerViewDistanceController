package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.IntegrationManager;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Manages player data operations including reset and query functionality.
 */
public class PlayerDataManager {

    /**
     * Resets all view distance data for a player to default values.
     * Handles both online and offline players appropriately.
     * 
     * @param player The player whose data should be reset
     */
    public static void resetPlayerData(OfflinePlayer player) {
        String worldId = player.isOnline() ? (plugin.getPluginConfig().isWorldIndependent() ? ((Player) player).getWorld().getUID().toString() : "DEFAULT") : "DEFAULT";
        
        Storage.setChunks(player, worldId, ClampAmountUtility.getMaxPossible());
        Storage.setAdminChunks(player, worldId, 0);
        Storage.setPingMode(player, false);

        if (player.isOnline()) {
            Player onlinePlayer = (Player) player;
            
            // Clear AFK state and transition to active
            plugin.getStateManager().transitionState(onlinePlayer, PlayerState.ACTIVE);
            
            // Apply view distance reset using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createResetContext(onlinePlayer);
            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }

    /**
     * Gets the current effective view distance for a player.
     * Considers permissions, saved data, and override settings.
     * 
     * @param player The player to get view distance for
     * @return The current effective view distance
     */
    public static int getCurrentViewDistance(OfflinePlayer player) {
        int luckpermsDistance = IntegrationManager.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);
        
        final String worldId = player.isOnline() ? (plugin.getPluginConfig().isWorldIndependent() ? ((Player) player).getWorld().getUID().toString() : "DEFAULT") : "DEFAULT";
        
        int savedChunks = Storage.getChunks(player, worldId);
        int adminChunks = Storage.getAdminChunks(player, worldId);
        
        int finalChunks = Math.min(savedChunks, luckpermsDistance);

        if (adminChunks != 0 && adminChunks != -1) {
            finalChunks = adminChunks;
        }

        return finalChunks;
    }
}
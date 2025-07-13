package me.wyzebb.playerviewdistancecontroller.utility;

import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Utility class for managing player view distances with client optimization support.
 * Provides centralized view distance calculation and application logic.
 */
public class ViewDistanceUtility {
    
    /**
     * Result container for view distance operations
     */
    public static class ViewDistanceResult {
        private final int viewDistance;
        private final int simulationDistance;
        private final boolean wasOptimized;
        
        public ViewDistanceResult(int viewDistance, int simulationDistance, boolean wasOptimized) {
            this.viewDistance = viewDistance;
            this.simulationDistance = simulationDistance;
            this.wasOptimized = wasOptimized;
        }
        
        public int getViewDistance() { return viewDistance; }
        public int getSimulationDistance() { return simulationDistance; }
        public boolean wasOptimized() { return wasOptimized; }
    }
    
    /**
     * Calculates the optimal view distance considering client limitations
     */
    public static int calculateOptimalViewDistance(Player player, int serverCalculatedDistance) {
        if (!isClientOptimizationEnabled()) {
            return serverCalculatedDistance;
        }
        
        try {
            int clientViewDistance = player.getClientViewDistance();
            int optimalDistance = Math.min(serverCalculatedDistance, clientViewDistance);

            return optimalDistance;
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get client view distance for player " + player.getName() + 
                ": " + e.getMessage() + ". Using server calculated distance.");
            return serverCalculatedDistance;
        }
    }
    
    /**
     * Applies optimal view and simulation distances and returns the result
     */
    public static ViewDistanceResult applyOptimalViewDistance(Player player, int serverCalculatedDistance) {
        int optimalViewDistance = calculateOptimalViewDistance(player, serverCalculatedDistance);
        int simulationDistance = calculateSimulationDistance(optimalViewDistance, serverCalculatedDistance);
        boolean wasOptimized = optimalViewDistance < serverCalculatedDistance;
        
        try {
            // Apply view distance
            player.setViewDistance(optimalViewDistance);
            
            // Apply simulation distance if sync is enabled
            if (isSimulationSyncEnabled()) {
                player.setSimulationDistance(simulationDistance);
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to apply view distance for player " + player.getName() + 
                ": " + e.getMessage());
        }
        
        return new ViewDistanceResult(optimalViewDistance, simulationDistance, wasOptimized);
    }
    
    /**
     * Calculates appropriate simulation distance based on configuration
     */
    private static int calculateSimulationDistance(int optimalViewDistance, int serverCalculatedDistance) {
        if (!isSimulationSyncEnabled()) {
            return 0; // Not applicable
        }
        
        // Use optimal view distance for simulation distance when syncing
        return optimalViewDistance;
    }
    
    /**
     * Checks if client view distance optimization is enabled
     */
    private static boolean isClientOptimizationEnabled() {
        return plugin.getConfig().getBoolean("use-client-view-distance", false);
    }
    
    /**
     * Checks if simulation distance sync is enabled
     */
    private static boolean isSimulationSyncEnabled() {
        return plugin.getConfig().getBoolean("sync-simulation-distance", true);
    }
}
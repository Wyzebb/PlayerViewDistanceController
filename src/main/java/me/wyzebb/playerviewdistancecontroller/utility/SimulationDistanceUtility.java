package me.wyzebb.playerviewdistancecontroller.utility;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SimulationDistanceUtility {

    private static final int MAX_POSSIBLE = 32;
    private static final int MIN_POSSIBLE = 2;

    /**
     * Calculate simulation distance based on view distance and sync percentage
     * @param viewDistance The current view distance
     * @return The calculated simulation distance
     */
    public static int calculateSimulationDistance(int viewDistance) {
        int syncPercent = plugin.getConfig().getInt("sync-simulation-distance-percent", 60);

        // If sync is disabled (0%), use default simulation distance settings
        if (syncPercent <= 0) {
            return clampSimulationDistance(plugin.getConfig().getInt("default-simulation-distance", 10));
        }

        // Calculate simulation distance as percentage of view distance
        int simulationDistance = (int) Math.ceil((viewDistance * syncPercent) / 100.0);

        return clampSimulationDistance(simulationDistance);
    }

    /**
     * Clamp simulation distance to configured min/max values
     * @param amount The simulation distance to clamp
     * @return The clamped simulation distance
     */
    public static int clampSimulationDistance(int amount) {
        amount = Math.min(MAX_POSSIBLE, amount);
        amount = Math.max(MIN_POSSIBLE, amount);

        amount = Math.min(plugin.getConfig().getInt("max-simulation-distance", 32), amount);
        amount = Math.max(plugin.getConfig().getInt("min-simulation-distance", 2), amount);

        return amount;
    }

    /**
     * Check if simulation distance syncing is enabled
     * @return true if sync percentage is greater than 0
     */
    public static boolean isSimulationSyncEnabled() {
        return plugin.getConfig().getInt("sync-simulation-distance-percent", 100) > 0;
    }
}

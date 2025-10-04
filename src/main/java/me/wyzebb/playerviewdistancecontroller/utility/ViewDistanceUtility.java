package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;

import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Utility class for managing player view distances.
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
        private final boolean wasClientLimited;
        private final boolean wasPermissionLimited;
        private final boolean wasAfkLimited;
        
        public ViewDistanceResult(int viewDistance, int simulationDistance, boolean wasOptimized, 
                                boolean wasClientLimited, boolean wasPermissionLimited, boolean wasAfkLimited) {
            this.viewDistance = viewDistance;
            this.simulationDistance = simulationDistance;
            this.wasOptimized = wasOptimized;
            this.wasClientLimited = wasClientLimited;
            this.wasPermissionLimited = wasPermissionLimited;
            this.wasAfkLimited = wasAfkLimited;
        }
        
        public int getViewDistance() { return viewDistance; }
        public int getSimulationDistance() { return simulationDistance; }
        public boolean wasOptimized() { return wasOptimized; }
        public boolean wasClientLimited() { return wasClientLimited; }
        public boolean wasPermissionLimited() { return wasPermissionLimited; }
        public boolean wasAfkLimited() { return wasAfkLimited; }
    }
    
    /**
     * Comprehensive view distance calculation using context object
     */
    public static ViewDistanceResult calculateAndApplyViewDistance(ViewDistanceCalculationContext context) {
        Player player = context.getPlayer();
        
        // Check if player should use AFK view distance
        if (context.getPlayerState().shouldUseAfkViewDistance()) {
            int afkDistance = calculateAfkViewDistance();
            
            applyViewDistanceToPlayer(player, afkDistance);
            return new ViewDistanceResult(afkDistance, 
                calculateSimulationDistance(afkDistance, context.getBaseViewDistance()), 
                false, false, false, true);
        }
        
        // Get effective base distance from context
        int baseViewDistance = context.getEffectiveBaseDistance();
        
        // Get permission max distance from context
        int permissionMaxDistance = context.getPermissionMaxDistance();
        
        // Apply permission limit to base distance
        int effectiveDistance = Math.min(baseViewDistance, permissionMaxDistance);
        boolean wasPermissionLimited = effectiveDistance < baseViewDistance;
        
        // Apply client preference if enabled
        boolean wasClientLimited = false;
        if (isClientOptimizationEnabled()) {
            int clientViewDistance = context.getClientPreferredDistance();
            
            if (clientViewDistance > 0) {
                // Cap client preference by permission limit
                int cappedClientDistance = Math.min(clientViewDistance, permissionMaxDistance);
                
                if (cappedClientDistance < clientViewDistance) {
                    plugin.getLogger().fine("Client requested " + clientViewDistance + 
                                          " chunks but limited to " + permissionMaxDistance + " by permissions");
                }
                
                // Apply client preference (most restrictive wins)
                int beforeClientLimit = effectiveDistance;
                effectiveDistance = Math.min(effectiveDistance, cappedClientDistance);
                wasClientLimited = effectiveDistance < beforeClientLimit;
            }
        }
        
        // Apply dynamic mode reduction
        if (context.isDynamicModeEnabled()) {
            effectiveDistance -= context.getDynamicReduction();
        }
        
        // Clamp to valid range
        int finalDistance = ClampAmountUtility.clampChunkValue(effectiveDistance);
        
        // Apply to player
        applyViewDistanceToPlayer(player, finalDistance);
        
        boolean wasOptimized = finalDistance < baseViewDistance;
        return new ViewDistanceResult(finalDistance, calculateSimulationDistance(finalDistance, baseViewDistance), 
                                    wasOptimized, wasClientLimited, wasPermissionLimited, false);
    }
    
    /**
     * Main method for applying view distance using context with messaging support
     */
    public static ViewDistanceResult applyOptimalViewDistance(ViewDistanceCalculationContext context) {
        ViewDistanceResult result = calculateAndApplyViewDistance(context);
        
        updatePlayerDataHandler(context, result);
        processMessaging(context, result);
        
        if (context.isPingModeEnabled()) {
            PingModeHandler.optimisePing(context.getPlayer());
        }
        
        return result;
    }
    
    
    /**
     * Calculates AFK view distance based on configuration
     */
    private static int calculateAfkViewDistance() {
        if (plugin.getPluginConfig().isVoidAfkEnabled()) {
            return 0;
        } else {
            return ClampAmountUtility.clampChunkValue(plugin.getPluginConfig().getAfkChunks());
        }
    }
    
    /**
     * Applies view distance to the player
     */
    private static void applyViewDistanceToPlayer(Player player, int viewDistance) {
        try {
            player.setViewDistance(viewDistance);
            
            // Apply simulation distance if sync is enabled
            if (isSimulationSyncEnabled()) {
                player.setSimulationDistance(calculateSimulationDistance(viewDistance, viewDistance));
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to apply view distance for player " + player.getName() + 
                ": " + e.getMessage());
        }
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
        return plugin.getPluginConfig().useClientViewDistance();
    }
    
    /**
     * Checks if simulation distance sync is enabled
     */
    private static boolean isSimulationSyncEnabled() {
        return plugin.getPluginConfig().isSyncSimulationDistanceEnabled();
    }
    
    /**
     * Processes appropriate messaging based on context and result
     */
    private static void processMessaging(ViewDistanceCalculationContext context, ViewDistanceResult result) {
        // Skip all messaging if explicitly disabled
        if (context.shouldSendNoMessages()) {
            return;
        }
        
        Player player = context.getPlayer();
        int appliedDistance = result.getViewDistance();
        int permissionMaxDistance = context.getPermissionMaxDistance();
        boolean wasPermissionLimited = result.wasPermissionLimited();
        
        // Handle world change messaging
        if (context.isWorldChange() && wasPermissionLimited) {
            MessageProcessor.processMessage("not-max", MessageType.INFO, appliedDistance, permissionMaxDistance, player);
            return;
        }
        
        // Handle LuckPerms event messaging
        if (context.isLuckPermsEvent()) {
            if (context.isWorldChange()) {
                if (plugin.getPluginConfig().msgOnWorldChange()) {
                    MessageProcessor.processMessage("target-view-distance-change", MessageType.INFO, appliedDistance, player);
                }
            } else {
                MessageProcessor.processMessage("target-view-distance-change", MessageType.INFO, appliedDistance, player);
            }
            return;
        }
        
        // Handle join messaging
        if (!context.isLuckPermsEvent() && !context.shouldSendNoMessages()) {
            handleJoinMessaging(context, result, appliedDistance, permissionMaxDistance, wasPermissionLimited, player);
        }
    }
    
    /**
     * Handles the join messaging logic
     */
    private static void handleJoinMessaging(ViewDistanceCalculationContext context, ViewDistanceResult result,
                                          int appliedDistance, int permissionMaxDistance, boolean wasPermissionLimited, Player player) {
        
        // Check if join messages are enabled and not in AFK-on-join mode
        if (!plugin.getPluginConfig().msgOnJoin() ||
            plugin.getPluginConfig().isAfkOnJoinEnabled()) {
            return;
        }
        
        boolean isBedrockPlayer = context.isBedrockPlayer();
        int maxDistance = plugin.getPluginConfig().getMaxDistance();
        int defaultDistance = plugin.getPluginConfig().getDefaultDistance();
        int bedrockDefaultDistance = plugin.getPluginConfig().getBedrockDefaultDistance();
        
        // Check if we should show the "max join message"
        if (plugin.getPluginConfig().msgOnJoinMax()) {
            // Player is at maximum possible distance
            if (appliedDistance == maxDistance || 
                (appliedDistance == defaultDistance && !isBedrockPlayer) ||
                (appliedDistance == bedrockDefaultDistance && isBedrockPlayer) ||
                appliedDistance == ClampAmountUtility.getMaxPossible()) {
                
                MessageProcessor.processMessage("join", MessageType.INFO, appliedDistance, player);
                return;
            }
            
            // Player's distance was limited, check if we should show "not-max" message
            if (plugin.getPluginConfig().msgOnJoinMaxView() && wasPermissionLimited) {
                MessageProcessor.processMessage("not-max", MessageType.INFO, appliedDistance, permissionMaxDistance, player);
                return;
            }
            
            // Default join message
            MessageProcessor.processMessage("join", MessageType.INFO, appliedDistance, player);
            return;
        }
        
        // Not showing max join messages, but check for "not-max" message
        if (plugin.getPluginConfig().msgOnJoinMaxView() && wasPermissionLimited) {
            MessageProcessor.processMessage("not-max", MessageType.INFO, appliedDistance, permissionMaxDistance, player);
            return;
        }
        
        // Default join message
        MessageProcessor.processMessage("join", MessageType.INFO, appliedDistance, player);
    }
    
    private static void updatePlayerDataHandler(ViewDistanceCalculationContext context, ViewDistanceResult result) {
        if (!context.isLuckPermsEvent()) {
            Player player = context.getPlayer();
            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
            
            // Save user preference
            dataHandler.setChunks(context.getSavedViewDistance());
            dataHandler.setChunksOthers(context.getSavedOthersDistance());
            dataHandler.setPingMode(context.isPingModeEnabled());
            
            DataHandlerHandler.setPlayerDataHandler(player, dataHandler);
        }
    }
}
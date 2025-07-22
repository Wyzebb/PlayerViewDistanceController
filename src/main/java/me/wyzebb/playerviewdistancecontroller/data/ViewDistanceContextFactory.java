package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.config.ConfigKeys;
import me.wyzebb.playerviewdistancecontroller.integrations.ClientViewDistanceTracker;
import me.wyzebb.playerviewdistancecontroller.integrations.GeyserDetector;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Factory class for creating ViewDistanceCalculationContext objects with common patterns.
 */
public class ViewDistanceContextFactory {

    private ViewDistanceContextFactory() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Creates a standard context for normal view distance calculations.
     * Used for most common scenarios including player joins, permission changes, and general updates.
     */
    public static ViewDistanceCalculationContext createStandardContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(false)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for player join events.
     */
    public static ViewDistanceCalculationContext createJoinContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(false)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for AFK state transitions.
     * Uses custom base distance and suppresses messages during AFK transitions.
     */
    public static ViewDistanceCalculationContext createAfkContext(Player player, int afkChunks) {
        return createBaseContext(player)
            .withBaseViewDistance(afkChunks)
            .withLuckPermsEvent(true)
            .withSendNoMessages(true)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for returning from AFK state.
     * Uses standard base distance and allows normal messaging.
     */
    public static ViewDistanceCalculationContext createReturnFromAfkContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(true)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for world change events.
     * Enables world change flag for appropriate messaging.
     */
    public static ViewDistanceCalculationContext createWorldChangeContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(false)
            .withSendNoMessages(false)
            .withWorldChange(true)
            .build();
    }

    /**
     * Creates a context for LuckPerms permission change events.
     * Enables LuckPerms event flag for permission-related messaging.
     */
    public static ViewDistanceCalculationContext createPermissionChangeContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(true)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for client view distance changes.
     * Suppresses messages and handles client preference updates.
     */
    public static ViewDistanceCalculationContext createClientChangeContext(Player player, int clientViewDistance) {
        return createBaseContext(player)
            .withClientPreferredDistance(clientViewDistance)
            .withLuckPermsEvent(true)
            .withSendNoMessages(true)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for administrative commands.
     * Uses specified base distance and enables appropriate messaging.
     */
    public static ViewDistanceCalculationContext createCommandContext(Player player, int commandDistance) {
        return createBaseContext(player)
            .withBaseViewDistance(commandDistance)
            .withLuckPermsEvent(false)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for stopping dynamic mode.
     * Disables dynamic mode and enables permission-based messaging.
     */
    public static ViewDistanceCalculationContext createStopDynamicModeContext(Player player) {
        return createBaseContextWithPlayerData(player)
            .withDynamicMode(false, 0) // Dynamic mode is being stopped
            .withLuckPermsEvent(true)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for stopping ping mode.
     * Disables ping mode and enables permission-based messaging.
     */
    public static ViewDistanceCalculationContext createStopPingModeContext(Player player) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        return createBaseContextWithoutPingMode(player, dataHandler)
            .withPingMode(false) // Ping mode is being stopped
            .withLuckPermsEvent(true)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates a context for data reset operations.
     * Used when resetting player data to defaults.
     */
    public static ViewDistanceCalculationContext createResetContext(Player player) {
        return createBaseContext(player)
            .withLuckPermsEvent(true)
            .withSendNoMessages(false)
            .withWorldChange(false)
            .build();
    }

    /**
     * Creates the base context builder with common parameters that are used in most scenarios.
     */
    private static ViewDistanceCalculationContext.Builder createBaseContext(Player player) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        return createBaseContextWithPlayerData(player, dataHandler);
    }

    /**
     * Creates base context with provided player data handler.
     * Useful when the data handler is already available.
     */
    private static ViewDistanceCalculationContext.Builder createBaseContextWithPlayerData(Player player) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        return createBaseContextWithPlayerData(player, dataHandler);
    }

    /**
     * Creates base context with specific player data handler.
     */
    private static ViewDistanceCalculationContext.Builder createBaseContextWithPlayerData(Player player, PlayerDataHandler dataHandler) {
        return ViewDistanceCalculationContext.builder(player)
            .withPlayerState(plugin.getStateManager().getPlayerState(player.getUniqueId()))
            .withBaseViewDistance(getDefaultViewDistance(player))
            .withSavedViewDistance(dataHandler.getChunks())
            .withSavedOthersDistance(dataHandler.getChunksOthers())
            .withPingMode(dataHandler.isPingMode())
            .withDynamicMode(PlayerViewDistanceController.dynamicModeEnabled, PlayerViewDistanceController.dynamicReducedChunks)
            .withPermissionMaxDistance(ClampAmountUtility.clampChunkValue(LPDetector.getLuckpermsDistance(player)))
            .withClientPreferredDistance(ClientViewDistanceTracker.getLastKnownClientVD(player.getUniqueId()))
            .withBedrockPlayer(GeyserDetector.checkBedrockPlayer(player.getUniqueId()));
    }

    /**
     * Creates base context without ping mode (used when stopping ping mode).
     */
    private static ViewDistanceCalculationContext.Builder createBaseContextWithoutPingMode(Player player, PlayerDataHandler dataHandler) {
        return ViewDistanceCalculationContext.builder(player)
            .withPlayerState(plugin.getStateManager().getPlayerState(player.getUniqueId()))
            .withBaseViewDistance(getDefaultViewDistance(player))
            .withSavedViewDistance(dataHandler.getChunks())
            .withSavedOthersDistance(dataHandler.getChunksOthers())
            .withDynamicMode(PlayerViewDistanceController.dynamicModeEnabled, PlayerViewDistanceController.dynamicReducedChunks)
            .withPermissionMaxDistance(ClampAmountUtility.clampChunkValue(LPDetector.getLuckpermsDistance(player)))
            .withClientPreferredDistance(ClientViewDistanceTracker.getLastKnownClientVD(player.getUniqueId()))
            .withBedrockPlayer(GeyserDetector.checkBedrockPlayer(player.getUniqueId()));
    }

    /**
     * Gets the appropriate default view distance for a player (Bedrock vs Java).
     */
    private static int getDefaultViewDistance(Player player) {
        boolean isBedrockPlayer = GeyserDetector.checkBedrockPlayer(player.getUniqueId());
        String configKey = isBedrockPlayer ? ConfigKeys.BEDROCK_DEFAULT_DISTANCE : ConfigKeys.DEFAULT_DISTANCE;
        return ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt(configKey));
    }

    /**
     * Creates a completely custom context builder for special cases.
     * Should be used sparingly when the predefined factory methods don't fit the use case.
     */
    public static ViewDistanceCalculationContext.Builder createCustomContext(Player player) {
        return createBaseContext(player);
    }
}
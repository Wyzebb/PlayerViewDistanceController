package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.*;

public class DynamicModeHandler {

    public static void checkServerMSPT() {
        if (dynamicModeEnabled) {
            int chunksToReduceBy = checkChunksToReduceBy();
            dynamicReducedChunks = chunksToReduceBy;

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("pvdc.dynamic-mode-bypass")) {
                    int luckpermsDistance = LPDetector.getLuckpermsDistance(player);
                    luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

                    PlayerDataHandler playerDataHandler = DataHandlerHandler.getPlayerDataHandler(player);
                    int maxAllowed = ClampAmountUtility.clampChunkValue(ClampAmountUtility.getMaxPossible());

                    if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
                        maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
                    }

                    int optimisedChunks = ClampAmountUtility.clampChunkValue(maxAllowed);

                    optimisedChunks = Math.max(optimisedChunks, plugin.getPingOptimiserConfig().getInt("min"));
                    optimisedChunks = Math.min(optimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

                    // Build context for view distance calculation
                    ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCustomContext(player)
                        .withBaseViewDistance(optimisedChunks)
                        .withDynamicMode(true, chunksToReduceBy)
                        .withSendNoMessages(true)
                        .build();

                    ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
                    int actualOptimisedChunks = result.getViewDistance();

                    if (actualOptimisedChunks != maxAllowed) {
                        MessageProcessor.processMessage("dynamic-mode-reduced", MessageType.SUCCESS, player);
                    }
                }
            }
        }
    }

    public static int checkChunksToReduceBy() {
        double MSPT = Bukkit.getServer().getAverageTickTime();
        Set<String> keys = Objects.requireNonNull(plugin.getDynamicModeConfig().getConfigurationSection("mspt")).getKeys(false);
        int chunksToReduceBy = 0;

        if (!keys.isEmpty()) {
            for (String key : keys) {
                if (MSPT >= Integer.parseInt(key)) {
                    int val = plugin.getDynamicModeConfig().getInt(("mspt." + key));
                    chunksToReduceBy = Math.max(chunksToReduceBy, val);
                }
            }

            return chunksToReduceBy;
        }

        plugin.getLogger().severe("There are no MSPT keys in the dynamic mode config! Dynamic mode will not work until you fix this!");

        for (Player player : Bukkit.getOnlinePlayers()) {
            MessageProcessor.processMessage("no-keys-dynamic", MessageType.ERROR, player);
        }

        dynamicModeEnabled = false;
        dynamicReducedChunks = 0;

        plugin.stopDynamicMode();

        return chunksToReduceBy;
    }
}

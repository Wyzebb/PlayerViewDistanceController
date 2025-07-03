package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
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
                    int maxAllowed = ClampAmountUtility.clampChunkValue(32);

                    if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
                        maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
                    }

                    int optimisedChunks = ClampAmountUtility.clampChunkValue(maxAllowed - chunksToReduceBy);

                    optimisedChunks = Math.max(optimisedChunks, plugin.getPingOptimiserConfig().getInt("min"));
                    optimisedChunks = Math.min(optimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

                    player.setViewDistance(optimisedChunks);

                    if (plugin.getConfig().getBoolean("sync-simulation-distance")) {
                        player.setSimulationDistance(optimisedChunks);
                    }

                    if (optimisedChunks != maxAllowed) {
                        MessageProcessor.processMessage("messages.dynamic-mode-reduced", 2, player);
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
            MessageProcessor.processMessage("messages.no-keys-dynamic", 1, player);
        }

        dynamicModeEnabled = false;
        dynamicReducedChunks = 0;

        plugin.stopDynamicMode();

        return chunksToReduceBy;
    }
}

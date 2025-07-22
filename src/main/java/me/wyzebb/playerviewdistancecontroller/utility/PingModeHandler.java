package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.*;

public class PingModeHandler {

    public static void optimisePingPerPlayer() {
        if (!pingModeDisabled) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                optimisePing(player);
            }
        }
    }

    public static void optimisePing(Player player) {
        if (!pingModeDisabled) {
            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);
            if (dataHandler.isPingMode()) {
                int luckpermsDistance = LPDetector.getLuckpermsDistance(player);
                luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

                PlayerDataHandler playerDataHandler = DataHandlerHandler.getPlayerDataHandler(player);
                int maxAllowed = ClampAmountUtility.clampChunkValue(ClampAmountUtility.getMaxPossible());

                if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
                    maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
                }

                int pingOptimisedChunks = Math.max(maxAllowed, plugin.getPingOptimiserConfig().getInt("min"));
                pingOptimisedChunks = Math.min(pingOptimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

                pingOptimisedChunks = comparePingToConfig(pingOptimisedChunks, player.getPing());

                if (dynamicModeEnabled) {
                    pingOptimisedChunks = comparePingToConfig(pingOptimisedChunks - dynamicReducedChunks, player.getPing());
                }

                if (pingOptimisedChunks == 1000) {
                    MessageProcessor.processMessage("messages.no-keys-ping", 1, player);
                    plugin.stopPingMode();
                    return;
                }

                if (dynamicModeEnabled && (maxAllowed - dynamicReducedChunks != pingOptimisedChunks)) {
                    DataProcessorUtility.processPingChunks(player, pingOptimisedChunks);
                    return;
                }

                if (!dynamicModeEnabled && (maxAllowed != pingOptimisedChunks)) {
                    DataProcessorUtility.processPingChunks(player, pingOptimisedChunks);
                }
            }
        }
    }

    public static int comparePingToConfig(int chunks, int ping) {
        Set<String> keys = Objects.requireNonNull(plugin.getPingOptimiserConfig().getConfigurationSection("pings")).getKeys(false);
        int chunksToReduceBy = 0;

        if (!keys.isEmpty()) {
            for (String key : keys) {
                if (ping >= Integer.parseInt(key)) {
                    int val = plugin.getPingOptimiserConfig().getInt(("pings." + key));
                    chunksToReduceBy = Math.max(chunksToReduceBy, val);
                }
            }

            int amount = ClampAmountUtility.clampChunkValue(chunks - chunksToReduceBy);

            amount = Math.max(amount, plugin.getPingOptimiserConfig().getInt("min"));
            amount = Math.min(amount, plugin.getPingOptimiserConfig().getInt("max"));

            return amount;
        }

        plugin.getLogger().severe("There are no ping keys in the ping mode config! Ping mode will not work until you fix this!");
        return 1000;
    }
}

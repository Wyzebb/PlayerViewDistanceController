package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.IntegrationManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
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
            if (Storage.isPingMode(player)) {
                int luckpermsDistance = IntegrationManager.getLuckpermsDistance(player);
                luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

                int maxAllowed = ClampAmountUtility.clampChunkValue(ClampAmountUtility.getMaxPossible());

                final int adminChunks = Storage.getAdminChunks(player, player.getWorld().getUID().toString());
                if (adminChunks != 0 && adminChunks != -1) {
                    maxAllowed = Math.min(adminChunks, luckpermsDistance);
                }

                int pingOptimisedChunks = Math.max(maxAllowed, plugin.getPingOptimiserConfig().getInt("min"));
                pingOptimisedChunks = Math.min(pingOptimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

                pingOptimisedChunks = comparePingToConfig(pingOptimisedChunks, player.getPing());

                if (dynamicModeEnabled) {
                    pingOptimisedChunks = comparePingToConfig(pingOptimisedChunks - dynamicReducedChunks, player.getPing());
                }

                if (pingOptimisedChunks == 1000) {
                    MessageProcessor.processMessage("no-keys-ping", MessageType.ERROR, player);
                    plugin.stopPingMode();
                    return;
                }

                if (dynamicModeEnabled && (maxAllowed - dynamicReducedChunks != pingOptimisedChunks)) {
                    processPingChunks(player, pingOptimisedChunks);
                    return;
                }

                if (!dynamicModeEnabled && (maxAllowed != pingOptimisedChunks)) {
                    processPingChunks(player, pingOptimisedChunks);
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

    private static void processPingChunks(Player target, int pingChunks) {
        ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(target, pingChunks);
        ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
        MessageProcessor.processMessage("ping-optimised", MessageType.SUCCESS, result.getViewDistance(), target);
    }
}

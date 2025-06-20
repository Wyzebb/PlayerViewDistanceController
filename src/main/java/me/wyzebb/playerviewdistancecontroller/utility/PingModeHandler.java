package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.*;

public class PingModeHandler {

    public static void optimisePingPerPlayer() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            optimisePing(player);
        }
    }

    public static void optimisePing(Player player) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);
        if (dataHandler.isPingMode()) {
            int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
            luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

            PlayerDataHandler playerDataHandler = PlayerUtility.getPlayerDataHandler(player);
            int maxAllowed = ClampAmountUtility.clampChunkValue(32);

            if (playerDataHandler.getChunksOthers() != 0) {
                maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
            }

            int pingOptimisedChunks = comparePingToConfig(maxAllowed, player.getPing());

            if (dynamicModeEnabled) {
                pingOptimisedChunks = comparePingToConfig(maxAllowed - dynamicReducedChunks, player.getPing());
            }

            if (pingOptimisedChunks == 1000) {
                player.sendMessage("Contact your admin. Ping error");
                return;
            }

            pingOptimisedChunks = Math.max(pingOptimisedChunks, plugin.getPingOptimiserConfig().getInt("min"));
            pingOptimisedChunks = Math.min(pingOptimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

            if (dynamicModeEnabled && (maxAllowed - dynamicReducedChunks != pingOptimisedChunks)) {
                DataProcessorUtility.processPingChunks(player, pingOptimisedChunks);
                return;
            }

            if (!dynamicModeEnabled && (maxAllowed != pingOptimisedChunks)) {
                DataProcessorUtility.processPingChunks(player, pingOptimisedChunks);
                return;
            }

            player.sendMessage("not optimised");
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

        plugin.getLogger().severe("There are no ping keys in the ping mode config!");
        return 1000;
    }
}

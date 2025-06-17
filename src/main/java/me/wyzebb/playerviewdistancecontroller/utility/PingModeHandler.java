package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Set;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class PingModeHandler {

    public static void optimisePing() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);
            if (dataHandler.isPingMode()) {
                int newChunks = comparePingToConfig(VdCalculator.calcVdGet(player), player.getPing());

                if (newChunks == 1000) return;

                int luckpermsMax = 32;

                if (LuckPermsDetector.detectLuckPerms()) {
                    luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance(player);
                }

                if (luckpermsMax >= newChunks || player.hasPermission("pvdc.bypass-maxdistance")) {
                    MessageProcessor.processMessage("messages.ping-optimised", 2, newChunks, player);

                    DataProcessorUtility.processPingChunks(player, newChunks);
                } else {
                    MessageProcessor.processMessage("messages.chunks-too-high", 1, luckpermsMax, player);
                }
            }
        }
    }

    public static void optimisePingOnce(Player player) {
        int ping = player.getPing();

        if (ping > 20) {
            int luckpermsMax = 32;
            int amount = PlayerUtility.getPlayerDataHandler(player).getChunks() - 2;

            if (LuckPermsDetector.detectLuckPerms()) {
                luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance(player);
            }

            if (luckpermsMax >= amount || player.hasPermission("pvdc.bypass-maxdistance")) {
                MessageProcessor.processMessage("messages.ping-optimised", 2, amount, player);

                DataProcessorUtility.processPingChunks(player, amount);
            } else {
                MessageProcessor.processMessage("messages.chunks-too-high", 1, luckpermsMax, player);
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
        return 1000;
    }
}

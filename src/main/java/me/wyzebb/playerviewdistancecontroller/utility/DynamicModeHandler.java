package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;
import java.util.Set;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DynamicModeHandler {

    public static void checkServerMSPT() {
        if (plugin.getDynamicModeConfig().getBoolean("enabled")) {
            double MSPT = Bukkit.getServer().getAverageTickTime();

            for (Player player : Bukkit.getOnlinePlayers()) {
                int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
                luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

                PlayerDataHandler playerDataHandler = PlayerUtility.getPlayerDataHandler(player);
                int value = ClampAmountUtility.clampChunkValue(32);

                if (playerDataHandler.getChunksOthers() != 0) {
                    value = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
                }

                int newChunks = optimiseChunks(VdCalculator.calcVdGet(player), MSPT);

                if (newChunks == 1000) return;

                if (newChunks < value) {
                    value = newChunks;
                }

                player.setViewDistance(value);
            }
        }
    }

    public static int optimiseChunks(int chunks, double MSPT) {
        Set<String> keys = Objects.requireNonNull(plugin.getDynamicModeConfig().getConfigurationSection("mspt")).getKeys(false);
        int chunksToReduceBy = 0;

        if (!keys.isEmpty()) {
            for (String key : keys) {
                if (MSPT >= Integer.parseInt(key)) {
                    int val = plugin.getDynamicModeConfig().getInt(("mspt." + key));
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

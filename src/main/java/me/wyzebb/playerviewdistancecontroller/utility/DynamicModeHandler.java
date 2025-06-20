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

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.dynamicModeEnabled;
import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DynamicModeHandler {

    public static void checkServerMSPT() {
        if (dynamicModeEnabled) {
            int chunksToReduceBy = checkChunksToReduceBy();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.hasPermission("pvdc.bypass-dynamic-mode")) {
                    int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
                    luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

                    PlayerDataHandler playerDataHandler = PlayerUtility.getPlayerDataHandler(player);
                    int maxAllowed = ClampAmountUtility.clampChunkValue(32);

                    if (playerDataHandler.getChunksOthers() != 0) {
                        maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
                    }

                    int optimisedChunks = ClampAmountUtility.clampChunkValue(maxAllowed - chunksToReduceBy);

                    optimisedChunks = Math.max(optimisedChunks, plugin.getPingOptimiserConfig().getInt("min"));
                    optimisedChunks = Math.min(optimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

                    player.setViewDistance(optimisedChunks);
                } else {
                    player.sendMessage("you bypassed dynamic mode");
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

        plugin.getLogger().severe("There are no MSPT keys in the dynamic mode config!");
        return chunksToReduceBy;
    }
}

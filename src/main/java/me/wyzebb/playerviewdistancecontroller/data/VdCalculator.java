package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class VdCalculator {
    public static void calcVdAndSet(Player player) {
        int amount = plugin.getConfig().getInt("default-distance");
        int amountOthers = 32;

        if (player.getName().startsWith(".")) {
            amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("bedrock-default-distance"));
        }

        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        PlayerUtility playerDataHandler = new PlayerUtility();
        File playerDataFile = playerDataHandler.getPlayerDataFile(player);

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");
            amountOthers = cfg.getInt("chunksOthers");
        }

        // Get max distances from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);

        amount = ClampAmountUtility.clampChunkValue(amount);
        amountOthers = ClampAmountUtility.clampChunkValue(amountOthers);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks = Math.min(amount, luckpermsDistance);

        if (amountOthers > finalChunks) {
            finalChunks = amountOthers;
        }

        dataHandler.setChunks(amount);
        dataHandler.setChunksOthers(amountOthers);

        player.setViewDistance(finalChunks);

        if (plugin.getConfig().getBoolean("display-msg-on-join")) {
            if (finalChunks == plugin.getConfig().getInt("max-distance") || finalChunks == ClampAmountUtility.getMaxPossible())  {
                if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                    ProcessConfigMessagesUtility.processMessage("join-msg", player, finalChunks);
                }
            } else {
                ProcessConfigMessagesUtility.processMessage("join-msg", player, finalChunks);
            }
        }
        PlayerUtility.setPlayerDataHandler(player, dataHandler);
    }
}

package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class VdCalculator {
    public static void calcVdAndSet(Player player) {
        int amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("default-distance"));
        int amountOthers = 0;

        if (player.getName().startsWith(".")) {
            amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("bedrock-default-distance"));
        }

        // Get an instance of the player data handler for the specific player
        PlayerUtility playerUtility = new PlayerUtility();
        File playerDataFile = playerUtility.getPlayerDataFile(player);

        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");
            amountOthers = cfg.getInt("chunksOthers");
        }

        // Get max distances from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);

        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks;

        if (player.hasPermission("pvdc.bypass-maxdistance")) {
            finalChunks = amount;
        } else {
            finalChunks = Math.min(amount, luckpermsDistance);
        }

        if (amountOthers != 0) {
            if (amountOthers > finalChunks) {
                finalChunks = amountOthers;
            }
        }

        dataHandler.setChunks(amount);
        dataHandler.setChunksOthers(amountOthers);

        player.setViewDistance(finalChunks);

        boolean bedrockPlayer = player.getName().startsWith(".");

        if (plugin.getConfig().getBoolean("display-msg-on-join")) {
            if (finalChunks == plugin.getConfig().getInt("max-distance") || (finalChunks == plugin.getConfig().getInt("default-distance") && !bedrockPlayer) || (finalChunks == plugin.getConfig().getInt("bedrock-default-distance") && bedrockPlayer) || finalChunks == ClampAmountUtility.getMaxPossible())  {
                if (plugin.getConfig().getBoolean("display-max-join")) {
                    MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                }
            } else {
                MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
            }
        }
        PlayerUtility.setPlayerDataHandler(player, dataHandler);
    }


    public static void calcVdAndSetNew(Player player) {
        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = new PlayerDataHandler();

        // Get max distances from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);

        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

//        plugin.getLogger().warning("FINAL CHUNKS " + luckpermsDistance);

        dataHandler.setChunks(32);
        dataHandler.setChunksOthers(0);

        player.setViewDistance(luckpermsDistance);

        PlayerUtility.setPlayerDataHandler(player, dataHandler);
    }

    public static void calcVdAndSetNoReset(Player player) {
        int amount = 32;
        int amountOthers = 0;

        // Get an instance of the player data handler for the specific player
        PlayerUtility playerUtility = new PlayerUtility();
        File playerDataFile = playerUtility.getPlayerDataFile(player);

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");
            amountOthers = cfg.getInt("chunksOthers");
        }

        // Get max distances from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);

        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks = Math.min(amount, luckpermsDistance);

        if (amountOthers != 0) {
            if (amountOthers > finalChunks) {
                finalChunks = amountOthers;
            }
        }

        player.setViewDistance(finalChunks);
    }


    public static int calcVdAndGet(Player player) {
        // Get max distances from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
//        plugin.getLogger().warning("LPD: " + luckpermsDistance);

        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks = Math.min(PlayerUtility.getPlayerDataHandler(player).getChunks(), luckpermsDistance);

//        plugin.getLogger().warning("final: " + finalChunks);

        if (PlayerUtility.getPlayerDataHandler(player).getChunksOthers() != 0) {
            if (PlayerUtility.getPlayerDataHandler(player).getChunksOthers() > finalChunks) {
                finalChunks = PlayerUtility.getPlayerDataHandler(player).getChunksOthers();
            }
        }

//        plugin.getLogger().warning("final2: " + finalChunks);

        return finalChunks;
    }
}

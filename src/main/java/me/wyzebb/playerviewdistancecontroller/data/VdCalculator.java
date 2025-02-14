package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
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
    public static void calcVdSet(Player player, boolean luckPermsEvent) {
        int amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("default-distance"));
        int amountOthers = 0;

        final boolean bedrockPlayer = player.getName().startsWith(".");

        if (bedrockPlayer) {
            amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("bedrock-default-distance"));
        }

        // Get an instance of the player data handler for the specific player
        PlayerUtility playerUtility = new PlayerUtility();
        File playerDataFile = playerUtility.getPlayerDataFile(player);

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = ClampAmountUtility.clampChunkValue(cfg.getInt("chunks"));
            amountOthers = cfg.getInt("chunksOthers");
        }

        // Get max distance from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks;

        if (player.hasPermission("pvdc.bypass-maxdistance")) {
            finalChunks = amount;
        } else {
            finalChunks = Math.min(amount, luckpermsDistance);
        }

        if (amountOthers != 0 && amountOthers != -1) {
            finalChunks = ClampAmountUtility.clampChunkValue(amountOthers);
        }

        if (!luckPermsEvent) {
            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

            dataHandler.setChunks(amount);
            dataHandler.setChunksOthers(amountOthers);

            PlayerUtility.setPlayerDataHandler(player, dataHandler);
        }

        player.setViewDistance(finalChunks);

        if (!luckPermsEvent) {
            if (plugin.getConfig().getBoolean("display-msg-on-join")) {
                if (finalChunks == plugin.getConfig().getInt("max-distance") || (finalChunks == plugin.getConfig().getInt("default-distance") && !bedrockPlayer) || (finalChunks == plugin.getConfig().getInt("bedrock-default-distance") && bedrockPlayer) || finalChunks == ClampAmountUtility.getMaxPossible()) {
                    if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                        MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                    }
                } else {
                    MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                }
            }
        }

        if (luckPermsEvent) {
            MessageProcessor.processMessage("messages.target-view-distance-change", 3, calcVdGet(player), player);
        }
    }


    public static void calcVdReset(Player player) {

        PlayerUtility playerUtility = new PlayerUtility();

        File playerDataFile = playerUtility.getPlayerDataFile(player);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = new PlayerDataHandler();

        // Get max distance from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        dataHandler.setChunks(32);
        dataHandler.setChunksOthers(0);

        cfg.set("chunks", 32);
        cfg.set("chunksOthers", 0);

        try {
            cfg.save(playerDataFile);
        } catch (Exception ex) {
            plugin.getLogger().severe("An exception occurred when resetting view distance data for " + player.getName() + ": " + ex.getMessage());
        } finally {
            PlayerViewDistanceController.playerAfkMap.remove(player.getUniqueId());
            PlayerUtility.setPlayerDataHandler(player, dataHandler);
        }

        player.setViewDistance(luckpermsDistance);

        MessageProcessor.processMessage("messages.target-view-distance-change", 3, luckpermsDistance, player);
    }


    public static int calcVdGet(Player player) {
        // Get max distance from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

        int finalChunks = Math.min(dataHandler.getChunks(), luckpermsDistance);

        if (dataHandler.getChunksOthers() != 0) {
            finalChunks = PlayerUtility.getPlayerDataHandler(player).getChunksOthers();
        }

        return finalChunks;
    }
}

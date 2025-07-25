package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.integrations.GeyserDetector;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.*;

public class VdCalculator {
    public static void calcVdSet(Player player, boolean luckPermsEvent, boolean sendNoMessages, boolean worldChange) {
        int amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("default-distance"));
        int amountOthers = 0;
        boolean pingMode = false;

        final boolean bedrockPlayer = GeyserDetector.checkBedrockPlayer(player.getUniqueId());

        if (bedrockPlayer) {
            amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("bedrock-default-distance"));
        }

        // Get an instance of the player data handler for the specific player
        File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);

        if (playerDataFile.exists() && !luckPermsEvent && PlayerViewDistanceController.isPlayerDataSavingEnabled()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = ClampAmountUtility.clampChunkValue(cfg.getInt("chunks"));
            amountOthers = cfg.getInt("chunksOthers");
            pingMode = cfg.getBoolean("pingMode");
        }

        if (player.isOnline() && luckPermsEvent) {
            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

            amount = ClampAmountUtility.clampChunkValue(dataHandler.getChunks());
            amountOthers = dataHandler.getChunksOthers();
            pingMode = dataHandler.isPingMode();
        }

        // Get max distance from LuckPerms
        int luckpermsDistance = ClampAmountUtility.clampChunkValue(LPDetector.getLuckpermsDistance(player));

        int finalChunks = Math.min(amount, luckpermsDistance);

        if (amountOthers != 0 && amountOthers != -1) {
            finalChunks = ClampAmountUtility.clampChunkValue(amountOthers);
        }

        if (player.isOnline() && dynamicModeEnabled) {
            PlayerDataHandler playerDataHandler = DataHandlerHandler.getPlayerDataHandler(player);
            int maxAllowed = ClampAmountUtility.clampChunkValue(32);

            if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
                maxAllowed = Math.min(playerDataHandler.getChunksOthers(), luckpermsDistance);
            }

            int optimisedChunks = Math.max(maxAllowed, plugin.getPingOptimiserConfig().getInt("min"));
            optimisedChunks = Math.min(optimisedChunks, plugin.getPingOptimiserConfig().getInt("max"));

            finalChunks = ClampAmountUtility.clampChunkValue(optimisedChunks - dynamicReducedChunks);
        }

        if (!luckPermsEvent) {
            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

            dataHandler.setChunks(amount);
            dataHandler.setChunksOthers(amountOthers);
            dataHandler.setPingMode(pingMode);

            DataHandlerHandler.setPlayerDataHandler(player, dataHandler);
        }

        player.setViewDistance(finalChunks);

        boolean msgSent = false;

        if (plugin.getConfig().getBoolean("sync-simulation-distance")) {
            player.setSimulationDistance(amount);
        }

        if (worldChange && finalChunks != luckpermsDistance) {
            MessageProcessor.processMessage("messages.not-max", 3, finalChunks, luckpermsDistance, player);
            msgSent = true;
        }

        if (!luckPermsEvent && !sendNoMessages) {
            if (plugin.getConfig().getBoolean("display-msg-on-join") && !plugin.getConfig().getBoolean("afkOnJoin")) {
                if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                    if (finalChunks == plugin.getConfig().getInt("max-distance") || (finalChunks == plugin.getConfig().getInt("default-distance") && !bedrockPlayer) || (finalChunks == plugin.getConfig().getInt("bedrock-default-distance") && bedrockPlayer) || finalChunks == ClampAmountUtility.getMaxPossible()) {
                        MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                        msgSent = true;
                    } else {
                        if (plugin.getConfig().getBoolean("display-max-change-join-msg") && luckpermsDistance != finalChunks) {
                            MessageProcessor.processMessage("messages.not-max", 3, finalChunks, luckpermsDistance, player);
                            msgSent = true;
                        } else {
                            MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                            msgSent = true;
                        }
                    }
                } else {
                    if (plugin.getConfig().getBoolean("display-max-change-join-msg") && luckpermsDistance != finalChunks) {
                        MessageProcessor.processMessage("messages.not-max", 3, finalChunks, luckpermsDistance, player);
                        msgSent = true;
                    } else {
                        MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                        msgSent = true;
                    }
                }
            }
        }

        if (luckPermsEvent && !sendNoMessages) {
            if (worldChange) {
                if (!msgSent && plugin.getConfig().getBoolean("send-msg-on-world-change")) {
                    MessageProcessor.processMessage("messages.target-view-distance-change", 3, finalChunks, player);
                }
            } else {
                MessageProcessor.processMessage("messages.target-view-distance-change", 3, finalChunks, player);
            }
        }

        if (pingMode) {
            PingModeHandler.optimisePing(player);
        }
    }

    public static void calcVdReset(OfflinePlayer player) {
        File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

        if (player.isOnline()) {
            dataHandler.setChunks(32);
            dataHandler.setChunksOthers(0);
            dataHandler.setPingMode(false);

            DataHandlerHandler.setPlayerDataHandler(player, dataHandler);
        } else if (PlayerViewDistanceController.isPlayerDataSavingEnabled()) {
            cfg.set("chunks", 32);
            cfg.set("chunksOthers", 0);
            cfg.set("pingMode", false);

            try {
                cfg.save(playerDataFile);
            } catch (Exception ex) {
                plugin.getLogger().severe("An error occurred when resetting view distance data for " + player.getName() + ": " + ex.getMessage());
            }
        }

        if (player.isOnline()) {
            PlayerViewDistanceController.playerAfkMap.remove(player.getUniqueId());
            VdCalculator.calcVdSet(player.getPlayer(), true, false, false);
        }
    }


    public static int calcVdGet(OfflinePlayer player) {
        // Get max distance from LuckPerms
        int luckpermsDistance = LPDetector.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);
        PlayerDataHandler playerDataHandler;

        if (!player.isOnline()) {
            // Get an instance of the player data handler for the specific player
            File playerDataFile = DataHandlerHandler.getPlayerDataFile(player);

            PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(player);

            if (playerDataFile.exists() && PlayerViewDistanceController.isPlayerDataSavingEnabled()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
                dataHandler.setChunksOthers(cfg.getInt("chunksOthers"));
                dataHandler.setPingMode(cfg.getBoolean("pingMode"));
            }

            playerDataHandler = dataHandler;
        } else {
            playerDataHandler = DataHandlerHandler.getPlayerDataHandler(player);
        }

        int finalChunks = Math.min(playerDataHandler.getChunks(), luckpermsDistance);

        if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
            finalChunks = DataHandlerHandler.getPlayerDataHandler(player).getChunksOthers();
        }

        return finalChunks;
    }
}

package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PingModeHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.*;

public class VdCalculator {
    public static void calcVdSet(Player player, boolean luckPermsEvent, boolean sendNoMessages) {
        int amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("default-distance"));
        int amountOthers = 0;
        boolean pingMode = false;
        int amountPing = 0;

        final boolean bedrockPlayer = player.getName().startsWith(".");

        if (bedrockPlayer) {
            amount = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("bedrock-default-distance"));
        }

        // Get an instance of the player data handler for the specific player
        PlayerUtility playerUtility = new PlayerUtility();
        File playerDataFile = playerUtility.getPlayerDataFile(player);

        if (playerDataFile.exists() && !luckPermsEvent) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = ClampAmountUtility.clampChunkValue(cfg.getInt("chunks"));
            amountOthers = cfg.getInt("chunksOthers");
            pingMode = cfg.getBoolean("pingMode");
            amountPing = cfg.getInt("chunksPing");
        }

        // Get max distance from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);

        int finalChunks = Math.min(amount, luckpermsDistance);

        if (amountOthers != 0 && amountOthers != -1) {
            finalChunks = ClampAmountUtility.clampChunkValue(amountOthers);
        }

        if (dynamicModeEnabled) {
            finalChunks -= dynamicReducedChunks;
        }

        if (pingMode) {
            PingModeHandler.optimisePing(player);
        }

        if (!luckPermsEvent) {
            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

            dataHandler.setChunks(amount);
            dataHandler.setChunksOthers(amountOthers);
            dataHandler.setPingMode(pingMode);
            dataHandler.setChunksPing(amountPing);

            PlayerUtility.setPlayerDataHandler(player, dataHandler);
        }

        player.setViewDistance(finalChunks);

        if (plugin.getConfig().getBoolean("sync-simulation-distance")) {
            player.setSimulationDistance(amount);
        }

        if (!luckPermsEvent) {
            if (plugin.getConfig().getBoolean("display-msg-on-join")) {
                if (finalChunks == plugin.getConfig().getInt("max-distance") || (finalChunks == plugin.getConfig().getInt("default-distance") && !bedrockPlayer) || (finalChunks == plugin.getConfig().getInt("bedrock-default-distance") && bedrockPlayer) || finalChunks == ClampAmountUtility.getMaxPossible()) {
                    if (!plugin.getConfig().getBoolean("afkOnJoin")) {
                        if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                            if (!sendNoMessages) {
                                MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                            }
                        }
                    }
                } else {
                    if (!sendNoMessages) {
                        MessageProcessor.processMessage("messages.join", 3, finalChunks, player);
                    }
                }
            }
        }

        if (luckPermsEvent) {
            if (!sendNoMessages) {
                MessageProcessor.processMessage("messages.target-view-distance-change", 3, finalChunks, player);
            }
        }
    }

    public static void calcVdReset(OfflinePlayer player) {
        PlayerUtility playerUtility = new PlayerUtility();

        File playerDataFile = playerUtility.getPlayerDataFile(player);
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

        if (player.isOnline()) {
            dataHandler.setChunks(32);
            dataHandler.setChunksOthers(0);
            dataHandler.setPingMode(false);
            dataHandler.setChunksPing(0);

            PlayerUtility.setPlayerDataHandler(player, dataHandler);
        } else {
            cfg.set("chunks", 32);
            cfg.set("chunksOthers", 0);
            cfg.set("pingMode", false);
            cfg.set("chunksPing", 0);

            try {
                cfg.save(playerDataFile);
            } catch (Exception ex) {
                plugin.getLogger().severe("An exception occurred when resetting view distance data for " + player.getName() + ": " + ex.getMessage());
            }
        }

        if (player.isOnline()) {
            PlayerViewDistanceController.playerAfkMap.remove(player.getUniqueId());
            VdCalculator.calcVdSet(player.getPlayer(), true, false);
        }
    }


    public static int calcVdGet(OfflinePlayer player) {
        // Get max distance from LuckPerms
        int luckpermsDistance = JoinLeaveEvent.getLuckpermsDistance(player);
        luckpermsDistance = ClampAmountUtility.clampChunkValue(luckpermsDistance);
        PlayerDataHandler playerDataHandler;

        if (!player.isOnline()) {
            // Get an instance of the player data handler for the specific player
            PlayerUtility playerUtility = new PlayerUtility();
            File playerDataFile = playerUtility.getPlayerDataFile(player);

            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);

            if (playerDataFile.exists()) {
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
                dataHandler.setChunksOthers(cfg.getInt("chunksOthers"));
                dataHandler.setPingMode(cfg.getBoolean("pingMode"));
                dataHandler.setChunksPing(cfg.getInt("chunksPing"));
            }

            playerDataHandler = dataHandler;
        } else {
            playerDataHandler = PlayerUtility.getPlayerDataHandler(player);
        }

        int finalChunks = Math.min(playerDataHandler.getChunks(), luckpermsDistance);

        if (playerDataHandler.getChunksOthers() != 0 && playerDataHandler.getChunksOthers() != -1) {
            finalChunks = PlayerUtility.getPlayerDataHandler(player).getChunksOthers();
        }

        return finalChunks;
    }
}

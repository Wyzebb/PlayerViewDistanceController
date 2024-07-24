package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.CheckPrefixesUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessageUtility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class JoinLeaveEvent implements Listener {

    private final PlayerViewDistanceController plugin;

    public JoinLeaveEvent(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    String msg;

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        int amount = plugin.getConfig().getInt("default-distance");
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        PlayerUtility playerDataHandler = new PlayerUtility(plugin);
        File playerDataFile = playerDataHandler.getPlayerDataFile(e.getPlayer());

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");

            if (amount == plugin.getConfig().getInt("default-distance")) {
                // Default so redirect to prefixes
                int errorCheck = CheckPrefixesUtility.checkPrefixes(amount, e, playerDataFile, dataHandler, plugin);
                if (!(errorCheck == 1000)) {
                    amount = errorCheck;
                }
            }
        } else {
            int errorCheck = CheckPrefixesUtility.checkPrefixes(amount, e, playerDataFile, dataHandler, plugin);
            if (!(errorCheck == 1000)) {
                amount = errorCheck;
            }

        }

        amount = ClampAmountUtility.clampChunkValue(amount, plugin);

        dataHandler.setChunks(amount);
        e.getPlayer().setViewDistance(amount);

        if (plugin.getConfig().getBoolean("display-msg-on-join")) {
            if (amount == plugin.getConfig().getInt("max-distance") || amount == 32)  {
                if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                    e.getPlayer().sendMessage(ProcessConfigMessageUtility.getProcessedConfigMessage("join-msg", amount, plugin));
                }
            } else {
                e.getPlayer().sendMessage(ProcessConfigMessageUtility.getProcessedConfigMessage("join-msg", amount, plugin));
            }
        }
        PlayerUtility.setPlayerDataHandler(e.getPlayer(), dataHandler);
    }


    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(e.getPlayer());
        PlayerUtility playerDataHandler = new PlayerUtility(plugin);
        File playerDataFile = playerDataHandler.getPlayerDataFile(e.getPlayer());
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
        cfg.set("chunks", dataHandler.getChunks());

        try {
            cfg.save(playerDataFile);
        } catch (Exception event) {
            plugin.getLogger().warning("An error occurred saving the player view distance data!");
        }

        PlayerUtility.setPlayerDataHandler(e.getPlayer(), null);
    }
}

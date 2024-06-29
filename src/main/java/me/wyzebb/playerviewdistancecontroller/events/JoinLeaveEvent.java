package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;
import java.io.IOException;

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
        }

        amount = Math.min(32, amount);
        amount = Math.max(2, amount);

        amount = Math.min(plugin.getConfig().getInt("max-distance"), amount);
        amount = Math.max(plugin.getConfig().getInt("min-distance"), amount);

        if (!((playerDataFile.exists())) && !(plugin.getConfig().getBoolean("set-default-distance"))) {
            amount = Math.min(32, plugin.getConfig().getInt("max-distance"));
            amount = Math.max(2, plugin.getConfig().getInt("min-distance"));
        }

        dataHandler.setChunks(amount);
        e.getPlayer().setViewDistance(amount);

        if (plugin.getConfig().getBoolean("display-msg-on-join")) {
            if (amount == plugin.getConfig().getInt("max-distance") || amount == 32)  {
                if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                    msg = plugin.getConfig().getString("join-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    e.getPlayer().sendMessage(msg);
                }
            } else {
                msg = plugin.getConfig().getString("join-msg");
                msg = msg.replace("{chunks}", String.valueOf(amount));
                e.getPlayer().sendMessage(msg);
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
        } catch (IOException event) {
            plugin.getLogger().warning("An error occurred saving the player view distance data!");
        }

        PlayerUtility.setPlayerDataHandler(e.getPlayer(), null);
    }
}

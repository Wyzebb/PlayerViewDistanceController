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
            plugin.getLogger().info("1");

            if (amount == plugin.getConfig().getInt("default-distance")) {
                // Default so redirect to prefixes
                int pre = checkPrefixes(amount, e, playerDataFile, dataHandler);
                if (!(pre == 1000)) {
                    amount = pre;
                }
            }
        } else {
            int pre = checkPrefixes(amount, e, playerDataFile, dataHandler);
            if (!(pre == 1000)) {
                amount = pre;
            }

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


    private int checkPrefixes(Integer chunkAmount, PlayerJoinEvent event, File playerDataFile, PlayerDataHandler dataHandler) {
        Set<String> keys = plugin.getPrefixesConfig().getConfigurationSection("prefixes").getKeys(false);
        plugin.getLogger().info("2");
        if (!keys.isEmpty()) {
            plugin.getLogger().info("Keys: " + keys.toString());
            plugin.getLogger().info("3");

            for (String key : keys) {
                plugin.getLogger().info("4");
                plugin.getLogger().info("Key: " + key);
                plugin.getLogger().info("Value: " + plugin.getPrefixesConfig().getInt(("prefixes." + key)));


            }
        } else {
            plugin.getLogger().info("5");
            plugin.getLogger().info("No keys found or keys set is null");
            return 1000;
        }
        plugin.getLogger().info("6");
        for (String key : keys) {
            plugin.getLogger().info("7");
            if (Objects.equals(key, "dot")) {
                key = ".";
            }
            plugin.getLogger().info("8");
            if (event.getPlayer().getName().toLowerCase().startsWith(key.toLowerCase())) {
                // Name starts with prefix

                chunkAmount = plugin.getPrefixesConfig().getInt(("prefixes." + key));


                chunkAmount = Math.min(32, chunkAmount);
                chunkAmount = Math.max(2, chunkAmount);

                chunkAmount = Math.min(plugin.getConfig().getInt("max-distance"), chunkAmount);
                chunkAmount = Math.max(plugin.getConfig().getInt("min-distance"), chunkAmount);

                if (!((playerDataFile.exists())) && !(plugin.getConfig().getBoolean("set-default-distance"))) {
                    chunkAmount = Math.min(32, plugin.getConfig().getInt("max-distance"));
                    chunkAmount = Math.max(2, plugin.getConfig().getInt("min-distance"));
                }

                dataHandler.setChunks(chunkAmount);
                event.getPlayer().setViewDistance(chunkAmount);

                String msg = "SET YOUR VIEW DISTANCE TO {chunks} chunks because of your name's prefix";
                msg = msg.replace("{chunks}", String.valueOf(chunkAmount));
                event.getPlayer().sendMessage(msg);


            }
        }
        return chunkAmount;
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

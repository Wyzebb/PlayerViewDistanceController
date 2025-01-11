package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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

    private int getLuckpermsDistance(Player player) {
        try {
            Class.forName("net.luckperms.api.LuckPerms"); // Use reflection to check if LuckPerms is available
            plugin.getLogger().info("LuckPerms detected!");
            return LuckPermsDataHandler.getLuckpermsDistance(player, plugin);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but it extends the plugin's functionality!");
            return 32; // Return default distance if LuckPerms is not available
        } catch (Exception ex) {
            plugin.getLogger().warning("An unknown error occurred while accessing LuckPerms data: " + ex.getMessage());
            return 32; // Return default distance if LuckPerms is not available
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        int amount = plugin.getConfig().getInt("default-distance");

        // Get an instance of the player data handler for the specific player
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        PlayerUtility playerDataHandler = new PlayerUtility(plugin);
        File playerDataFile = playerDataHandler.getPlayerDataFile(e.getPlayer());

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");

            if (amount == plugin.getConfig().getInt("default-distance")) {
                // Default so check to prefixes
                int errorCheck = CheckPrefixesUtility.checkPrefixes(amount, e, dataHandler, plugin);
                if (!(errorCheck == 1000)) {
                    amount = errorCheck;
                }
            }
        } else {
            int errorCheck = CheckPrefixesUtility.checkPrefixes(amount, e, dataHandler, plugin);
            if (!(errorCheck == 1000)) {
                amount = errorCheck;
            }

        }

        boolean save = true;

        // Get max distances from LuckPerms
        int luckpermsDistance = getLuckpermsDistance(e.getPlayer());
        if (luckpermsDistance != ClampAmountUtility.getMaxPossible()) {
            if (luckpermsDistance > amount) {
                amount = luckpermsDistance;
                save = false;
            }
        }

        amount = ClampAmountUtility.clampChunkValue(amount, plugin);

        dataHandler.setChunks(amount);
        dataHandler.setSaveChunks(save);
        e.getPlayer().setViewDistance(amount);

        if (plugin.getConfig().getBoolean("display-msg-on-join")) {
            if (amount == plugin.getConfig().getInt("max-distance") || amount == ClampAmountUtility.getMaxPossible())  {
                if (plugin.getConfig().getBoolean("display-max-join-msg")) {
                    ProcessConfigMessagesUtility.processMessage("join-msg", e.getPlayer(), amount);
                }
            } else {
                ProcessConfigMessagesUtility.processMessage("join-msg", e.getPlayer(), amount);
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
        boolean save = dataHandler.getSaveChunks();

        try {
            if (save) {
                plugin.getLogger().info("Attempting to save player data for: " + e.getPlayer().getName());
                cfg.save(playerDataFile);
                plugin.getLogger().info("Player data saved successfully for: " + e.getPlayer().getName());
            }
        } catch (IOException ioException) {
            plugin.getLogger().severe("IOException occurred while saving player view distance data for " + e.getPlayer().getName() + ": " + ioException.getMessage());
            ioException.printStackTrace(); // Print the stack trace for detailed debugging
        } catch (Exception ex) {
            plugin.getLogger().severe("An unexpected error occurred saving the player view distance data for " + e.getPlayer().getName() + ": " + ex.getMessage());
            ex.printStackTrace(); // Print the stack trace for unexpected errors
        } finally {
            PlayerUtility.setPlayerDataHandler(e.getPlayer(), null);
        }
    }
}

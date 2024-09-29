package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinLeaveEvent implements Listener {

    private final PlayerViewDistanceController plugin;

    public JoinLeaveEvent(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    private int getLuckpermsDistance(PlayerJoinEvent e) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

        if (provider == null) {
            plugin.getLogger().warning("LuckPerms provider is not registered!");
            return 32; // Return default distance if LuckPerms is not available
        }

        LuckPerms api = provider.getProvider();
        User user = api.getPlayerAdapter(Player.class).getUser(e.getPlayer());

        // Regular expression to match permissions like pvdc.maxdistance.7
        Pattern pattern = Pattern.compile("viewdistance\\.maxdistance\\.(\\d+)");

        // Iterate over the player's permissions and find the smallest view distance
        int maxDistance = 32;
        for (var node : user.getNodes()) {
            String permission = node.getKey();

            Matcher matcher = pattern.matcher(permission);
            if (matcher.matches()) {
                // Extract the number from the permission
                int distance = Integer.parseInt(matcher.group(1));
                if (maxDistance == 32 || distance < maxDistance) {
                    maxDistance = distance;
                }
            }
        }

        return maxDistance;
    }



    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        int amount = plugin.getConfig().getInt("default-distance");
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        PlayerUtility playerDataHandler = new PlayerUtility(plugin);
        File playerDataFile = playerDataHandler.getPlayerDataFile(e.getPlayer());

        boolean save = true;

        int luckpermsDistance = getLuckpermsDistance(e);

        if (luckpermsDistance != 32) {
            amount = luckpermsDistance;
            save = false;
        }

        if (playerDataFile.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);
            amount = cfg.getInt("chunks");

            if (amount == plugin.getConfig().getInt("default-distance")) {
                // Default so redirect to prefixes
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
                cfg.save(playerDataFile);
            }
        } catch (Exception event) {
            plugin.getLogger().warning("An error occurred saving the player view distance data!");
        }

        PlayerUtility.setPlayerDataHandler(e.getPlayer(), null);
    }
}

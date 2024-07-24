package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Objects;
import java.util.Set;

public class CheckPrefixesUtility {

    public static int checkPrefixes(int amount, PlayerJoinEvent event, PlayerDataHandler dataHandler, PlayerViewDistanceController plugin) {
        Set<String> keys = plugin.getPrefixesConfig().getConfigurationSection("prefixes").getKeys(false);
        if (!keys.isEmpty()) {
            plugin.getLogger().info("Loaded prefixes: " + keys);

            for (String key : keys) {
                if (Objects.equals(key, "dot")) {
                    key = ".";
                }
                if (event.getPlayer().getName().toLowerCase().startsWith(key.toLowerCase())) {
                    // Name starts with prefix
                    amount = plugin.getPrefixesConfig().getInt(("prefixes." + key));
                    amount = ClampAmountUtility.clampChunkValue(amount, plugin);

                    dataHandler.setChunks(amount);
                    event.getPlayer().setViewDistance(amount);

                    String msg = "SET YOUR VIEW DISTANCE TO {chunks} chunks because of your name's prefix";
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    event.getPlayer().sendMessage(msg);
                }
            }
            return amount;
        } else {
            plugin.getLogger().info("No keys found or keys set is null");
            return 1000; // ERROR
        }
    }
}

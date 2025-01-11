package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class PlayerUtility {

    private static final Map<String, PlayerDataHandler> PlayerDataHandlerMap = new HashMap<>();

    public static PlayerDataHandler getPlayerDataHandler(Player p) {
        if (!(PlayerDataHandlerMap.containsKey(p.getUniqueId().toString()))) {
            PlayerDataHandler m = new PlayerDataHandler();
            PlayerDataHandlerMap.put(p.getUniqueId().toString(), m);
            return m;
        }
        return PlayerDataHandlerMap.get(p.getUniqueId().toString());
    }

    public static void setPlayerDataHandler(Player p, PlayerDataHandler dataHandler) {
        if (dataHandler == null) {
            PlayerDataHandlerMap.remove(p.getUniqueId().toString());
        } else {
            PlayerDataHandlerMap.put(p.getUniqueId().toString(), dataHandler);
        }
    }

    public File getPlayerDataFile(Player p) {
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, "players/" + p.getUniqueId() + ".yml");
    }
}

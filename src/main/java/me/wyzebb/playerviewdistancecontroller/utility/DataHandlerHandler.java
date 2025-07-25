package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DataHandlerHandler {

    private static final Map<String, PlayerDataHandler> PlayerDataHandlerMap = new ConcurrentHashMap<>();

    public static PlayerDataHandler getPlayerDataHandler(OfflinePlayer p) {
        File dataFolder = new File(plugin.getDataFolder(), "players");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        if (!(PlayerDataHandlerMap.containsKey(p.getUniqueId().toString()))) {
            PlayerDataHandler handler = new PlayerDataHandler();
            PlayerDataHandlerMap.put(p.getUniqueId().toString(), handler);
            return handler;
        }

        return PlayerDataHandlerMap.get(p.getUniqueId().toString());
    }

    public static void setPlayerDataHandler(OfflinePlayer p, PlayerDataHandler dataHandler) {
        if (dataHandler == null) {
            PlayerDataHandlerMap.remove(p.getUniqueId().toString());
        } else {
            PlayerDataHandlerMap.put(p.getUniqueId().toString(), dataHandler);
        }
    }

    public static File getPlayerDataFile(OfflinePlayer p) {
        File dataFolder = plugin.getDataFolder();
        return new File(dataFolder, "players/" + p.getUniqueId() + ".yml");
    }
}

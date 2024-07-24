package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;

public class ProcessConfigMessageUtility {

    public static String getProcessedConfigMessage(String configPath, int amount, PlayerViewDistanceController plugin) {
        String msg = plugin.getConfig().getString(configPath);
        msg = msg.replace("{chunks}", String.valueOf(amount));

        return msg;
    }
}

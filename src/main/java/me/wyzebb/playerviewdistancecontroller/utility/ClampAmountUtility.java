package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;

public class ClampAmountUtility {

    public static int clampChunkValue(int amount, PlayerViewDistanceController plugin) {
        amount = Math.min(32, amount);
        amount = Math.max(2, amount);

        amount = Math.min(plugin.getConfig().getInt("max-distance"), amount);
        amount = Math.max(plugin.getConfig().getInt("min-distance"), amount);

        return amount;
    }
}

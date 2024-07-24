package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;

public class ClampAmountUtility {

    private static final int MAX_POSSIBLE = 32;
    private static final int MIN_POSSIBLE = 2;

    public static int getMaxPossible() {
        return MAX_POSSIBLE;
    }

    public static int clampChunkValue(int amount, PlayerViewDistanceController plugin) {
        amount = Math.min(MAX_POSSIBLE, amount);
        amount = Math.max(MIN_POSSIBLE, amount);

        amount = Math.min(plugin.getConfig().getInt("max-distance"), amount);
        amount = Math.max(plugin.getConfig().getInt("min-distance"), amount);

        return amount;
    }
}

package me.wyzebb.playerviewdistancecontroller.utility;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ClampAmountUtility {

    private static final int MAX_POSSIBLE = 32;
    private static final int MIN_POSSIBLE = 2;

    public static int getMaxPossible() {
        return MAX_POSSIBLE;
    }

    public static int clampChunkValue(int amount) {
        amount = Math.min(MAX_POSSIBLE, amount);
        amount = Math.max(MIN_POSSIBLE, amount);

        amount = Math.min(plugin.config.getInt("max-distance"), amount);
        amount = Math.max(plugin.config.getInt("min-distance"), amount);

        return amount;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}

package me.wyzebb.playerviewdistancecontroller.utility;

import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.utility.GeyserFunc.checkBedrock;

public class GeyserCompat {
    public static boolean checkBedrockPlayer(UUID uuid) {
        try {
            Class.forName("org.geysermc.api.GeyserApiBase");
            return checkBedrock(uuid);
        } catch (Exception ex) {
            return false;
        }
    }
}

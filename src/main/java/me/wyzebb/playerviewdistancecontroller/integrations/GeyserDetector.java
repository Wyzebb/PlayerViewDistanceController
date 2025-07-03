package me.wyzebb.playerviewdistancecontroller.integrations;

import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.integrations.GeyserFunc.checkBedrock;

public class GeyserDetector {
    public static boolean checkBedrockPlayer(UUID uuid) {
        try {
            Class.forName("org.geysermc.api.GeyserApiBase");
            return checkBedrock(uuid);
        } catch (Exception ex) {
            return false;
        }
    }
}

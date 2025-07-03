package me.wyzebb.playerviewdistancecontroller.utility;

import org.geysermc.geyser.api.GeyserApi;

import java.util.UUID;

public class GeyserFunc {
    public static boolean checkBedrock(UUID uuid) {
        return GeyserApi.api().isBedrockPlayer(uuid);
    }
}

package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.entity.Player;

public class DataProcessorUtility {
    public static void processData(Player target, int amount) {
        target.setViewDistance(amount);
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        dataHandler.setChunks(amount);
        dataHandler.setSaveChunks(true);
        PlayerUtility.setPlayerDataHandler(target, dataHandler);
    }
}

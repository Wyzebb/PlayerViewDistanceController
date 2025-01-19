package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.entity.Player;

public class DataProcessorUtility {
    public static void processData(Player target, int amount) {
        target.setViewDistance(amount);
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        dataHandler.setChunks(amount);
        PlayerUtility.setPlayerDataHandler(target, dataHandler);
    }

    public static void processDataOthers(Player target, int amountOthers) {
        target.setViewDistance(amountOthers);
        PlayerDataHandler dataHandler = new PlayerDataHandler();
        dataHandler.setChunksOthers(amountOthers);
        PlayerUtility.setPlayerDataHandler(target, dataHandler);
    }
}

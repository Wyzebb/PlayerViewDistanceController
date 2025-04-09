package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import org.bukkit.entity.Player;

public class DataProcessorUtility {
    public static void processData(Player target, int amount) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunks(amount);
        target.setViewDistance(amount);
    }

    public static void processDataOthers(Player target, int amountOthers) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunksOthers(amountOthers);
    }

    public static void processPingMode(Player target, boolean pingMode) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setPingMode(pingMode);

        if (pingMode) {
            PingModeHandler.optimisePingOnce(target);
        }

        target.sendMessage("PING MODE: " + pingMode);
    }

    public static void processPingChunks(Player target, int pingChunks) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunksPing(pingChunks);
        target.sendMessage("PING CHUNKS: " + pingChunks);
        target.setViewDistance(pingChunks);
    }
}

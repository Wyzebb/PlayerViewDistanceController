package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DataProcessorUtility {
    public static void processData(OfflinePlayer target, int amount) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunks(amount);

        if (target.isOnline()) {
            ((Player) target).setViewDistance(amount);
        }
    }

    public static void processDataOthers(OfflinePlayer target, int amountOthers) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunksOthers(amountOthers);
    }

    public static void processPingMode(Player target, boolean pingMode) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setPingMode(pingMode);

        if (pingMode) {
            PingModeHandler.optimisePing(target);
        } else {
            VdCalculator.calcVdSet(target, true);
        }
    }

    public static void processPingChunks(Player target, int pingChunks) {
        PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
        dataHandler.setChunksPing(pingChunks);
        MessageProcessor.processMessage("messages.ping-optimised", 2, pingChunks, target);
        target.setViewDistance(pingChunks);
    }
}

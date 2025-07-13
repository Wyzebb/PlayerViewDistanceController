package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DataProcessorUtility {
    public static void processData(OfflinePlayer target, int amount) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);
        dataHandler.setChunks(amount);

        if (target.isOnline()) {
            ViewDistanceUtility.applyOptimalViewDistance((Player) target, amount);
        }
    }

    public static void processDataOthers(OfflinePlayer target, int amountOthers) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);
        dataHandler.setChunksOthers(amountOthers);
    }

    public static void processPingMode(Player target, boolean pingMode) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);
        dataHandler.setPingMode(pingMode);

        if (pingMode) {
            PingModeHandler.optimisePing(target);
        } else {
            VdCalculator.calcVdSet(target, true, false, false);
        }
    }

    public static void processPingChunks(Player target, int pingChunks) {
        ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(target, pingChunks);
        MessageProcessor.processMessage("messages.ping-optimised", 2, result.getViewDistance(), target);
    }
}

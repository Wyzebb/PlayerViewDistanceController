package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class DataProcessorUtility {
    public static void processData(OfflinePlayer target, int amount) {
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);
        dataHandler.setChunks(amount);

        if (target.isOnline()) {
            Player player = (Player) target;
            
            // Build context for data processing using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(player, amount);

            ViewDistanceUtility.applyOptimalViewDistance(context);
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
            // Build context for disabling ping mode using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createStandardContext(target);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }

    public static void processPingChunks(Player target, int pingChunks) {        
        // Build context for ping optimization using factory
        ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(target, pingChunks);

        ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
        MessageProcessor.processMessage("ping-optimised", MessageType.SUCCESS, result.getViewDistance(), target);
    }
}

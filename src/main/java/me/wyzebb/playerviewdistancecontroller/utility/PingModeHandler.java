package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingModeHandler {

    public static void optimisePing() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (PlayerUtility.getPlayerDataHandler(player).isPingMode()) {
                int ping = player.getPing();

                if (ping > 20) {
                    int luckpermsMax = 32;
                    int amount = PlayerUtility.getPlayerDataHandler(player).getChunks() - 2;

                    if (LuckPermsDetector.detectLuckPerms()) {
                        luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance(player);
                    }

                    if (luckpermsMax >= amount || player.hasPermission("pvdc.bypass-maxdistance")) {
                        MessageProcessor.processMessage("messages.ping-optimised", 2, amount, player);

                        DataProcessorUtility.processPingChunks(player, amount);
                    } else {
                        MessageProcessor.processMessage("messages.chunks-too-high", 1, luckpermsMax, player);
                    }
                }
            }
        }
    }

    public static void optimisePingOnce(Player player) {
        int ping = player.getPing();

        if (ping > 20) {
            int luckpermsMax = 32;
            int amount = PlayerUtility.getPlayerDataHandler(player).getChunks() - 2;

            if (LuckPermsDetector.detectLuckPerms()) {
                luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance(player);
            }

            if (luckpermsMax >= amount || player.hasPermission("pvdc.bypass-maxdistance")) {
                MessageProcessor.processMessage("messages.ping-optimised", 2, amount, player);

                DataProcessorUtility.processPingChunks(player, amount);
            } else {
                MessageProcessor.processMessage("messages.chunks-too-high", 1, luckpermsMax, player);
            }
        }
    }
}

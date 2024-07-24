package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ProcessColorCodesUtility {
    public static void processMessage(String configPath, CommandSender commandSender) {
        PlayerViewDistanceController plugin = PlayerViewDistanceController.getPlugin(PlayerViewDistanceController.class);
        String msg = plugin.getConfig().getString(configPath);

        if (!(commandSender instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(msg);
        }
    }

    public static void processMessage(String configPath, CommandSender commandSender, int amount) {
        PlayerViewDistanceController plugin = PlayerViewDistanceController.getPlugin(PlayerViewDistanceController.class);
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg, amount);

        if (!(commandSender instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(msg);
        }
    }

    public static void processMessage(String configPath, CommandSender commandSender, int amount, Player target, CommandSender toSendTo) {
        PlayerViewDistanceController plugin = PlayerViewDistanceController.getPlugin(PlayerViewDistanceController.class);
        String msg = plugin.getConfig().getString(configPath);

        msg = getProcessedConfigMessage(msg, amount, target);

        if (!(toSendTo instanceof Player)) {
            msg = msg.replaceAll("§.", "");
            plugin.getLogger().info(msg);
        }

        if (!(toSendTo instanceof ConsoleCommandSender)) {
            toSendTo.sendMessage(msg);
        }
    }

    public static String getProcessedConfigMessage(String msg, int amount) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        return msg;
    }

    public static String getProcessedConfigMessage(String msg, int amount, Player target) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        msg = msg.replace("{target-player}", target.getName());
        return msg;
    }
}

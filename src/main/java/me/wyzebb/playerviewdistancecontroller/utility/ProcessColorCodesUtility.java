package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class ProcessColorCodesUtility {
    public static void processColorMessage(String configPath, CommandSender commandSender, PlayerViewDistanceController plugin) {
        String msg = plugin.getConfig().getString(configPath);

        if (!(commandSender instanceof Player)) {
            msg = msg.replaceAll("§.", "");
        }

        if (!(commandSender instanceof ConsoleCommandSender)) {
            commandSender.sendMessage(msg);
        } else {
            plugin.getLogger().warning(msg);
        }
    }
}

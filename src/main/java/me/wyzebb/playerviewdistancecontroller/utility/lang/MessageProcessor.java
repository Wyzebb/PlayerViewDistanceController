package me.wyzebb.playerviewdistancecontroller.utility.lang;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class MessageProcessor {
    public static void processMessage(String langPath, int status, Player target, int amount, CommandSender sendTo) {
        String colour = plugin.getConfig().getString("colour");
        String errorColour = plugin.getConfig().getString("error-colour");
        String successColour = plugin.getConfig().getString("success-colour");

        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg;
            if (status == 1) {
                msg = errorColour;
            } else if (status == 2) {
                msg = successColour;
            } else {
                msg = colour;
            }

            msg = msg + langConfig.getString(langPath, "Message key not found. Please report to the developer!");
            sendTo.sendMessage(processPlaceholders(msg, target, amount));

        } else {
            String msg = langConfig.getString(langPath, "Message key not found. Please report to the developer!");

            if (status == 1) {
                plugin.getLogger().warning(processPlaceholders(msg, target, amount));
            } else {
                plugin.getLogger().info(processPlaceholders(msg, target, amount));
            }
        }
    }



    private static String processPlaceholders(String msg, Player target, int amount) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        msg = msg.replace("{target-player}", target.getName());

        return msg;
    }
}

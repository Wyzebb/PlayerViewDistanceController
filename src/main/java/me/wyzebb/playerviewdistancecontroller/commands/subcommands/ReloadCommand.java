package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.command.CommandSender;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "commands.reload";
    }

    @Override
    public String getSyntax() {
        return "/pvdc reload";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("pvdc.reload")) {
            plugin.getConfig().options().copyDefaults(true);
            plugin.reloadConfig();
            MessageProcessor.processMessage("messages.reload-config", 2, null, 0, commandSender);
        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, null, 0, commandSender);
        }
    }
}
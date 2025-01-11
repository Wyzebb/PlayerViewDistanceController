package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.command.CommandSender;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ReloadCommand extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload this plugin's config.yml";
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
            ProcessConfigMessagesUtility.processMessage("reload-config-msg", commandSender);
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
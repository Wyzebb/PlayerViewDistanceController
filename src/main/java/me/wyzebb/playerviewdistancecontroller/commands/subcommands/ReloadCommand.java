package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ReloadCommand extends SubCommand {

    private final LanguageManager languageManager;

    public ReloadCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.reload");
    }

    @Override
    public String getSyntax() {
        return "/pvdc reload";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("pvdc.reload") || commandSender instanceof ConsoleCommandSender) {
            plugin.getConfig().options().copyDefaults(true);
            plugin.reloadConfig();
            MessageProcessor.processMessage("messages.reload-config", 2, 0, commandSender);
        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
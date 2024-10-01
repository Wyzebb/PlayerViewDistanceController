package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.command.CommandSender;


public class HelpCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public HelpCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Displays the plugin's help message";
    }

    @Override
    public String getSyntax() {
        return "/vd help";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        SendHelpMsgUtility.sendHelpMessage(commandSender, plugin);
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.command.CommandSender;


public class HelpCommand extends SubCommand {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "commands.help";
    }

    @Override
    public String getSyntax() {
        return "/pvdc help";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        SendHelpMsgUtility.sendHelpMessage(commandSender);
    }
}
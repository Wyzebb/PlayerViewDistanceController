package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import org.bukkit.command.CommandSender;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;


public class HelpCommand extends SubCommand {

    private final LanguageManager languageManager;

    public HelpCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.help");
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
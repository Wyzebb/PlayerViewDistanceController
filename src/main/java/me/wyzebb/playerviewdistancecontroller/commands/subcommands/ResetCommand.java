package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ResetCommand extends SubCommand {

    private final LanguageManager languageManager;

    public ResetCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.reset");
    }

    @Override
    public String getSyntax() {
        return "/pvdc reset [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    resetSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("messages.not-player", 1, 0, commandSender);
                }

            } else {
                String targetName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    resetSelf(commandSender);
                } else {
                    if (commandSender.hasPermission("pvdc.reset-others") || commandSender instanceof ConsoleCommandSender) {
                        VdCalculator.calcVdReset(target);
                        MessageProcessor.processMessage("messages.reset", 2, target, 0, commandSender);
                    } else {
                        MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
                    }
                }
            }
        }
    }

    private void resetSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.reset-self")) {
            VdCalculator.calcVdReset((Player) commandSender);
            MessageProcessor.processMessage("messages.self-reset", 2, 0, commandSender);

        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
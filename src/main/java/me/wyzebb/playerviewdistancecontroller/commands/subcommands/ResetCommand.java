package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerDataManager;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        return languageManager.getLanguageFile().getString("reset-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc reset [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    resetSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("not-player", MessageType.ERROR, 0, commandSender);
                }

            } else {
                String targetName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    resetSelf(commandSender);
                } else {
                    if (commandSender.hasPermission("pvdc.reset-others") || commandSender instanceof ConsoleCommandSender) {
                        PlayerDataManager.resetPlayerData(target);
                        MessageProcessor.processMessage("reset", MessageType.SUCCESS, target, 0, commandSender);
                    } else {
                        MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
                    }
                }
            }
        }
    }

    private void resetSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.reset-self")) {
            PlayerDataManager.resetPlayerData((Player) commandSender);
            MessageProcessor.processMessage("self-reset", MessageType.SUCCESS, 0, commandSender);

        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
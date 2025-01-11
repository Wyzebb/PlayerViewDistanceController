package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetCommand extends SubCommand {

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Resets a player's saved max view distance so that just Luckperms permissions are used";
    }

    @Override
    public String getSyntax() {
        return "/pvdc reset [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    resetSelf(commandSender);
                } else {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

            } else {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessConfigMessagesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    resetSelf(commandSender);

                } else {
                    if (commandSender.hasPermission("pvdc.reset-others")) {
                        ProcessConfigMessagesUtility.processMessage("reset-msg", target, commandSender);
                    } else {
                        ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
                    }
                }
            }
        }
    }

    private void resetSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.reset-self")) {
            ProcessConfigMessagesUtility.processMessage("self-reset-msg", commandSender);
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
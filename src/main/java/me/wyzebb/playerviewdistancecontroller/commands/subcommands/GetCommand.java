package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GetCommand extends SubCommand {

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "Displays a player's max view distance";
    }

    @Override
    public String getSyntax() {
        return "/pvdc get [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    sendToSelf(commandSender);
                } else {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

            } else {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessConfigMessagesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    sendToSelf(commandSender);

                } else {
                    if (commandSender.hasPermission("pvdc.get-others")) {
                        ProcessConfigMessagesUtility.processMessage("view-distance-get-msg", target.getViewDistance(), target, commandSender);
                    } else {
                        ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
                    }
                }
            }
        }
    }

    private void sendToSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.get-self")) {
            Player player = (Player) commandSender;
            ProcessConfigMessagesUtility.processMessage("self-view-distance-get-msg", commandSender, player.getViewDistance());
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
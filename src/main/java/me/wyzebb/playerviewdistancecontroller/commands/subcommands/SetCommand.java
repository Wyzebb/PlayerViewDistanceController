package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand extends SubCommand {

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set your own max view distance or the max view distance of another player";
    }

    @Override
    public String getSyntax() {
        return "/pvdc set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

            } else {
                String targetName = args[2];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessConfigMessagesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    setSelf(commandSender, amount);

                } else {
                    if (commandSender.hasPermission("pvdc.others")) {
                        ProcessConfigMessagesUtility.processMessage("sender-view-distance-change-msg", amount, target, commandSender);
                        ProcessConfigMessagesUtility.processMessage("target-view-distance-change-msg", amount, target, target);
                        DataProcessorUtility.processData(target, amount);
                    } else {
                        ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
                    }
                }
            }
        }
    }

    private void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance((Player) commandSender);
            if (luckpermsMax > amount || commandSender.hasPermission("pvdc.bypass")) {
                ProcessConfigMessagesUtility.processMessage("self-view-distance-change-msg", commandSender, amount);
                DataProcessorUtility.processData((Player) commandSender, amount);
            } else {
                ProcessConfigMessagesUtility.processMessage("chunks-too-high", commandSender, luckpermsMax);
            }

        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
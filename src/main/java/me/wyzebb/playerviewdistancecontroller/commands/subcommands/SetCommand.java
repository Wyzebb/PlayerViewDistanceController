package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
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
            MessageProcessor.processMessage("incorrect-args", 1, null, 0, commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                MessageProcessor.processMessage("incorrect-args", 1, null, 0, commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    MessageProcessor.processMessage("incorrect-args", 1, null, 0, commandSender);
                }

            } else {
                String targetName = args[2];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    MessageProcessor.processMessage("player-offline-msg", 1, null, 0, commandSender);

                } else if (commandSender == target) {
                    setSelf(commandSender, amount);

                } else {
                    if (commandSender.hasPermission("pvdc.set-others")) {
                        MessageProcessor.processMessage("sender-view-distance-change-msg", 2, target, amount, commandSender);
                        MessageProcessor.processMessage("target-view-distance-change-msg", 2, target, amount, target);
                        DataProcessorUtility.processDataOthers(target, amount);
                    } else {
                        MessageProcessor.processMessage("no-permission", 1, null, 0, commandSender);
                    }
                }
            }
        }
    }

    private void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance((Player) commandSender);
            if (luckpermsMax >= amount || commandSender.hasPermission("pvdc.bypass-maxdistance")) {
                MessageProcessor.processMessage("self-view-distance-change-msg", 2, null, amount, commandSender);
                DataProcessorUtility.processData((Player) commandSender, amount);
            } else {
                MessageProcessor.processMessage("chunks-too-high", 1, null, luckpermsMax, commandSender);
            }

        } else {
            MessageProcessor.processMessage("no-permission", 1, null, 0, commandSender);
        }
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SetOnlineCommand extends SubCommand {

    @Override
    public String getName() {
        return "setonline";
    }

    @Override
    public String getDescription() {
        return "Sets the max view distance of all online players";
    }

    @Override
    public String getSyntax() {
        return "/pvdc setonline <chunks>";
    }

    @Override
    public void performCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (commandSender.hasPermission("pvdc.setonline")) {

            if (args.length != 2) {
                ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
            } else {
                int amount = ClampAmountUtility.getMaxPossible();

                try {
                    amount = Integer.parseInt(args[1]);
                    amount = ClampAmountUtility.clampChunkValue(amount);
                } catch (Exception e) {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

                try {
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        ProcessConfigMessagesUtility.processMessage("all-online-change-msg", p, amount);

                        DataProcessorUtility.processDataOthers(p, amount);
                    }
                } catch (Exception e) {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

                if (commandSender instanceof ConsoleCommandSender) {
                    ProcessConfigMessagesUtility.processMessage("all-online-change-msg", commandSender, amount);
                }
            }
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
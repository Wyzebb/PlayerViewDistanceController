package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessColorCodesUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetOnlineCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public SetOnlineCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    @Override
    public void performCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length != 2) {
            ProcessColorCodesUtility.processMessage("incorrect-args", commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount, plugin);
            } catch (Exception e) {
                ProcessColorCodesUtility.processMessage("incorrect-args", commandSender);
            }

            try {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    ProcessColorCodesUtility.processMessage("all-online-change-msg", p, amount);

                    DataProcessorUtility.processData(p, amount);
                }
            } catch (Exception e) {
                ProcessColorCodesUtility.processMessage("incorrect-args", commandSender);
            }

            if (commandSender instanceof ConsoleCommandSender) {
                ProcessColorCodesUtility.processMessage("all-online-change-msg", commandSender, amount);
            }
        }
    }

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
        return "/vd setonline <chunks>";
    }
}
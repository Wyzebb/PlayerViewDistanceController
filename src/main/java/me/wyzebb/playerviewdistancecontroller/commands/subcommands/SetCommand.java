package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public SetCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

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
        return "/vd set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {

        if (args.length < 2 || args.length > 3) {
            ProcessColorCodesUtility.processMessage("incorrect-args", commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount, plugin);
            } catch (Exception e) {
                ProcessColorCodesUtility.processMessage("incorrect-args", commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player player) {
                    ProcessColorCodesUtility.processMessage("self-view-distance-change-msg", commandSender, amount);
                    DataProcessorUtility.processData(player, amount);
                } else {
                    plugin.getLogger().info(plugin.getConfig().getString("incorrect-args"));
                }

            } else {
                String targetName = args[2];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessColorCodesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    ProcessColorCodesUtility.processMessage("self-view-distance-change-msg", commandSender, amount);
                    DataProcessorUtility.processData(target, amount);

                } else {
                    ProcessColorCodesUtility.processMessage("sender-view-distance-change-msg", commandSender, amount, target, commandSender);

                    ProcessColorCodesUtility.processMessage("target-view-distance-change-msg", target, amount, target, target);

                    DataProcessorUtility.processData(target, amount);
                }
            }
        }

    }
}
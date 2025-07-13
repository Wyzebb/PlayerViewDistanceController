package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SetOnlineCommand extends SubCommand {

    private final LanguageManager languageManager;

    public SetOnlineCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "setonline";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.setonline");
    }

    @Override
    public String getSyntax() {
        return "/pvdc setonline <chunks>";
    }

    @Override
    public void performCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (commandSender.hasPermission("pvdc.setonline") || commandSender instanceof ConsoleCommandSender) {

            if (args.length != 2) {
                MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
            } else {
                int amount = ClampAmountUtility.getMaxPossible();

                try {
                    amount = Integer.parseInt(args[1]);
                    amount = ClampAmountUtility.clampChunkValue(amount);
                } catch (Exception e) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                try {
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        MessageProcessor.processMessage("messages.all-online-change", 2, amount, p);

                        DataProcessorUtility.processDataOthers(p, amount);
                        ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(p, amount);
                        int appliedAmount = result.getViewDistance();
                    }
                } catch (Exception e) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                if (commandSender instanceof ConsoleCommandSender) {
                    MessageProcessor.processMessage("messages.all-online-change", 2, amount, commandSender);
                }
            }
        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
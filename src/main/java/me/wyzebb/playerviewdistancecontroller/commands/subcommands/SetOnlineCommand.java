package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
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
        return languageManager.getLanguageFile().getString("setonline-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc setonline <chunks>";
    }

    @Override
    public void performCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (commandSender.hasPermission("pvdc.setonline") || commandSender instanceof ConsoleCommandSender) {

            if (args.length != 2) {
                MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
            } else {
                int amount = ClampAmountUtility.getMaxPossible();

                try {
                    amount = Integer.parseInt(args[1]);
                    amount = ClampAmountUtility.clampChunkValue(amount);
                } catch (Exception e) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                try {
                    for (Player p : plugin.getServer().getOnlinePlayers()) {
                        MessageProcessor.processMessage("all-online-change", MessageType.SUCCESS, amount, p);

                        DataProcessorUtility.processDataOthers(p, amount);
                        
                        // Build context for command execution using factory
                        ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(p, amount);

                        ViewDistanceUtility.applyOptimalViewDistance(context);
                    }
                } catch (Exception e) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                if (commandSender instanceof ConsoleCommandSender) {
                    MessageProcessor.processMessage("all-online-change", MessageType.SUCCESS, amount, commandSender);
                }
            }
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
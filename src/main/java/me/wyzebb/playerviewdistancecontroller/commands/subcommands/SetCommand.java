package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import me.wyzebb.playerviewdistancecontroller.integrations.IntegrationManager;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SetCommand extends SubCommand {

    private final LanguageManager languageManager;

    public SetCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("set-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    MessageProcessor.processMessage("not-player", MessageType.ERROR, 0, commandSender);
                }

            } else {
                if (!ClampAmountUtility.isNumeric(args[1])) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

                if (commandSender == target) {
                    setSelf(commandSender, amount);
                } else {
                    setOthers(commandSender, target, amount);
                }
            }
        }
    }

    public static void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = IntegrationManager.getLuckpermsDistance((Player) commandSender);

            if (luckpermsMax >= amount || commandSender.hasPermission("pvdc.bypass-maxdistance")) {
                ViewDistanceCalculationContext context = ViewDistanceContextFactory.createSelfCommandContext((Player) commandSender, amount);
                ViewDistanceUtility.applyOptimalViewDistance(context);
            } else {
                MessageProcessor.processMessage("chunks-too-high", MessageType.ERROR, luckpermsMax, commandSender);
            }

        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }

    public static void setOthers(CommandSender commandSender, OfflinePlayer target, int amount) {
        if (commandSender.hasPermission("pvdc.set-others") || commandSender instanceof ConsoleCommandSender) {
            if (!target.isOnline()) {
                // If WI storage is enabled we cannot determine the world for an offline player
                if (plugin.getPluginConfig().isWorldIndependent()) {
                    MessageProcessor.processMessage("not-offline-cmd", MessageType.ERROR, 0, commandSender);
                    return;
                }
            }

            if (target.isOnline()) {
                Player player = (Player) target;
                // Build context for command execution using factory
                ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(player, amount);

                ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
                int appliedAmount = result.getViewDistance();

                MessageProcessor.processMessage("target-view-distance-change", MessageType.SUCCESS, target, appliedAmount, player);
            } else {
                // Use "DEFAULT" as we cannot determine the world of an offline player)
                Storage.setAdminChunks(target, "DEFAULT", amount);
                MessageProcessor.processMessage("sender-view-distance-change", MessageType.SUCCESS, target, amount, commandSender);
            }
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
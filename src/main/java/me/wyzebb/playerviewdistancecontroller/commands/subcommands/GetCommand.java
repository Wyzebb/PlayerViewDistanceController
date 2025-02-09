package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class GetCommand extends SubCommand {

    private final LanguageManager languageManager;

    public GetCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.get");
    }

    @Override
    public String getSyntax() {
        return "/pvdc get [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    sendToSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

            } else {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, commandSender);

                } else if (commandSender == target) {
                    sendToSelf(commandSender);

                } else {
                    if (commandSender.hasPermission("pvdc.get-others") || commandSender instanceof ConsoleCommandSender) {
                        MessageProcessor.processMessage("messages.view-distance-get", 3, target, VdCalculator.calcVdGet(target), commandSender);
                    } else {
                        MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
                    }
                }
            }
        }
    }

    private void sendToSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.get-self")) {
            Player player = (Player) commandSender;

            int amount = VdCalculator.calcVdGet(player);

            MessageProcessor.processMessage("messages.self-view-distance-get", 3, player, amount, commandSender);
        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
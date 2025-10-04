package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.PlayerDataManager;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
        return languageManager.getLanguageFile().getString("get-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc get [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    sendToSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

            } else {
                String targetName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    sendToSelf(commandSender);
                } else {
                    if (commandSender.hasPermission("pvdc.get-others") || commandSender instanceof ConsoleCommandSender) {
                        MessageProcessor.processMessage("view-distance-get", MessageType.INFO, target, PlayerDataManager.getCurrentViewDistance(target), commandSender);
                    } else {
                        MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
                    }
                }
            }
        }
    }

    private void sendToSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.get-self")) {
            Player player = (Player) commandSender;

            int amount = PlayerDataManager.getCurrentViewDistance(player);

            MessageProcessor.processMessage("self-view-distance-get", MessageType.INFO, player, amount, commandSender);
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
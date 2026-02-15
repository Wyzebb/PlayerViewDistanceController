package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.Database;
import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.models.WorldDataRow;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerDataManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DatabaseCommand extends SubCommand {

    private final LanguageManager languageManager;

    public DatabaseCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "db";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("db-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc db [player]";
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
                    if (commandSender.hasPermission("pvdc.db-view") || commandSender instanceof ConsoleCommandSender) {
                        final List<WorldDataRow> rows = Storage.getRows((Player) commandSender);

                        MessageProcessor.processMessage("db-header", MessageType.INFO, (Player) commandSender, 0, commandSender);

                        for (final WorldDataRow row : rows) {
                            commandSender.sendMessage(row.toString());
                        }
                    } else {
                        MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
                    }
                }
            }
        }
    }

    private void sendToSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.db-view")) {
            Player player = (Player) commandSender;
            final List<WorldDataRow> rows = Storage.getRows(player);

            MessageProcessor.processMessage("db-header", MessageType.INFO, player, 0, commandSender);

            for (final WorldDataRow row : rows) {
                commandSender.sendMessage(row.toString());
            }
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
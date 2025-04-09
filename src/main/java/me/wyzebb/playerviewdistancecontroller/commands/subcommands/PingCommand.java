package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class PingCommand extends SubCommand {

    private final LanguageManager languageManager;

    public PingCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.ping");
    }

    @Override
    public String getSyntax() {
        return "/pvdc ping [player] [on/off]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    setSelfPingMode(commandSender, !(PlayerUtility.getPlayerDataHandler((Player) commandSender).isPingMode()));
                } else {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

            } else if (args.length == 2) {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, commandSender);

                } else if (commandSender == target) {
                    setSelfPingMode(commandSender, !(PlayerUtility.getPlayerDataHandler((Player) commandSender).isPingMode()));
                } else {
                    setPingMode(commandSender, !(PlayerUtility.getPlayerDataHandler(target).isPingMode()), target);
                }

            } else {
                final String[] OPTIONS = {"on", "off"};

                if (!(Arrays.asList(OPTIONS).contains(args[2]))) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                boolean mode = Objects.equals(args[2], "on");

                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, commandSender);

                } else if (commandSender == target) {
                    setSelfPingMode(commandSender, mode);
                } else {
                    setPingMode(commandSender, mode, target);
                }
            }
        }
    }

    public static void setSelfPingMode(CommandSender commandSender, boolean pingMode) {
        if (commandSender.hasPermission("pvdc.set-self-pingmode")) {
            MessageProcessor.processMessage("messages.pingmode-change-self", 2, pingMode, commandSender);

            DataProcessorUtility.processPingMode((Player) commandSender, pingMode);

        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }

    public static void setPingMode(CommandSender commandSender, boolean pingMode, Player target) {
        if (commandSender.hasPermission("pvdc.set-others-pingmode")) {
            MessageProcessor.processMessage("messages.pingmode-change", 2, pingMode, target);
            MessageProcessor.processMessage("messages.pingmode-change-others", 2, pingMode, commandSender);

            DataProcessorUtility.processPingMode(target, pingMode);

        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
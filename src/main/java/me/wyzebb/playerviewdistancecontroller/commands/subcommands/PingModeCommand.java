package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class PingModeCommand extends SubCommand {

    private final LanguageManager languageManager;

    public PingModeCommand() {
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
        return "/pvdc ping [on/off/info] [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player player) {
                    setSelfPingMode(commandSender, !(PlayerUtility.getPlayerDataHandler(player).isPingMode()));
                } else {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

            } else if (args.length == 2) {
                if (!(commandSender instanceof Player)) {
                    MessageProcessor.processMessage("messages.not-player", 1, commandSender);
                    return;
                }

                final String[] OPTIONS = {"on", "off", "info"};

                if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                if (Objects.equals(args[1], "info")) {
                    MessageProcessor.processMessage("messages.ping-info", 2, PlayerUtility.getPlayerDataHandler((Player) commandSender).isPingMode(), commandSender);
                    return;
                }

                boolean mode = Objects.equals(args[1], "on");

                setSelfPingMode(commandSender, mode);

            } else {
                String targetName = args[2];
                Player target = Bukkit.getPlayer(targetName);

                final String[] OPTIONS = {"on", "off", "info"};

                if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                if (Objects.equals(args[1], "info")) {
                    MessageProcessor.processMessage("messages.ping-info", 2, PlayerUtility.getPlayerDataHandler((Player) commandSender).isPingMode(), commandSender);
                    return;
                }

                boolean mode = Objects.equals(args[1], "on");

                if (target == null) {
                    MessageProcessor.processMessage("messages.not-offline-cmd", 1, 0, commandSender);
                } else if (commandSender == target) {
                    setSelfPingMode(commandSender, mode);
                } else {
                    setPingMode(commandSender, mode, target);
                }
            }
        }
    }

    public static void setSelfPingMode(CommandSender commandSender, boolean pingMode) {
        if (plugin.getPingOptimiserConfig().getBoolean("enabled")) {
            if (commandSender.hasPermission("pvdc.ping-mode-set-self")) {
                MessageProcessor.processMessage("messages.ping-mode-change-self", 2, pingMode, commandSender);

                DataProcessorUtility.processPingMode((Player) commandSender, pingMode);

            } else {
                MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
            }
        } else {
            MessageProcessor.processMessage("messages.ping-mode-disabled", 1, commandSender);
        }
    }

    public static void setPingMode(CommandSender commandSender, boolean pingMode, Player target) {
        if (plugin.getPingOptimiserConfig().getBoolean("enabled")) {
            if (commandSender.hasPermission("pvdc.ping-mode-set-others")) {
                MessageProcessor.processMessage("messages.ping-mode-change", 2, pingMode, target);
                MessageProcessor.processMessage("messages.ping-mode-change-others", 2, pingMode, commandSender);

                DataProcessorUtility.processPingMode(target, pingMode);

            } else {
                MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
            }
        } else {
            MessageProcessor.processMessage("messages.ping-mode-disabled", 1, commandSender);
        }
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
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
        return languageManager.getLanguageFile().getString("ping-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc ping [on/off/info] [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 3) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player player) {
                    setSelfPingMode(commandSender, !(Storage.isPingMode(player)));
                } else {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

            } else if (args.length == 2) {
                if (!(commandSender instanceof Player)) {
                    MessageProcessor.processMessage("not-player", MessageType.ERROR, commandSender);
                    return;
                }

                final String[] OPTIONS = {"on", "off", "info"};

                if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                if (Objects.equals(args[1], "info")) {
                    MessageProcessor.processMessage("ping-info", MessageType.SUCCESS, Storage.isPingMode((Player) commandSender), commandSender);
                    return;
                }

                boolean mode = Objects.equals(args[1], "on");

                setSelfPingMode(commandSender, mode);

            } else {
                String targetName = args[2];
                Player target = Bukkit.getPlayer(targetName);

                final String[] OPTIONS = {"on", "off", "info"};

                if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                if (Objects.equals(args[1], "info")) {
                    MessageProcessor.processMessage("ping-info", MessageType.SUCCESS, Storage.isPingMode((Player) commandSender), commandSender);
                    return;
                }

                boolean mode = Objects.equals(args[1], "on");

                if (target == null) {
                    MessageProcessor.processMessage("not-offline-cmd", MessageType.ERROR, 0, commandSender);
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
                MessageProcessor.processMessage("ping-mode-change-self", MessageType.SUCCESS, pingMode, commandSender);

                Storage.setPingMode((Player) commandSender, pingMode);

            } else {
                MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
            }
        } else {
            MessageProcessor.processMessage("ping-mode-disabled", MessageType.ERROR, commandSender);
        }
    }

    public static void setPingMode(CommandSender commandSender, boolean pingMode, Player target) {
        if (plugin.getPingOptimiserConfig().getBoolean("enabled")) {
            if (commandSender.hasPermission("pvdc.ping-mode-set-others")) {
                MessageProcessor.processMessage("ping-mode-change", MessageType.SUCCESS, pingMode, target);
                MessageProcessor.processMessage("ping-mode-change-others", MessageType.SUCCESS, pingMode, commandSender);

                Storage.setPingMode(target, pingMode);

            } else {
                MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
            }
        } else {
            MessageProcessor.processMessage("ping-mode-disabled", MessageType.ERROR, commandSender);
        }
    }
}
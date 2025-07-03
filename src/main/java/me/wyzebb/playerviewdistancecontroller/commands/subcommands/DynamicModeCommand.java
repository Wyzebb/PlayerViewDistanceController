package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.dynamicModeEnabled;
import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class DynamicModeCommand extends SubCommand {

    private final LanguageManager languageManager;

    public DynamicModeCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "dynamic";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.dynamic");
    }

    @Override
    public String getSyntax() {
        return "/pvdc dynamic [on/off/info]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else if (args.length == 1) {
            if (commandSender.hasPermission("pvdc.dynamic-mode-set")) {
                setDynamicMode(!dynamicModeEnabled);
            } else {
                MessageProcessor.processMessage("messages.no-permission", 1, commandSender);
            }
        } else {
            if (commandSender.hasPermission("pvdc.dynamic-mode-set")) {
                final String[] OPTIONS = {"on", "off", "info"};

                if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                if (Objects.equals(args[1], "info")) {
                    MessageProcessor.processMessage("messages.dynamic-info", 2, dynamicModeEnabled, commandSender);
                    return;
                }

                boolean mode = Objects.equals(args[1], "on");

                setDynamicMode(mode);
            } else {
                MessageProcessor.processMessage("messages.no-permission", 1, commandSender);
            }
        }
    }

    public static void setDynamicMode(boolean mode) {
        dynamicModeEnabled = mode;

        if (mode) {
            plugin.startDynamicMode();
        } else {
            plugin.stopDynamicMode();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            MessageProcessor.processMessage("messages.dynamic-mode-change", 2, mode, player);
        }
    }
}
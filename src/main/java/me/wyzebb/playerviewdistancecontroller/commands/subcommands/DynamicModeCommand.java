package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PingModeHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
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
        return "/pvdc dynamic [on/off]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else if (args.length == 1) {
            setDynamicMode(!dynamicModeEnabled);
        } else {
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
            player.sendMessage("dynamic is now " + mode);
            player.sendMessage("dynamic global is now " + dynamicModeEnabled);
        }
    }
}
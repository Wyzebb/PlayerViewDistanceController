package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PingModeHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

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
            setDynamicMode(!plugin.getDynamicModeConfig().getBoolean("enabled"));
        } else {
            final String[] OPTIONS = {"on", "off"};

            if (!(Arrays.asList(OPTIONS).contains(args[1]))) {
                MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
            }

            boolean mode = Objects.equals(args[1], "on");

            setDynamicMode(mode);
        }
    }

    public static void setDynamicMode(boolean mode) {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.getDynamicModeConfig().set("enabled", mode);

            if (mode) {
                plugin.startDynamicMode();
            } else {
                plugin.stopDynamicMode();
            }
            MessageProcessor.processMessage("messages.all-online-change", 2, 0, p);
        }
    }
}
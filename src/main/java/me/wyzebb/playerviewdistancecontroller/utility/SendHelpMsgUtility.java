package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SubCommand;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SendHelpMsgUtility {

    public static void sendHelpMessage(CommandSender commandSender, PlayerViewDistanceController plugin) {
        ArrayList<String> messageLines = getStrings(plugin);

        if (commandSender instanceof Player p) {
            for (String line : messageLines) {
                p.sendMessage(line);
            }
        } else if (commandSender instanceof BlockCommandSender b) {
            for (String line : messageLines) {
                b.sendMessage(line);
            }
        } else {
            for (String line : messageLines) {
                plugin.getLogger().info(line);
            }
        }
    }

    private static @NotNull ArrayList<String> getStrings(PlayerViewDistanceController plugin) {
        CommandManager cmdManager = new CommandManager(plugin);
        List<SubCommand> subcommands = cmdManager.getSubcommands();

        ArrayList<String> messageLines = new ArrayList<>();
        messageLines.add("§c--------------------------------");
        for (SubCommand subcommand : subcommands) {
            messageLines.add("§c§l" + subcommand.getSyntax() + " - §e" + subcommand.getDescription());
        }
        messageLines.add("");
        messageLines.add("§cFurther configuration options in config.yml");
        messageLines.add("§c--------------------------------");
        return messageLines;
    }
}

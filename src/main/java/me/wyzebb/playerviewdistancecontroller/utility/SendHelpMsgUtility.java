package me.wyzebb.playerviewdistancecontroller.utility;

import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SubCommand;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SendHelpMsgUtility {

    public static void sendHelpMessage(CommandSender commandSender) {
        ArrayList<String> lines = getContent();

        if (commandSender instanceof Player player) {
            for (String line : lines) {
                player.sendMessage(line);
            }
        } else if (commandSender instanceof BlockCommandSender sender) {
            for (String line : lines) {
                sender.sendMessage(line);
            }
        } else {
            for (String line : lines) {
                plugin.getLogger().info(line);
            }
        }
    }

    private static @NotNull ArrayList<String> getContent() {
        CommandManager cmdManager = new CommandManager();
        List<SubCommand> subcommands = cmdManager.getSubcommands();

        ArrayList<String> lines = new ArrayList<>();

        lines.add("§c§l---------- PVDC §e§lv" + plugin.getDescription().getVersion() + " §c§lCommand Help ----------");

        for (SubCommand subcommand : subcommands) {
            lines.add("§c§l" + subcommand.getSyntax() + " - §e" + subcommand.getDescription());
        }

        return lines;
    }
}

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
        ArrayList<String> messageLines = getStrings();

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

    private static @NotNull ArrayList<String> getStrings() {
        CommandManager cmdManager = new CommandManager();
        List<SubCommand> subcommands = cmdManager.getSubcommands();

        ArrayList<String> messageLines = new ArrayList<>();

        messageLines.add("");
        messageLines.add("§c§l----------------- PVDC Help -----------------");
        for (SubCommand subcommand : subcommands) {
            messageLines.add("§c§l" + subcommand.getSyntax() + " - §e" + subcommand.getDescription());
        }
        messageLines.add("§eMost options in config.yml     //     v" + plugin.getDescription().getVersion());
        messageLines.add("§c§l---------------------------------------------");
        return messageLines;
    }
}

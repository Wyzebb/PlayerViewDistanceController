package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.commands.subcommands.*;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager() {
        subcommands.add(new SetCommand());
        subcommands.add(new SetOnlineCommand());
        subcommands.add(new GetCommand());
        subcommands.add(new ResetCommand());
        subcommands.add(new InfoCommand());
        subcommands.add(new HelpCommand());
        subcommands.add(new PingModeCommand());
        subcommands.add(new DynamicModeCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0) {
            boolean found = false;
            for (int i = 0; i < getSubcommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    getSubcommands().get(i).performCommand(commandSender, args);
                    found = true;
                } else {
                    if (i == getSubcommands().size() - 1 && !found) {
                        if (ClampAmountUtility.isNumeric(args[0])) {
                            SetCommand.setSelf(commandSender, ClampAmountUtility.clampChunkValue(Integer.parseInt(args[0])));
                        } else {
                            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                        }
                    }
                }
            }
        } else {
            SendHelpMsgUtility.sendHelpMessage(commandSender);
        }
        return true;
    }

    public ArrayList<SubCommand> getSubcommands() {
        return subcommands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();

            for (int i = 0; i < getSubcommands().size(); i++) {
                suggestions.add(getSubcommands().get(i).getName());
            }

            suggestions.add("<chunks>");

            return suggestions;
        } else if (args.length == 2 && args[0].equals("get")) {
            return getAllPlayers(args, 1);
        } else if (args.length == 2 && args[0].equals("reset")) {
            return getAllPlayers(args, 1);
        } else if (args.length == 2 && args[0].equals("info")) {
            return getAllPlayers(args, 1);
        } else if (args.length == 2 && args[0].equals("setonline")) {
            return Collections.singletonList("<chunks>");

        } else if (args.length == 2 && args[0].equals("set")) {
            return Collections.singletonList("<chunks>");

        } else if (args.length == 3 && args[0].equals("set")) {
            return getAllPlayers(args, 2);
        } else if (args.length == 2 && args[0].equals("ping")) {
            String[] suggestions = {"on", "off", "info"};
            return List.of(suggestions);
        } else if (args.length == 3 && args[0].equals("ping")) {
            return getAllPlayers(args, 2);
        } else if (args.length == 2 && args[0].equals("dynamic")) {
            String[] suggestions = {"on", "off", "info"};
            return List.of(suggestions);
        }

        return new ArrayList<>() {};
    }

    private static @NotNull ArrayList<String> getAllPlayers(@NotNull String @NotNull [] args, int argIndex) {
        ArrayList<String> playerNames = new ArrayList<>();
        OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();

        if (args[argIndex].isEmpty()) {
            for (OfflinePlayer player : players) {
                playerNames.add(player.getName());
            }

        } else {
            String input = args[argIndex].toLowerCase();
            for (OfflinePlayer player : players) {
                String name = player.getName();
                if (name != null && name.toLowerCase().startsWith(input)) {
                    playerNames.add(name);
                }
            }
        }
        return playerNames;
    }
}

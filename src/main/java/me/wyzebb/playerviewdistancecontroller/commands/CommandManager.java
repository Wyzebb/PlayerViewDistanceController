package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.commands.subcommands.*;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

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
        subcommands.add(new DatabaseCommand());
        subcommands.add(new MenuCommand());
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
            final List<String> suggestions = getSubcommands().stream()
                    .map(SubCommand::getName)
                    .collect(Collectors.toCollection(ArrayList::new));

            if (!args[0].isEmpty()) {
                final String input = args[0].toLowerCase();
                final List<String> filteredSuggestions = new ArrayList<>();

                suggestions.forEach(suggestion -> {
                    if (suggestion.startsWith(input)) {
                        filteredSuggestions.add(suggestion);
                    }
                });

                try {
                    Integer.parseInt(args[0]);
                    filteredSuggestions.add("<chunks>");
                } catch (NumberFormatException ignored) {}

                return filteredSuggestions;
            } else {
                suggestions.add("<chunks>");
                return suggestions;
            }
        } else if (args.length == 2 && (args[0].equals("get") || args[0].equals("reset") || args[0].equals("info")
                || args[0].equals("db"))) {
            return getAllPlayers(args, 1);
        } else if (args.length == 2 && args[0].equals("setonline")) {
            return Collections.singletonList("<chunks>");
        } else if (args.length == 2 && args[0].equals("set")) {
            return Collections.singletonList("<chunks>");
        } else if (args.length == 3 && args[0].equals("set")) {
            return getAllPlayers(args, 2);
        } else if (args.length == 2 && args[0].equals("ping")) {
            return getPerfModeSuggestions(args);
        } else if (args.length == 3 && args[0].equals("ping")) {
            return getAllPlayers(args, 2);
        } else if (args.length == 2 && args[0].equals("dynamic")) {
            return getPerfModeSuggestions(args);
        }

        return new ArrayList<>();
    }

    private static List<String> getPerfModeSuggestions(String[] args) {
        final List<String> suggestions = Arrays.asList("on", "off", "info");

        if (!args[1].isEmpty()) {
            final String input = args[1].toLowerCase();
            final List<String> filteredSuggestions = new ArrayList<>();

            suggestions.forEach(suggestion -> {
                plugin.getLogger().info(suggestion);
                if (suggestion.startsWith(input)) {
                    filteredSuggestions.add(suggestion);
                }
            });

            return filteredSuggestions;
        } else return suggestions;
    }

    private static @NotNull ArrayList<String> getAllPlayers(@NotNull String @NotNull [] args, int argIndex) {
        final ArrayList<String> playerNames = new ArrayList<>();

        if (args[argIndex].isEmpty()) {
            if (plugin.getPluginConfig().isListOfflinePlayers()) {
                for (final OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                    playerNames.add(player.getName());
                }
            } else {
                for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
                    playerNames.add(player.getName());
                }
            }
        } else {
            final String input = args[argIndex].toLowerCase();

            if (plugin.getPluginConfig().isListOfflinePlayers()) {
                for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
                    final String name = player.getName();
                    if (name != null && name.toLowerCase().startsWith(input)) {
                        playerNames.add(name);
                    }
                }
            } else {
                Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                    if (player.getName().toLowerCase().startsWith(input)) {
                        playerNames.add(player.getName());
                    }
                });
            }
        }

        return playerNames;
    }
}

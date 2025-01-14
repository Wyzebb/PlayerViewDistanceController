package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.commands.subcommands.*;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(){
        subcommands.add(new SetCommand());
        subcommands.add(new SetOnlineCommand());
        subcommands.add(new GetCommand());
        subcommands.add(new ResetCommand());
        subcommands.add(new ReloadCommand());
        subcommands.add(new HelpCommand());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0){
            boolean found = false;
            for (int i = 0; i < getSubcommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    getSubcommands().get(i).performCommand(commandSender, args);
                    found = true;
                } else {
                    if (i == getSubcommands().size() - 1 && !found) {
                        MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                    }
                }
            }
        } else {
            SendHelpMsgUtility.sendHelpMessage(commandSender);
        }
        return true;
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            ArrayList<String> suggestions = new ArrayList<>();

            for (int i = 0; i < getSubcommands().size(); i++){
                suggestions.add(getSubcommands().get(i).getName());
            }

            return suggestions;
        } else if (args.length == 2 && args[0].equals("get")) {
            ArrayList<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().toArray().length];
            Bukkit.getServer().getOnlinePlayers().toArray(players);

            for (Player player : players) {
                playerNames.add(player.getName());
            }

            return playerNames;
        } else if (args.length == 3 && args[0].equals("set")) {
            ArrayList<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().toArray().length];
            Bukkit.getServer().getOnlinePlayers().toArray(players);

            for (Player player : players) {
                playerNames.add(player.getName());
            }

            return playerNames;
        }

        return new ArrayList<>() {};
    }
}

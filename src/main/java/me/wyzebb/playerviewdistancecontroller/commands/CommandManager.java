package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SetCommand;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SetOnlineCommand;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SubCommand;
import me.wyzebb.playerviewdistancecontroller.utility.SendHelpMsgUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    private final PlayerViewDistanceController plugin;

    public CommandManager(PlayerViewDistanceController plugin){
        this.plugin = plugin;
        subcommands.add(new SetCommand(plugin));
        subcommands.add(new SetOnlineCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (args.length > 0){
            for (int i = 0; i < getSubcommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                    getSubcommands().get(i).performCommand(commandSender, args);
                }
            }
        } else {
            SendHelpMsgUtility.sendHelpMessage(commandSender, plugin);
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
        } else if (args.length == 2) {
            return new ArrayList<>() {};
        } else if (args.length == 3 && args[0].equals("set")) {
            ArrayList<String> playerNames = new ArrayList<>();
            Player[] players = new Player[Bukkit.getServer().getOnlinePlayers().toArray().length];
            Bukkit.getServer().getOnlinePlayers().toArray(players);

            for (Player player : players) {
                playerNames.add(player.getName());
            }

            return playerNames;
        } else if (args.length == 3) {
            return new ArrayList<>() {};
        }

        return new ArrayList<>() {};
    }
}

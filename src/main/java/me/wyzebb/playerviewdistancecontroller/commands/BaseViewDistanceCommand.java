package me.wyzebb.playerviewdistancecontroller.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BaseViewDistanceCommand implements CommandExecutor {
    private final Map<String, SubCommand> commands = new HashMap<>();

    public void registerCommand(String cmd, SubCommand subCommand) {
        commands.put(cmd, subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(!commands.containsKey(args[0].toLowerCase())) {
            commandSender.sendMessage("That sub command doesn't exist!");
            return false;
        }

        commands.get(args[0]).onCommand(commandSender, command, s, args);
        return true;
    }
}

package me.wyzebb.playerviewdistancecontroller.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetSubCommand extends SubCommand {

    @Override
    void onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

    }

    public String getPermission() {
        return "PERMS";
    }
}
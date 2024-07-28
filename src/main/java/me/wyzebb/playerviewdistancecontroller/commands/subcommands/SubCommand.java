package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand{

    public abstract void performCommand(CommandSender commandSender, String[] args);

    public abstract String getName();

    public abstract String getDescription();

    public abstract String getSyntax();

}

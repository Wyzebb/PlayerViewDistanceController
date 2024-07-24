package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SetCommand;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SetOnlineCommand;
import me.wyzebb.playerviewdistancecontroller.commands.subcommands.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandManager implements CommandExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();

    public CommandManager(PlayerViewDistanceController plugin){
        subcommands.add(new SetCommand(plugin));
        subcommands.add(new SetOnlineCommand(plugin));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (commandSender instanceof Player p){
            if (args.length > 0){
                for (int i = 0; i < getSubcommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubcommands().get(i).getName())) {
                        getSubcommands().get(i).performCommand(p, args);
                    }
                }
            } else {
                sendSubcommands(p);
            }

        }


        return true;
    }

    private void sendSubcommands(Player p) {
        p.sendMessage("--------------------------------");
        for (int i = 0; i < getSubcommands().size(); i++){
            p.sendMessage(getSubcommands().get(i).getSyntax() + " - " + getSubcommands().get(i).getDescription());
        }
        p.sendMessage("--------------------------------");
    }

    public ArrayList<SubCommand> getSubcommands(){
        return subcommands;
    }

}

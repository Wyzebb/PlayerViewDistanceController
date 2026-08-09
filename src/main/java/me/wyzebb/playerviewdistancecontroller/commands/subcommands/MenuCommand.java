package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.menu.MenuHandlerUtility;
import me.wyzebb.playerviewdistancecontroller.menu.PlayerSelectionMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand extends SubCommand {
    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player player) {
            new PlayerSelectionMenu(MenuHandlerUtility.getPlayerMenuUtility(player)).open();
        } else {
            commandSender.sendMessage("This command can only be run by a player.");
        }
    }

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Opens PVDC menu";
    }

    @Override
    public String getSyntax() {
        return "/pvdc menu";
    }
}
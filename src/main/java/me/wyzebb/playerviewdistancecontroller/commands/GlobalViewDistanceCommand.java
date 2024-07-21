package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GlobalViewDistanceCommand implements CommandExecutor {

    private final PlayerViewDistanceController plugin;

    public GlobalViewDistanceCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    String msg;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length != 1) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
            return true;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[0]);

            amount = Math.min(32, amount);
            amount = Math.max(2, amount);

            amount = Math.min(plugin.getConfig().getInt("max-distance"), amount);
            amount = Math.max(plugin.getConfig().getInt("min-distance"), amount);

        } catch (Exception e) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
            return true;
        }

        try {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                msg = plugin.getConfig().getString("target-view-distance-change-msg");
                msg = msg.replace("{chunks}", String.valueOf(amount));
                p.sendMessage(msg);

                p.setViewDistance(amount);

                PlayerDataHandler dataHandler = new PlayerDataHandler();
                dataHandler.setChunks(amount);
                PlayerUtility.setPlayerDataHandler(p, dataHandler);
            }

        } catch (Exception e) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
            return true;
        }
        return true;
    }
}
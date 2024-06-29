package me.wyzebb.playerviewdistancecontroller.commands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ViewDistanceCommand implements CommandExecutor {

    private final PlayerViewDistanceController plugin;

    public ViewDistanceCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    String msg;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length < 1 || args.length > 2) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
            return true;
        }

        int amount = 0;

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
            if (args.length == 1) {
                if (sender instanceof Player player) {
                    msg = plugin.getConfig().getString("self-view-distance-change-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    sender.sendMessage(msg);
                    player.setViewDistance(amount);

                    PlayerDataHandler dataHandler = new PlayerDataHandler();
                    dataHandler.setChunks(amount);
                    PlayerUtility.setPlayerDataHandler(player, dataHandler);

                } else {
                    plugin.getLogger().info(plugin.getConfig().getString("change-consoleorcmd-distance-error"));
                }

            } else {
                String targetName = args[1];

                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    if (sender instanceof Player) {
                        sender.sendMessage(plugin.getConfig().getString("player-offline-msg"));
                    } else {
                        plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-player-offline-msg"));
                    }

                } else if (sender == (Player) target) {
                    msg = plugin.getConfig().getString("self-view-distance-change-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    sender.sendMessage(msg);
                    target.setViewDistance(amount);

                    PlayerDataHandler dataHandler = new PlayerDataHandler();
                    dataHandler.setChunks(amount);
                    PlayerUtility.setPlayerDataHandler(target, dataHandler);

                } else {
                    if (sender instanceof Player) {
                        String msg = plugin.getConfig().getString("sender-view-distance-change-msg");
                        msg = msg.replace("{target-player}", target.getName());
                        msg = msg.replace("{chunks}", String.valueOf(amount));
                        sender.sendMessage(msg);

                    } else {
                        String msg = plugin.getConfig().getString("consoleorcmdblock-sender-view-distance-change-msg");
                        msg = msg.replace("{target-player}", target.getName());
                        msg = msg.replace("{chunks}", String.valueOf(amount));
                        plugin.getLogger().info(msg);
                    }

                    String msg = plugin.getConfig().getString("target-view-distance-change-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    target.setViewDistance(amount);
                    target.getPlayer().sendMessage(msg);

                    PlayerDataHandler dataHandler = new PlayerDataHandler();
                    dataHandler.setChunks(amount);

                    PlayerUtility.setPlayerDataHandler(target, dataHandler);
                }

            }
        } catch (Exception e) {
            if (sender instanceof Player) {
                sender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
        }
        return true;
    }
}
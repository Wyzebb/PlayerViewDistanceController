package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public SetCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set your own max view distance or the max view distance of another player";
    }

    @Override
    public String getSyntax() {
        return "/vd set <chunks> [player]";
    }

    private String msg;

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {

        if (args.length < 2 || args.length > 3) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(plugin.getConfig().getString("incorrect-args"));
            } else {
                plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
            }
        } else {
            int amount = 32;

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount, plugin);
            } catch (Exception e) {
                if (commandSender instanceof Player) {
                    commandSender.sendMessage(plugin.getConfig().getString("incorrect-args"));
                } else {
                    plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
                }
            }

            if (args.length == 2) {
                if (commandSender instanceof Player player) {
                    msg = plugin.getConfig().getString("self-view-distance-change-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    commandSender.sendMessage(msg);
                    player.setViewDistance(amount);

                    PlayerDataHandler dataHandler = new PlayerDataHandler();
                    dataHandler.setChunks(amount);
                    PlayerUtility.setPlayerDataHandler(player, dataHandler);

                } else {
                    plugin.getLogger().info(plugin.getConfig().getString("change-consoleorcmd-distance-error"));
                }

            } else {
                String targetName = args[2];

                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    if (commandSender instanceof Player) {
                        commandSender.sendMessage(plugin.getConfig().getString("player-offline-msg"));
                    } else {
                        plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-player-offline-msg"));
                    }

                } else if (commandSender == target) {
                    msg = plugin.getConfig().getString("self-view-distance-change-msg");
                    msg = msg.replace("{chunks}", String.valueOf(amount));
                    commandSender.sendMessage(msg);
                    target.setViewDistance(amount);

                    PlayerDataHandler dataHandler = new PlayerDataHandler();
                    dataHandler.setChunks(amount);
                    PlayerUtility.setPlayerDataHandler(target, dataHandler);

                } else {
                    if (commandSender instanceof Player) {
                        String msg = plugin.getConfig().getString("commandSender-view-distance-change-msg");
                        msg = msg.replace("{target-player}", target.getName());
                        msg = msg.replace("{chunks}", String.valueOf(amount));
                        commandSender.sendMessage(msg);

                    } else {
                        String msg = plugin.getConfig().getString("consoleorcmdblock-commandSender-view-distance-change-msg");
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
        }

    }
}
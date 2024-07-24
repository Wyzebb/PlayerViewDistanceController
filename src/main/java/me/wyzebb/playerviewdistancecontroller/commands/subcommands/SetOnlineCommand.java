package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetOnlineCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public SetOnlineCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    String msg;

    @Override
    public void performCommand(@NotNull CommandSender commandSender, @NotNull String[] args) {
        if (args.length != 2) {
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
                if (commandSender instanceof Player) {
                    commandSender.sendMessage(plugin.getConfig().getString("incorrect-args"));
                } else {
                    plugin.getLogger().warning(plugin.getConfig().getString("consoleorcmdblock-incorrect-args"));
                }
            }
        }
    }

    @Override
    public String getName() {
        return "setonline";
    }

    @Override
    public String getDescription() {
        return "Sets the max view distance of all online players";
    }

    @Override
    public String getSyntax() {
        return "/vd setonline <chunks>";
    }
}
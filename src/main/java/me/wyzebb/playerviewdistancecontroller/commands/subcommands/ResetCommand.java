package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ResetCommand extends SubCommand {

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "Resets a player's saved max view distance so that just Luckperms permissions are used";
    }

    @Override
    public String getSyntax() {
        return "/pvdc reset [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    resetSelf(commandSender);
                } else {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

            } else {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessConfigMessagesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    resetSelf(commandSender);

                } else {
                    if (commandSender.hasPermission("pvdc.reset-others")) {
                        ProcessConfigMessagesUtility.processMessage("reset-msg", target, commandSender);
                    } else {
                        ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
                    }
                }
            }
        }
    }

    private void resetSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.reset-self")) {
            PlayerUtility playerUtility = new PlayerUtility();
            Player player = (Player) commandSender;

            File playerDataFile = playerUtility.getPlayerDataFile((Player) commandSender);
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

            cfg.set("chunks", 32);
            cfg.set("chunksOthers", 32);

            try {
                plugin.getLogger().info("Attempting to reset player data for: " + commandSender.getName());
                cfg.save(playerDataFile);
                plugin.getLogger().info("Player data saved successfully for: " + commandSender.getName());
            } catch (IOException ioException) {
                plugin.getLogger().severe("IOException occurred while resetting player view distance data for " + commandSender.getName() + ": " + ioException.getMessage());
                ioException.printStackTrace(); // Print the stack trace for detailed debugging
            } catch (Exception ex) {
                plugin.getLogger().severe("An unexpected error occurred resetting the player view distance data for " + commandSender.getName() + ": " + ex.getMessage());
                ex.printStackTrace(); // Print the stack trace for unexpected errors
            } finally {
                PlayerViewDistanceController.playerAfkMap.remove(player.getUniqueId());
                PlayerUtility.setPlayerDataHandler(player, null);
            }

            VdCalculator.calcVdAndSet(player);




            ProcessConfigMessagesUtility.processMessage("self-reset-msg", commandSender);
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ResetCommand extends SubCommand {

    private final LanguageManager languageManager;

    public ResetCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("commands.reset");
    }

    @Override
    public String getSyntax() {
        return "/pvdc reset [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    resetSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

            } else {
                String targetName = args[1];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    MessageProcessor.processMessage("messages.player-offline", 1, 0, commandSender);

                } else if (commandSender == target) {
                    resetSelf(commandSender);

                } else {
                    if (commandSender.hasPermission("pvdc.reset-others")) {
                        MessageProcessor.processMessage("messages.reset", 2, target, 0, commandSender);
                    } else {
                        MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
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
            cfg.set("chunksOthers", 0);

            try {
                cfg.save(playerDataFile);
            } catch (Exception ex) {
                plugin.getLogger().severe("An exception occurred when resetting view distance data for " + commandSender.getName() + ": " + ex.getMessage());
            } finally {
                PlayerViewDistanceController.playerAfkMap.remove(player.getUniqueId());
                PlayerUtility.setPlayerDataHandler(player, null);
            }

            VdCalculator.calcVdReset(player);

            MessageProcessor.processMessage("messages.self-reset", 2, 0, commandSender);
        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
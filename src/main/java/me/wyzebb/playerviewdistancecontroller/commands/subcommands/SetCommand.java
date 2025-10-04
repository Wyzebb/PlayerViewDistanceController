package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class SetCommand extends SubCommand {

    private final LanguageManager languageManager;

    public SetCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("set-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    MessageProcessor.processMessage("not-player", MessageType.ERROR, 0, commandSender);
                }

            } else {
                if (!ClampAmountUtility.isNumeric(args[1])) {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }

                String targetName = args[2];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    setSelf(commandSender, amount);
                } else {
                    setOthers(commandSender, target, amount);
                }
            }
        }
    }

    public static void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = LPDetector.getLuckpermsDistance((Player) commandSender);

            if (luckpermsMax >= amount || commandSender.hasPermission("pvdc.bypass-maxdistance")) {
                MessageProcessor.processMessage("self-view-distance-change", MessageType.SUCCESS, amount, commandSender);

                DataProcessorUtility.processData((Player) commandSender, amount);

                DataProcessorUtility.processDataOthers((Player) commandSender, 0);
            } else {
                MessageProcessor.processMessage("chunks-too-high", MessageType.ERROR, luckpermsMax, commandSender);
            }

        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }

    public static void setOthers(CommandSender commandSender, OfflinePlayer target, int amount) {
        if (commandSender.hasPermission("pvdc.set-others") || commandSender instanceof ConsoleCommandSender) {
            if (!target.isOnline()) {
                File playerDataFile = DataHandlerHandler.getPlayerDataFile(target);

                PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);

                if (playerDataFile.exists()) {
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                    dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
                    dataHandler.setChunksOthers(cfg.getInt("chunksOthers"));
                    dataHandler.setPingMode(cfg.getBoolean("pingMode"));
                }

                DataHandlerHandler.setPlayerDataHandler(target, dataHandler);
            }

            DataProcessorUtility.processDataOthers(target, amount);
            MessageProcessor.processMessage("sender-view-distance-change", MessageType.SUCCESS, target, amount, commandSender);

            if (target.isOnline()) {
                Player player = (Player) target;
                // Build context for command execution using factory
                ViewDistanceCalculationContext context = ViewDistanceContextFactory.createCommandContext(player, amount);

                ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
                int appliedAmount = result.getViewDistance();

                MessageProcessor.processMessage("target-view-distance-change", MessageType.SUCCESS, target, appliedAmount, player);
            } else {
                // Remove the data handler from memory and save
                PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);

                if (plugin.getPluginConfig().savePlayerData()) {
                    File playerDataFile = DataHandlerHandler.getPlayerDataFile(target);
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                    cfg.set("chunks", dataHandler.getChunks());
                    cfg.set("chunksOthers", dataHandler.getChunksOthers());
                    cfg.set("pingMode", dataHandler.isPingMode());

                    try {
                        cfg.save(playerDataFile);
                    } catch (Exception ex) {
                        plugin.getLogger().severe("An exception occurred when setting view distance data for " + target.getName() + ": " + ex.getMessage());
                    } finally {
                        DataHandlerHandler.setPlayerDataHandler(target, null);
                    }
                } else {
                    DataHandlerHandler.setPlayerDataHandler(target, null);
                }
            }
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }
}
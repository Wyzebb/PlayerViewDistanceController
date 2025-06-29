package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
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
        return languageManager.getLanguageFile().getString("commands.set");
    }

    @Override
    public String getSyntax() {
        return "/pvdc set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    MessageProcessor.processMessage("messages.not-player", 1, 0, commandSender);
                }

            } else {

                if (!ClampAmountUtility.isNumeric(args[1])) {
                    MessageProcessor.processMessage("messages.incorrect-args", 1, 0, commandSender);
                }

                String targetName = args[2];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    setSelf(commandSender, amount);
                } else {
                    if (commandSender.hasPermission("pvdc.set-others") || commandSender instanceof ConsoleCommandSender) {
                        if (!target.isOnline()) {
                            PlayerUtility playerUtility = new PlayerUtility();
                            File playerDataFile = playerUtility.getPlayerDataFile(target);

                            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);

                            if (playerDataFile.exists()) {
                                FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                                dataHandler.setChunks(ClampAmountUtility.clampChunkValue(cfg.getInt("chunks")));
                                dataHandler.setChunksOthers(cfg.getInt("chunksOthers"));
                                dataHandler.setPingMode(cfg.getBoolean("pingMode"));
                                dataHandler.setChunksPing(cfg.getInt("chunksPing"));
                            }

                            PlayerUtility.setPlayerDataHandler(target, dataHandler);
                        }

                        DataProcessorUtility.processDataOthers(target, amount);
                        MessageProcessor.processMessage("messages.sender-view-distance-change", 2, target, amount, commandSender);

                        if (target.isOnline()) {
                            ((Player) target).setViewDistance(amount);

                            if (plugin.getConfig().getBoolean("sync-simulation-distance")) {
                                ((Player) target).setSimulationDistance(amount);
                            }

                            MessageProcessor.processMessage("messages.target-view-distance-change", 2, target, amount, (Player) target);
                        } else {
                            // Remove the data handler from memory and save
                            PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(target);
                            PlayerUtility playerDataHandler = new PlayerUtility();

                            File playerDataFile = playerDataHandler.getPlayerDataFile(target);
                            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

                            cfg.set("chunks", dataHandler.getChunks());
                            cfg.set("chunksOthers", dataHandler.getChunksOthers());
                            cfg.set("pingMode", dataHandler.isPingMode());
                            cfg.set("chunksPing", dataHandler.getChunksPing());

                            try {
                                cfg.save(playerDataFile);
                            } catch (Exception ex) {
                                plugin.getLogger().severe("An exception occurred when setting view distance data for " + target.getName() + ": " + ex.getMessage());
                            } finally {
                                PlayerUtility.setPlayerDataHandler(target, null);
                            }
                        }
                    } else {
                        MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
                    }
                }
            }
        }
    }

    public static void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = 32;

            if (LuckPermsDetector.detectLuckPerms()) {
                luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance((Player) commandSender);
            }

            if (luckpermsMax >= amount || commandSender.hasPermission("pvdc.bypass-maxdistance")) {
                MessageProcessor.processMessage("messages.self-view-distance-change", 2, amount, commandSender);

                DataProcessorUtility.processData((Player) commandSender, amount);

                DataProcessorUtility.processDataOthers((Player) commandSender, 0);
            } else {
                MessageProcessor.processMessage("messages.chunks-too-high", 1, luckpermsMax, commandSender);
            }

        } else {
            MessageProcessor.processMessage("messages.no-permission", 1, 0, commandSender);
        }
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.integrations.ClientViewDistanceTracker;
import me.wyzebb.playerviewdistancecontroller.integrations.GeyserDetector;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Shows information for view distance configuration and player AFK state.
 */
public class InfoCommand extends SubCommand {

    private final LanguageManager languageManager;

    public InfoCommand() {
        this.languageManager = plugin.getLanguageManager();
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return languageManager.getLanguageFile().getString("info-cmd-description");
    }

    @Override
    public String getSyntax() {
        return "/pvdc info [player]";
    }

    /**
     * Executes the info command.
     * 
     * @param commandSender The sender of the command
     * @param args Command arguments containing optional target player name
     */
    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 1 || args.length > 2) {
            MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
        } else {
            if (args.length == 1) {
                if (commandSender instanceof Player) {
                    sendInfoToSelf(commandSender);
                } else {
                    MessageProcessor.processMessage("incorrect-args", MessageType.ERROR, 0, commandSender);
                }
            } else {
                String targetName = args[1];
                OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

                if (commandSender == target) {
                    sendInfoToSelf(commandSender);
                } else {
                    if (commandSender.hasPermission("pvdc.info-others") || commandSender instanceof ConsoleCommandSender) {
                        sendInfoForPlayer(target, commandSender);
                    } else {
                        MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
                    }
                }
            }
        }
    }

    /**
     * Sends information for the command sender.
     * Requires pvdc.info-self permission.
     * 
     * @param commandSender The command sender
     */
    private void sendInfoToSelf(CommandSender commandSender) {
        if (commandSender.hasPermission("pvdc.info-self")) {
            Player player = (Player) commandSender;
            sendInfoForPlayer(player, commandSender);
        } else {
            MessageProcessor.processMessage("no-permission", MessageType.ERROR, 0, commandSender);
        }
    }

    /**
     * Sends information for the target player.
     * 
     * @param target The player to get info for
     * @param commandSender The sender who will receive the output
     */
    private void sendInfoForPlayer(OfflinePlayer target, CommandSender commandSender) {
        String playerName = target.getName();
        boolean isOnline = target.isOnline();
        
        commandSender.sendMessage("§6=== PVDC View Distance Info for " + playerName + " ===");
        
        if (!isOnline) {
            commandSender.sendMessage("§cPlayer is OFFLINE - showing saved data only");
        }

        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(target);
        
        // Basic View Distance Information
        if (isOnline) {
            Player onlinePlayer = (Player) target;
            int appliedDistance = onlinePlayer.getViewDistance();
            commandSender.sendMessage("§aApplied View Distance: §f" + appliedDistance + " chunks");
        }
        
        int basePreference = dataHandler.getChunks();
        commandSender.sendMessage("§aBase Preference: §f" + basePreference + " chunks");
        
        int chunksOthers = dataHandler.getChunksOthers();
        if (chunksOthers != 0 && chunksOthers != -1) {
            commandSender.sendMessage("§eAdmin Override: §f" + chunksOthers + " chunks");
        }
        
        // Client and Permission Information
        if (isOnline) {
            int clientPref = ClientViewDistanceTracker.getLastKnownClientVD(target.getUniqueId());
            if (clientPref > 0) {
                commandSender.sendMessage("§9Client Preference: §f" + clientPref + " chunks");
            }
        }
        
        int permissionMax = ClampAmountUtility.clampChunkValue(LPDetector.getLuckpermsDistance(target));
        commandSender.sendMessage("§aPermission Max: §f" + permissionMax + " chunks");
        
        // Player State and Type
        if (isOnline) {
            PlayerState playerState = plugin.getStateManager().getPlayerState(target.getUniqueId());
            commandSender.sendMessage("§aPlayer State: §f" + playerState.name());
        }
        
        boolean isBedrockPlayer = GeyserDetector.checkBedrockPlayer(target.getUniqueId());
        String playerType = isBedrockPlayer ? "Bedrock" : "Java";
        commandSender.sendMessage("§aPlayer Type: §f" + playerType);
        
        // Mode Information
        boolean pingMode = dataHandler.isPingMode();
        String pingStatus = pingMode ? "§aON" : "§cOFF";
        commandSender.sendMessage("§aPing Mode: " + pingStatus);
        
        boolean dynamicMode = PlayerViewDistanceController.dynamicModeEnabled;
        String dynamicStatus = dynamicMode ? "§aON" : "§cOFF";
        if (dynamicMode) {
            int reduction = PlayerViewDistanceController.dynamicReducedChunks;
            dynamicStatus += "§f (-" + reduction + " chunks)";
        }
        commandSender.sendMessage("§aDynamic Mode: " + dynamicStatus);
        
        // Configuration Information
        boolean clientOptimization = plugin.getPluginConfig().useClientViewDistance();
        String clientOptStatus = clientOptimization ? "§aENABLED" : "§cDISABLED";
        commandSender.sendMessage("§aClient Optimization: " + clientOptStatus);
        
        commandSender.sendMessage("§6========================");
    }
}
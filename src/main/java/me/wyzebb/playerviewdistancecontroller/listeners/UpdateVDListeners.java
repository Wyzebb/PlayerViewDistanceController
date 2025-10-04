package me.wyzebb.playerviewdistancecontroller.listeners;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.config.ConfigKeys;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.ClientViewDistanceTracker;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import me.wyzebb.playerviewdistancecontroller.utility.UpdateChecker;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataHandlerHandler;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerDataManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class UpdateVDListeners implements Listener {

    private final MiniMessage mm;

    public UpdateVDListeners() {
        this.mm = MiniMessage.miniMessage();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.getConfig().getBoolean(ConfigKeys.UPDATE_CHECKER_ENABLED)) {
            if (e.getPlayer().isOp() && !UpdateChecker.isUpToDate()) {
                Component updateMsg = mm.deserialize("<yellow><b>(!)</b> <click:open_url:'https://modrinth.com/plugin/pvdc'><hover:show_text:'<green>Click to go to the plugin page</green>'>PVDC update available: <b><red>v" + UpdateChecker.getPluginVersion() + "</red> -> <green>v" + UpdateChecker.getLatestVersion() + "</green></b></hover></click></yellow>");

                e.getPlayer().sendMessage(updateMsg);
            }
            if (e.getPlayer().isOp() && UpdateChecker.isExperimental()) {
                Component updateMsg = mm.deserialize("<yellow><b>(!)</b> You are using an experimental version of PVDC. Proceed with caution!</yellow>");

                e.getPlayer().sendMessage(updateMsg);
            }
        }

        // Register player with state manager
        plugin.getStateManager().onPlayerJoin(e.getPlayer());
        
        // Load player data from file before context creation
        Player player = e.getPlayer();
        PlayerDataManager.ensureDataLoaded(player);
        
        // Use factory for player join context
        ViewDistanceCalculationContext context = ViewDistanceContextFactory.createJoinContext(player);

        ViewDistanceUtility.applyOptimalViewDistance(context);

        if (plugin.getConfig().getBoolean(ConfigKeys.AFK_ON_JOIN)) {
            plugin.getFoliaLib().getScheduler().runLater(() -> {
                if (!player.hasPermission("pvdc.bypass-afk")) {
                    // Transition to AFK state
                    plugin.getStateManager().transitionState(player, PlayerState.AFK);
                    
                    int afkChunks = 0;
                    if (!plugin.getConfig().getBoolean(ConfigKeys.ZERO_CHUNKS_AFK)) {
                        afkChunks = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt(ConfigKeys.AFK_CHUNKS));
                    }

                    // Build AFK context using factory
                    ViewDistanceCalculationContext afkContext = ViewDistanceContextFactory.createAfkContext(player, afkChunks);

                    ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(afkContext);
                    int appliedAfkChunks = result.getViewDistance();

                    MessageProcessor.processMessage("afk", 3, appliedAfkChunks, player);
                } else {
                    // Transition to active state if bypassing AFK
                    plugin.getStateManager().transitionState(player, PlayerState.ACTIVE);
                }
            }, 10);
        } else {
            // Transition to active state after join
            plugin.getFoliaLib().getScheduler().runLater(() -> {
                if (e.getPlayer().isOnline()) {
                    plugin.getStateManager().transitionState(e.getPlayer(), PlayerState.ACTIVE);
                }
            }, 1L);
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        // Notify state manager of player quit
        plugin.getStateManager().onPlayerQuit(e.getPlayer());
        
        PlayerDataHandler dataHandler = DataHandlerHandler.getPlayerDataHandler(e.getPlayer());

        if (PlayerViewDistanceController.isPlayerDataSavingEnabled()) {
            File playerDataFile = DataHandlerHandler.getPlayerDataFile(e.getPlayer());
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(playerDataFile);

            cfg.set("chunks", dataHandler.getChunks());
            cfg.set("chunksOthers", dataHandler.getChunksOthers());
            cfg.set("pingMode", dataHandler.isPingMode());

            try {
                cfg.save(playerDataFile);
            } catch (Exception ex) {
                plugin.getLogger().severe("An exception occurred when setting view distance data for " + e.getPlayer().getName() + ": " + ex.getMessage());
            } finally {
                DataHandlerHandler.setPlayerDataHandler(e.getPlayer(), null);
            }
        } else {
            DataHandlerHandler.setPlayerDataHandler(e.getPlayer(), null);
        }
            
        // Cleanup client view distance tracking data
        ClientViewDistanceTracker.onPlayerLeave(e.getPlayer());
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        if (plugin.getConfig().getBoolean(ConfigKeys.RECALCULATE_VD_ON_WORLD_CHANGE)) {
            Player player = event.getPlayer();
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createWorldChangeContext(player);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }
}

package me.wyzebb.playerviewdistancecontroller.listeners;

import me.wyzebb.playerviewdistancecontroller.Database;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.ClientViewDistanceTracker;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import me.wyzebb.playerviewdistancecontroller.utility.UpdateChecker;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class UpdateVDListeners implements Listener {

    private final MiniMessage mm;

    public UpdateVDListeners() {
        this.mm = MiniMessage.miniMessage();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent e) {
        if (plugin.getPluginConfig().isUpdateCheckerEnabled()) {
            if (e.getPlayer().isOp() && !UpdateChecker.isUpToDate()) {
                Component updateMsg = mm.deserialize("<yellow><b>(!)</b> <click:open_url:'https://modrinth.com/plugin/pvdc'><hover:show_text:'<green>Click to go to the plugin page</green>'>PVDC update available: <b><red>v" + UpdateChecker.getPluginVersion() + "</red> -> <green>v" + UpdateChecker.getLatestVersion() + "</green></b></hover></click></yellow>");

                e.getPlayer().sendMessage(updateMsg);
            }
            if (e.getPlayer().isOp() && UpdateChecker.isExperimental()) {
                Component updateMsg = mm.deserialize("<yellow><b>(!)</b> You are using an experimental version of PVDC. Proceed with caution!</yellow>");

                e.getPlayer().sendMessage(updateMsg);
            }
        }

        final Player player = e.getPlayer();
        plugin.getStateManager().onPlayerJoin(player);
        Database.getInstance().createUser(player);
        
        // Use factory for player join context
        ViewDistanceCalculationContext context = ViewDistanceContextFactory.createJoinContext(player);
        ViewDistanceUtility.applyOptimalViewDistance(context);

        if (plugin.getPluginConfig().isAfkOnJoinEnabled()) {
            plugin.getFoliaLib().getScheduler().runLater(() -> {
                if (!player.hasPermission("pvdc.bypass-afk")) {
                    // Transition to AFK state
                    plugin.getStateManager().transitionState(player, PlayerState.AFK);
                    
                    int afkChunks = 0;
                    if (!plugin.getPluginConfig().isVoidAfkEnabled()) {
                        afkChunks = ClampAmountUtility.clampChunkValue(plugin.getPluginConfig().getAfkChunks());
                    }

                    // Build AFK context using factory
                    ViewDistanceCalculationContext afkContext = ViewDistanceContextFactory.createAfkContext(player, afkChunks);

                    ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(afkContext);
                    int appliedAfkChunks = result.getViewDistance();

                    MessageProcessor.processMessage("afk", MessageType.INFO, appliedAfkChunks, player);
                } else {
                    // Transition to active state if bypassing AFK
                    plugin.getStateManager().transitionState(player, PlayerState.ACTIVE);
                }
            }, 10);
        } else {
            // Transition to active state after join
            plugin.getFoliaLib().getScheduler().runLater(() -> {
                if (player.isOnline()) {
                    plugin.getStateManager().transitionState(player, PlayerState.ACTIVE);
                }
            }, 1L);
        }
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent e) {
        // Notify state manager of player quit
        plugin.getStateManager().onPlayerQuit(e.getPlayer());

        // Clean up client view distance tracking data
        ClientViewDistanceTracker.onPlayerLeave(e.getPlayer());
    }

    @EventHandler
    private void onWorldChange(PlayerChangedWorldEvent event) {
        if (plugin.getPluginConfig().recalculateViewDistanceOnWorldChange()) {
            Player player = event.getPlayer();
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createWorldChangeContext(player);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }
}

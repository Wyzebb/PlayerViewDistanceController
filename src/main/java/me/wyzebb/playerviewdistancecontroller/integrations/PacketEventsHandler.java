package me.wyzebb.playerviewdistancecontroller.integrations;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Handles PacketEvents integration for client view distance tracking.
 * This class contains PacketEvents imports and is only loaded when PacketEvents is available.
 */
public class PacketEventsHandler {
    
    private static final Map<UUID, Integer> lastKnownClientVD = new HashMap<>();
    private static ClientPacketListenerImpl packetListener;
    
    /**
     * Initializes the PacketEvents integration for client view distance tracking.
     * Registers the packet listener to monitor client settings packets.
     * 
     * @throws RuntimeException if PacketEvents setup fails
     */
    public static void initialize() {
        try {
            packetListener = new ClientPacketListenerImpl();
            PacketEvents.getAPI().getEventManager().registerListener(packetListener);
            plugin.getLogger().info("Successfully registered PacketEvents packet listener for client view distance tracking");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to setup PacketEvents packet listener: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Shuts down the PacketEvents integration and cleans up resources.
     * Unregisters the packet listener and clears cached client view distance data.
     */
    public static void shutdown() {
        try {
            if (packetListener != null) {
                PacketEvents.getAPI().getEventManager().unregisterListener(packetListener);
            }
            lastKnownClientVD.clear();
        } catch (Exception e) {
            plugin.getLogger().warning("Error cleaning up PacketEvents packet listener: " + e.getMessage());
        }
    }
    
    /**
     * Called when a client view distance change is detected through packet monitoring.
     * 
     * @param player The player whose client view distance changed
     * @param oldVD The previous client view distance
     * @param newVD The new client view distance
     */
    public static void onClientViewDistanceChanged(Player player, int oldVD, int newVD) {
        if (!plugin.getPluginConfig().useClientViewDistance()) {
            return;
        }
        
        try {
            // Store the new client VD
            lastKnownClientVD.put(player.getUniqueId(), newVD);
            
            // Recalculate view distance with the new client setting using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createClientChangeContext(player, newVD);

            ViewDistanceUtility.applyOptimalViewDistance(context);
            
            plugin.getLogger().info(String.format(
                "Client VD changed for %s: %d -> %d, recalculated view distance",
                player.getName(), oldVD, newVD
            ));
            
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to sync client view distance change for " + 
                player.getName() + ": " + e.getMessage());
        }
    }
    
    /**
     * Removes cached client view distance data when a player leaves the server.
     * Called during player quit events to prevent memory leaks.
     * 
     * @param player The player who is leaving the server
     */
    public static void onPlayerLeave(Player player) {
        lastKnownClientVD.remove(player.getUniqueId());
    }
    
    /**
     * Gets the last known client view distance for a player.
     * Returns -1 if no client view distance has been recorded for this player.
     * 
     * @param playerId The UUID of the player
     * @return The last known client view distance, or -1 if unknown
     */
    public static int getLastKnownClientVD(UUID playerId) {
        return lastKnownClientVD.getOrDefault(playerId, -1);
    }
    
    /**
     * Applies any pending client view distance changes for a player returning from AFK.
     * Used to restore client optimization when a player becomes active again.
     * 
     * @param player The player returning from AFK state
     */
    public static void applyPendingClientViewDistance(Player player) {
        if (!plugin.getPluginConfig().useClientViewDistance()) {
            return;
        }
        
        // Check if we have stored client VD data
        Integer clientVD = lastKnownClientVD.get(player.getUniqueId());
        if (clientVD != null && clientVD > 0) {
            // Recalculate view distance taking client VD into account using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createClientChangeContext(player, clientVD);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }
    
    /**
     * Internal packet listener implementation for monitoring client settings packets.
     * Detects changes in client view distance and triggers recalculation.
     */
    private static class ClientPacketListenerImpl extends PacketListenerAbstract {
        
        public ClientPacketListenerImpl() {
            super(PacketListenerPriority.NORMAL);
        }
        
        /**
         * Handles incoming packets from clients.
         * Filters for client settings packets and delegates processing.
         * 
         * @param event The packet receive event
         */
        @Override
        public void onPacketReceive(PacketReceiveEvent event) {
            // Check if this is a client settings packet
            if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
                handleClientSettingsPacket(event);
            }
        }
        
        /**
         * Handles incoming client settings packets to detect view distance changes.
         * Extracts the view distance from the packet and compares with previously known values.
         * 
         * @param event The client settings packet event
         */
        private void handleClientSettingsPacket(PacketReceiveEvent event) {
            try {
                // Get the player from the event
                UUID playerId = event.getUser().getUUID();
                Player player = Bukkit.getPlayer(playerId);
                
                if (player == null) {
                    return;
                }
                
                // Extract view distance from the client settings packet
                int newClientVD = extractViewDistanceFromPacket(event);
                
                if (newClientVD > 0) {
                    int previousVD = getLastKnownClientVD(playerId);
                    
                    if (previousVD != newClientVD && previousVD != -1) {
                        // Client view distance changed!
                        onClientViewDistanceChanged(player, previousVD, newClientVD);
                    } else if (previousVD == -1) {
                        // First time seeing this player, just store the value
                        onClientViewDistanceChanged(player, 0, newClientVD);
                    }
                }
                
            } catch (Exception e) {
                plugin.getLogger().warning("Error processing client settings packet: " + e.getMessage());
            }
        }
        
        /**
         * Extracts view distance value from the Client Settings packet.
         * Validates the extracted value is within acceptable bounds.
         * 
         * @param event The packet event containing client settings
         * @return The client view distance, or -1 if invalid or extraction failed
         */
        private int extractViewDistanceFromPacket(PacketReceiveEvent event) {
            try {
                // Use the wrapper to get the view distance
                WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
                int viewDistance = wrapper.getViewDistance();
                
                if (viewDistance >= ClampAmountUtility.getMinPossible() && viewDistance <= ClampAmountUtility.getMaxPossible()) {
                    return viewDistance;
                }
                return -1;
            } catch (Exception e) {
                plugin.getLogger().warning("Exception extracting view distance: " + e.getMessage());
                return -1;
            }
        }
    }
}
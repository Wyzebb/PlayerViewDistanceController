package me.wyzebb.playerviewdistancecontroller.integrations;

import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Tracks client view distance changes using PacketEvents packet detection.
 * Provides event-driven synchronization when clients change their view distance settings.
 */
public class ClientViewDistanceTracker {
    
    private static final Map<UUID, Integer> lastKnownClientVD = new HashMap<>();
    private static boolean packetEventsAvailable = false;
    private static ClientPacketListener packetListener;
    
    /**
     * Initializes the client view distance tracker with PacketEvents integration
     */
    public static void initialize() {
        packetEventsAvailable = detectPacketEvents();
        
        if (packetEventsAvailable) {
            try {
                packetListener = new ClientPacketListener();
                packetListener.setupPacketListener();
                plugin.getLogger().info("PacketEvents detected - enabling automatic client view distance sync");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to initialize PacketEvents packet listener: " + e.getMessage());
                packetEventsAvailable = false;
            }
        } else {
            plugin.getLogger().info("PacketEvents not available - client view distance changes won't be auto-detected");
        }
    }
    
    /**
     * Cleans up resources when plugin is disabled
     */
    public static void shutdown() {
        if (packetListener != null) {
            packetListener.cleanup();
        }
        lastKnownClientVD.clear();
    }
    
    /**
     * Called when a client view distance change is detected
     */
    public static void onClientViewDistanceChanged(Player player, int oldVD, int newVD) {
        if (!plugin.getConfig().getBoolean("use-client-view-distance", false)) {
            // Client optimization is disabled, no need to sync
            return;
        }
        
        try {
            // Store the new client VD
            lastKnownClientVD.put(player.getUniqueId(), newVD);
            
            // Recalculate view distance with the new client setting
            VdCalculator.calcVdSet(player, false, true, false);
            
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
     * Removes player data when they leave the server
     */
    public static void onPlayerLeave(Player player) {
        lastKnownClientVD.remove(player.getUniqueId());
    }
    
    /**
     * Gets the last known client view distance for a player
     */
    public static int getLastKnownClientVD(UUID playerId) {
        return lastKnownClientVD.getOrDefault(playerId, -1);
    }
    
    /**
     * Checks if PacketEvents is available and enabled
     */
    public static boolean isPacketEventsAvailable() {
        return packetEventsAvailable;
    }
    
    /**
     * Detects if PacketEvents is available on the server
     */
    private static boolean detectPacketEvents() {
        return Bukkit.getPluginManager().isPluginEnabled("packetevents");
    }
}
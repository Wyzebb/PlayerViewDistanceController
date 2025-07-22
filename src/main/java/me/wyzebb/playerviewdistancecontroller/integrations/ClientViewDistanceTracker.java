package me.wyzebb.playerviewdistancecontroller.integrations;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Detects and manages PacketEvents integration for client view distance tracking.
 * Checks if PacketEvents is available before delegating to PacketEventsHandler.
 */
public class ClientViewDistanceTracker {
    
    private static boolean packetEventsAvailable = false;
    
    /**
     * Initializes the client view distance tracker with PacketEvents integration
     */
    public static void initialize() {
        if (!Bukkit.getPluginManager().isPluginEnabled("packetevents")) {
            plugin.getLogger().info("PacketEvents not available - client view distance changes won't be auto-detected");
            return;
        }
        
        try {
            // Check if PacketEvents classes are available
            Class.forName("com.github.retrooper.packetevents.PacketEvents");
            PacketEventsHandler.initialize();
            packetEventsAvailable = true;
            plugin.getLogger().info("PacketEvents detected - enabling automatic client view distance sync");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize PacketEvents integration: " + e.getMessage());
            packetEventsAvailable = false;
        }
    }
    
    /**
     * Cleans up resources when plugin is disabled
     */
    public static void shutdown() {
        if (packetEventsAvailable) {
            try {
                PacketEventsHandler.shutdown();
            } catch (Exception e) {
                plugin.getLogger().warning("Error cleaning up PacketEvents integration: " + e.getMessage());
            }
        }
        packetEventsAvailable = false;
    }
    
    /**
     * Called when a client view distance change is detected
     */
    public static void onClientViewDistanceChanged(Player player, int oldVD, int newVD) {
        if (packetEventsAvailable) {
            PacketEventsHandler.onClientViewDistanceChanged(player, oldVD, newVD);
        }
    }
    
    /**
     * Removes player data when they leave the server
     */
    public static void onPlayerLeave(Player player) {
        if (packetEventsAvailable) {
            PacketEventsHandler.onPlayerLeave(player);
        }
    }
    
    /**
     * Gets the last known client view distance for a player.
     * Falls back to Bukkit API (org.bukkit.entity.Player.getClientViewDistance) when PacketEvents unavailable or has no data yet.
     */
    public static int getLastKnownClientVD(UUID playerId) {
        int packetEventsVD = -1;
        
        if (packetEventsAvailable) {
            packetEventsVD = PacketEventsHandler.getLastKnownClientVD(playerId);
        }
        
        // If PacketEvents unavailable OR has no data yet (e.g., on join), fallback to Bukkit API
        if (packetEventsVD <= 0) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && player.isOnline()) {
                try {
                    return player.getClientViewDistance();
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to get client view distance for " + player.getName() + ": " + e.getMessage());
                }
            }
        }
        
        return packetEventsVD > 0 ? packetEventsVD : -1;
    }
    
    /**
     * Checks if PacketEvents is available and enabled
     */
    public static boolean isPacketEventsAvailable() {
        return packetEventsAvailable;
    }
    
    /**
     * Applies any pending client view distance changes for a player returning from AFK
     */
    public static void applyPendingClientViewDistance(Player player) {
        if (packetEventsAvailable) {
            PacketEventsHandler.applyPendingClientViewDistance(player);
        }
    }
}
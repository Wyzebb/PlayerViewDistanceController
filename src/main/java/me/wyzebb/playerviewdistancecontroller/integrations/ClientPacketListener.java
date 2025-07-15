package me.wyzebb.playerviewdistancecontroller.integrations;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

/**
 * Handles PacketEvents packet listening for client view distance changes.
 * This class is only instantiated when PacketEvents is available.
 */
public class ClientPacketListener extends PacketListenerAbstract {
    
    public ClientPacketListener() {
        super(PacketListenerPriority.NORMAL);
    }
    
    /**
     * Sets up the PacketEvents packet listener for client settings packets
     */
    public void setupPacketListener() {
        try {
            // Register the packet listener
            PacketEvents.getAPI().getEventManager().registerListener(this);
            
            plugin.getLogger().info("Successfully registered PacketEvents packet listener for client view distance tracking");
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to setup PacketEvents packet listener: " + e.getMessage());
            throw new RuntimeException("PacketEvents integration failed", e);
        }
    }
    
    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        // Check if this is a client settings packet
        if (event.getPacketType() == PacketType.Play.Client.CLIENT_SETTINGS) {
            handleClientSettingsPacket(event);
        }
    }
    
    /**
     * Handles incoming client settings packets
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
                int previousVD = ClientViewDistanceTracker.getLastKnownClientVD(playerId);
                
                if (previousVD != newClientVD && previousVD != -1) {
                    // Client view distance changed!
                    ClientViewDistanceTracker.onClientViewDistanceChanged(player, previousVD, newClientVD);
                } else if (previousVD == -1) {
                    // First time seeing this player, just store the value
                    ClientViewDistanceTracker.onClientViewDistanceChanged(player, 0, newClientVD);
                }
            }
            
        } catch (Exception e) {
            plugin.getLogger().warning("Error processing client settings packet: " + e.getMessage());
        }
    }
    
    /**
     * Extracts view distance value from the Client Settings packet.
     */
    private int extractViewDistanceFromPacket(PacketReceiveEvent event) {
        try {
            // Use the wrapper to get the view distance
            WrapperPlayClientSettings wrapper = new WrapperPlayClientSettings(event);
            int viewDistance = wrapper.getViewDistance();
            
            if (viewDistance >= 2 && viewDistance <= 32) {
                return viewDistance;
            }
            return -1;
        } catch (Exception e) {
            plugin.getLogger().warning("Exception extracting view distance: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Cleans up the packet listener
     */
    public void cleanup() {
        try {
            PacketEvents.getAPI().getEventManager().unregisterListener(this);
        } catch (Exception e) {
            plugin.getLogger().warning("Error cleaning up PacketEvents packet listener: " + e.getMessage());
        }
    }
}
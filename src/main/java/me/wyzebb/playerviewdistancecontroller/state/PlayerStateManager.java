package me.wyzebb.playerviewdistancecontroller.state;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player states and transitions for the view distance system.
 */
public class PlayerStateManager {
    
    private final PlayerViewDistanceController plugin;
    private final Map<UUID, PlayerStateInfo> playerStates = new ConcurrentHashMap<>();
    
    /**
     * Inner class to hold state information including timestamps for activity tracking.
     * Tracks current state, last activity time, and when the state was last changed.
     */
    private static class PlayerStateInfo {
        private PlayerState state;
        private long lastActivityTime;
        private long stateChangeTime;
        
        public PlayerStateInfo(PlayerState state) {
            this.state = state;
            this.lastActivityTime = System.currentTimeMillis();
            this.stateChangeTime = System.currentTimeMillis();
        }
    }
    
    public PlayerStateManager(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Gets the current state of a player.
     * Returns ACTIVE as default if no state information is available.
     * 
     * @param playerId The UUID of the player
     * @return The current player state, or ACTIVE if unknown
     */
    public PlayerState getPlayerState(UUID playerId) {
        PlayerStateInfo info = playerStates.get(playerId);
        return info != null ? info.state : PlayerState.ACTIVE;
    }
    
    /**
     * Updates player activity timestamp and handles state transitions.
     * Automatically transitions AFK players to RETURNING_FROM_AFK and then to ACTIVE.
     * 
     * @param player The player who performed an activity
     */
    public void updatePlayerActivity(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerStateInfo info = playerStates.computeIfAbsent(playerId, 
            k -> new PlayerStateInfo(PlayerState.ACTIVE));
        
        // Handle state transitions based on current state
        PlayerState previousState = info.state;
        info.lastActivityTime = System.currentTimeMillis();
        
        if (previousState == PlayerState.AFK) {
            // Transition from AFK to RETURNING_FROM_AFK
            transitionState(player, PlayerState.RETURNING_FROM_AFK);
        } else if (previousState == PlayerState.RETURNING_FROM_AFK) {
            // Complete the return transition
            transitionState(player, PlayerState.ACTIVE);
        }
    }
    
    /**
     * Transitions a player to a new state with validation.
     * Validates the state transition is allowed and triggers state change events.
     * 
     * @param player The player to transition
     * @param newState The new state to transition to
     */
    public void transitionState(Player player, PlayerState newState) {
        UUID playerId = player.getUniqueId();
        PlayerStateInfo info = playerStates.computeIfAbsent(playerId, 
            k -> new PlayerStateInfo(PlayerState.ACTIVE));
        
        PlayerState oldState = info.state;
        
        // Validate state transition
        if (!isValidTransition(oldState, newState)) {
            plugin.getLogger().warning(String.format(
                "Invalid state transition for %s: %s -> %s", 
                player.getName(), oldState, newState
            ));
            return;
        }
        
        info.state = newState;
        info.stateChangeTime = System.currentTimeMillis();
        
        onStateChanged(player, oldState, newState);
    }
    
    /**
     * Checks if a player should be marked as AFK based on inactivity.
     * Considers AFK timeout configuration and bypass permissions.
     * 
     * @param player The player to check for AFK status
     * @return true if the player should be marked as AFK, false otherwise
     */
    public boolean shouldMarkAsAfk(Player player) {
        if (player.hasPermission("pvdc.bypass-afk")) {
            return false;
        }
        
        PlayerStateInfo info = playerStates.get(player.getUniqueId());
        if (info == null || info.state != PlayerState.ACTIVE) {
            return false;
        }
        
        long afkTimeout = plugin.getConfig().getInt("afkTime") * 1000L;
        return (System.currentTimeMillis() - info.lastActivityTime) > afkTimeout;
    }
    
    /**
     * Gets the last activity time for a player in milliseconds.
     * Returns current time if no activity information is available.
     * 
     * @param playerId The UUID of the player
     * @return The timestamp of last activity in milliseconds
     */
    public long getLastActivityTime(UUID playerId) {
        PlayerStateInfo info = playerStates.get(playerId);
        return info != null ? info.lastActivityTime : System.currentTimeMillis();
    }
    
    /**
     * Handles player join by initializing their state to JOINING.
     * Creates initial state tracking for the player.
     * 
     * @param player The player who joined the server
     */
    public void onPlayerJoin(Player player) {
        playerStates.put(player.getUniqueId(), 
            new PlayerStateInfo(PlayerState.JOINING));
    }
    
    /**
     * Handles player quit by marking them as LEAVING and scheduling cleanup.
     * Removes state information after a short delay to allow final operations.
     * 
     * @param player The player who quit the server
     */
    public void onPlayerQuit(Player player) {
        UUID playerId = player.getUniqueId();
        PlayerStateInfo info = playerStates.get(playerId);
        if (info != null) {
            info.state = PlayerState.LEAVING;
        }
        // Clean up after a delay to allow final operations
        plugin.getFoliaLib().getScheduler().runLater(() -> {
            playerStates.remove(playerId);
        }, 20L); // 1 second delay
    }
    
    /**
     * Validates if a state transition is allowed based on predefined rules.
     * Ensures state machine integrity by preventing invalid transitions.
     * 
     * @param from The current state
     * @param to The target state
     * @return true if the transition is valid, false otherwise
     */
    private boolean isValidTransition(PlayerState from, PlayerState to) {
        // Define valid transitions
        switch (from) {
            case ACTIVE:
                return to == PlayerState.AFK || to == PlayerState.LEAVING;
            case AFK:
                return to == PlayerState.RETURNING_FROM_AFK || to == PlayerState.LEAVING;
            case RETURNING_FROM_AFK:
                return to == PlayerState.ACTIVE || to == PlayerState.LEAVING;
            case JOINING:
                return to == PlayerState.ACTIVE || to == PlayerState.AFK;
            case LEAVING:
                return false; // No transitions from LEAVING
            default:
                return false;
        }
    }
    
    /**
     * Called when a player's state changes successfully.
     * Hook point for future functionality like custom events or additional actions.
     * 
     * @param player The player whose state changed
     * @param oldState The previous state
     * @param newState The new state
     */
    private void onStateChanged(Player player, PlayerState oldState, PlayerState newState) {
        // This method can be extended to fire custom events or perform additional actions
        // For now, it serves as a hook point for future functionality
    }
    
    /**
     * Clears all player states and associated data.
     * Used during plugin disable.
     */
    public void clearAllStates() {
        playerStates.clear();
    }
    
    /**
     * Gets a snapshot of current player states for debugging purposes.
     * Returns a copy of the current state mapping to prevent external modification.
     * 
     * @return A snapshot map of player UUIDs to their current states
     */
    public Map<UUID, PlayerState> getStateSnapshot() {
        Map<UUID, PlayerState> snapshot = new ConcurrentHashMap<>();
        playerStates.forEach((uuid, info) -> snapshot.put(uuid, info.state));
        return snapshot;
    }
    
    /**
     * Gets time since last state change for a player in milliseconds.
     * Useful for debugging and determining how long a player has been in their current state.
     * 
     * @param playerId The UUID of the player
     * @return Milliseconds since last state change, or 0 if player not found
     */
    public long getTimeSinceStateChange(UUID playerId) {
        PlayerStateInfo info = playerStates.get(playerId);
        return info != null ? System.currentTimeMillis() - info.stateChangeTime : 0;
    }
}
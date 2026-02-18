package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import org.bukkit.entity.Player;

/**
 * Context object containing all data needed for view distance calculations.
 */
public class ViewDistanceCalculationContext {
    
    private final Player player;
    private final PlayerState playerState;
    private final int baseViewDistance;
    private final int savedViewDistance;
    private final int savedOthersDistance;
    private final boolean isPingModeEnabled;
    private final boolean isDynamicModeEnabled;
    private final int dynamicReduction;
    private final int permissionMaxDistance;
    private final int clientPreferredDistance;
    private final boolean isBedrockPlayer;
    private final boolean isLuckPermsEvent;
    private final boolean sendNoMessages;
    private final boolean isWorldChange;
    private final boolean isSelfChange;

    private ViewDistanceCalculationContext(Builder builder) {
        this.player = builder.player;
        this.playerState = builder.playerState;
        this.baseViewDistance = builder.baseViewDistance;
        this.savedViewDistance = builder.savedViewDistance;
        this.savedOthersDistance = builder.savedOthersDistance;
        this.isPingModeEnabled = builder.isPingModeEnabled;
        this.isDynamicModeEnabled = builder.isDynamicModeEnabled;
        this.dynamicReduction = builder.dynamicReduction;
        this.permissionMaxDistance = builder.permissionMaxDistance;
        this.clientPreferredDistance = builder.clientPreferredDistance;
        this.isBedrockPlayer = builder.isBedrockPlayer;
        this.isLuckPermsEvent = builder.isLuckPermsEvent;
        this.sendNoMessages = builder.sendNoMessages;
        this.isWorldChange = builder.isWorldChange;
        this.isSelfChange = builder.isSelfChange;
    }
    
    // Getters
    public Player getPlayer() { return player; }
    public PlayerState getPlayerState() { return playerState; }
    public int getBaseViewDistance() { return baseViewDistance; }
    public int getSavedViewDistance() { return savedViewDistance; }
    public int getSavedOthersDistance() { return savedOthersDistance; }
    public boolean isPingModeEnabled() { return isPingModeEnabled; }
    public boolean isDynamicModeEnabled() { return isDynamicModeEnabled; }
    public int getDynamicReduction() { return dynamicReduction; }
    public int getPermissionMaxDistance() { return permissionMaxDistance; }
    public int getClientPreferredDistance() { return clientPreferredDistance; }
    public boolean isBedrockPlayer() { return isBedrockPlayer; }
    public boolean isLuckPermsEvent() { return isLuckPermsEvent; }
    public boolean shouldSendNoMessages() { return sendNoMessages; }
    public boolean isWorldChange() { return isWorldChange; }
    public boolean isSelfChange() { return isSelfChange; }

    /**
     * Gets the effective base distance considering saved values
     */
    public int getEffectiveBaseDistance() {
        if (savedOthersDistance != 0 && savedOthersDistance != -1) {
            return savedOthersDistance;
        }
        return savedViewDistance > 0 ? savedViewDistance : baseViewDistance;
    }
    
    public static class Builder {
        private final Player player;
        private PlayerState playerState = PlayerState.ACTIVE;
        private int baseViewDistance;
        private int savedViewDistance = -1;
        private int savedOthersDistance = 0;
        private boolean isPingModeEnabled = false;
        private boolean isDynamicModeEnabled = false;
        private int dynamicReduction = 0;
        private int permissionMaxDistance = 32;
        private int clientPreferredDistance = -1;
        private boolean isBedrockPlayer = false;
        private boolean isLuckPermsEvent = false;
        private boolean sendNoMessages = false;
        private boolean isWorldChange = false;
        private boolean isSelfChange = false;

        public Builder(Player player) {
            this.player = player;
        }
        
        public Builder withPlayerState(PlayerState state) {
            this.playerState = state;
            return this;
        }
        
        public Builder withBaseViewDistance(int distance) {
            this.baseViewDistance = distance;
            return this;
        }
        
        public Builder withSavedViewDistance(int distance) {
            this.savedViewDistance = distance;
            return this;
        }
        
        public Builder withSavedOthersDistance(int distance) {
            this.savedOthersDistance = distance;
            return this;
        }
        
        public Builder withPingMode(boolean enabled) {
            this.isPingModeEnabled = enabled;
            return this;
        }
        
        public Builder withDynamicMode(boolean enabled, int reduction) {
            this.isDynamicModeEnabled = enabled;
            this.dynamicReduction = reduction;
            return this;
        }
        
        public Builder withPermissionMaxDistance(int distance) {
            this.permissionMaxDistance = distance;
            return this;
        }
        
        public Builder withClientPreferredDistance(int distance) {
            this.clientPreferredDistance = distance;
            return this;
        }
        
        public Builder withBedrockPlayer(boolean isBedrock) {
            this.isBedrockPlayer = isBedrock;
            return this;
        }
        
        public Builder withLuckPermsEvent(boolean isEvent) {
            this.isLuckPermsEvent = isEvent;
            return this;
        }
        
        public Builder withSendNoMessages(boolean noMessages) {
            this.sendNoMessages = noMessages;
            return this;
        }
        
        public Builder withWorldChange(boolean isWorldChange) {
            this.isWorldChange = isWorldChange;
            return this;
        }

        public Builder withSelfChange(boolean isSelfChange) {
            this.isSelfChange = isSelfChange;
            return this;
        }
        
        public ViewDistanceCalculationContext build() {
            return new ViewDistanceCalculationContext(this);
        }
    }
    
    /**
     * Creates a builder for the context
     */
    public static Builder builder(Player player) {
        return new Builder(player);
    }
}
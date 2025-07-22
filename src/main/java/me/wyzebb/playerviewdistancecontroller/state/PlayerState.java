package me.wyzebb.playerviewdistancecontroller.state;

/**
 * Represents the different states a player can be in for view distance management.
 */
public enum PlayerState {
    /**
     * Player is actively playing (default state)
     */
    ACTIVE,
    
    /**
     * Player is AFK (Away From Keyboard)
     */
    AFK,
    
    /**
     * Player is returning from AFK state (transitional state)
     * This state ensures proper view distance restoration
     */
    RETURNING_FROM_AFK,
    
    /**
     * Player just joined and is in initial setup
     */
    JOINING,
    
    /**
     * Player is leaving the server
     */
    LEAVING;
    
    /**
     * Checks if this state should use AFK view distance
     */
    public boolean shouldUseAfkViewDistance() {
        return this == AFK;
    }
    
    /**
     * Checks if this state allows normal view distance calculation
     */
    public boolean allowsNormalCalculation() {
        return this == ACTIVE || this == RETURNING_FROM_AFK || this == JOINING;
    }
    
    /**
     * Checks if this is a transitional state
     */
    public boolean isTransitional() {
        return this == RETURNING_FROM_AFK || this == JOINING || this == LEAVING;
    }
}
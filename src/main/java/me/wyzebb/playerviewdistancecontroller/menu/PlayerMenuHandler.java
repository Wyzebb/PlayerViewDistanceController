package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.entity.Player;

public class PlayerMenuHandler {
    private final Player player;
    private Player targetPlayer;
    private String worldId;
    private int currentValue = 0;

    public PlayerMenuHandler(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public String getWorldId() {
        return worldId;
    }

    public void setWorldId(String worldId) {
        this.worldId = worldId;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}

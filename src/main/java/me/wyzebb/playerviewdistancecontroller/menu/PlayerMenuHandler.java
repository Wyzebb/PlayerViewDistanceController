package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.entity.Player;

public class PlayerMenuHandler {
    private final Player player;
    private Player targetPlayer;

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
}

package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class MenuHandlerUtility {
    private static final HashMap<Player, PlayerMenuHandler> playerMenuHandlerMap = new HashMap<>();

    public static PlayerMenuHandler getPlayerMenuUtility(Player player) {
        if (!(playerMenuHandlerMap.containsKey(player))) {
            PlayerMenuHandler playerMenuHandler = new PlayerMenuHandler(player);
            playerMenuHandlerMap.put(player, playerMenuHandler);
            return playerMenuHandler;
        } else {
            return playerMenuHandlerMap.get(player);
        }
    }
}

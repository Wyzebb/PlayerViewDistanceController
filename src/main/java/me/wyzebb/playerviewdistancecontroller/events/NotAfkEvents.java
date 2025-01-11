package me.wyzebb.playerviewdistancecontroller.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class NotAfkEvents implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        plugin.updateLastMoved(event.getPlayer());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        plugin.updateLastMoved(event.getPlayer());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.updateLastMoved(event.getPlayer());
    }
}
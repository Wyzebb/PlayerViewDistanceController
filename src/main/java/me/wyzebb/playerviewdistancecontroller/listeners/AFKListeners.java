package me.wyzebb.playerviewdistancecontroller.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class AFKListeners implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        plugin.updateLastMoved(event.getPlayer());
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        Vehicle vehicle = event.getVehicle();
        vehicle.getPassengers().forEach(passenger -> {
           if (passenger instanceof Player player) {
               plugin.updateLastMoved(player);
           }
        });
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
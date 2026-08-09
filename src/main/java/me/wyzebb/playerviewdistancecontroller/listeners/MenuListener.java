package me.wyzebb.playerviewdistancecontroller.listeners;

import me.wyzebb.playerviewdistancecontroller.menu.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {
    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        InventoryHolder holder = e.getInventory().getHolder();
        if (holder instanceof Menu menu) {
            e.setCancelled(true);

            if (e.getCurrentItem() != null) {
                menu.handleMenu(e);
            }
        }
    }
}

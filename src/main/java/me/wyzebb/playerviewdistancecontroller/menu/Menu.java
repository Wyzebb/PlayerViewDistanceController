package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class Menu implements InventoryHolder {
    protected PlayerMenuHandler playerMenuHandler;
    protected Inventory inventory;

    public Menu(PlayerMenuHandler playerMenuHandler) {
        this.playerMenuHandler = playerMenuHandler;
    }

    public abstract String getTitle();

    public abstract int getTotalSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open() {
        inventory = Bukkit.createInventory(this, getTotalSlots(), getTitle());

        this.setMenuItems();

        playerMenuHandler.getPlayer().openInventory(inventory);
    }

    // Overridden from the InventoryHolder interface
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack makeItem(Material material, String displayName, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);
        itemMeta.setLore(List.of(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

}

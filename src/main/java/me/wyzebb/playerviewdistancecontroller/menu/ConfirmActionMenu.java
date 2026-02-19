package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ConfirmActionMenu extends Menu {
    private final String actionName;
    private final Runnable onConfirm;
    public ConfirmActionMenu(PlayerMenuHandler playerMenuHandler, String actionName, Runnable onConfirm) {
        super(playerMenuHandler);
        this.actionName = actionName;
        this.onConfirm = onConfirm;
    }

    public String getTitle() {
        return "Confirm Action";
    }

    @Override
    public int getTotalSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getCurrentItem().getType()){
            case GREEN_WOOL:
                e.getWhoClicked().closeInventory();
                onConfirm.run();
                break;
            case BARRIER:
                // Return to the previous menu
                new PlayerSelectionMenu(playerMenuHandler).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        ItemStack confirmItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta confirmItemMeta = confirmItem.getItemMeta();
        confirmItemMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        confirmItemMeta.setLore(List.of(actionName));
        confirmItem.setItemMeta(confirmItemMeta);

        ItemStack cancelItem = new ItemStack(Material.BARRIER, 1);
        ItemMeta cancelItemMeta = cancelItem.getItemMeta();
        cancelItemMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancelItem.setItemMeta(cancelItemMeta);

        inventory.setItem(3, confirmItem);
        inventory.setItem(5, cancelItem);
    }
}

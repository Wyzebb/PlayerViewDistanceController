package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public abstract class PaginatedMenu extends Menu {
    protected int maxItemsPerPage = 45;
    protected int page = 0;
    protected int slotI = 0;

    public PaginatedMenu(PlayerMenuHandler playerMenuHandler) {
        super(playerMenuHandler);
    }

    public void addPageControls(){
        inventory.setItem(47, makeItem(Material.JIGSAW, ChatColor.GREEN + "Back", ""));
        inventory.setItem(49, makeItem(Material.BARRIER, ChatColor.RED + "Close", ""));
        inventory.setItem(51, makeItem(Material.JIGSAW, ChatColor.GREEN + "Next", ""));
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}

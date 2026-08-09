package me.wyzebb.playerviewdistancecontroller.menu;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class NumberSelectionMenu extends Menu {
    private final Option option;

    public NumberSelectionMenu(PlayerMenuHandler playerMenuHandler, Option option) {
        super(playerMenuHandler);
        this.option = option;
    }

    public String getTitle() {
        return "Select Number";
    }

    @Override
    public int getTotalSlots() {
        return 18;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getCurrentItem().getType()){
            case GREEN_WOOL:
                e.getWhoClicked().closeInventory();

                switch (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                    case ("-1"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() - 1);
                        break;
                    case ("-3"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() - 3);
                        break;
                    case ("-5"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() - 5);
                        break;
                    case ("+1"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() + 1);
                        break;
                    case ("+3"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() + 3);
                        break;
                    case ("+5"):
                        playerMenuHandler.setCurrentValue(playerMenuHandler.getCurrentValue() + 5);
                        break;
                }
                break;
            case BARRIER:
                // Return to the previous menu
                new PlayerSelectionMenu(playerMenuHandler).open();
                break;
            case LIME_CONCRETE:
                this.inventory.close();
                Storage.setChunksToWorld(playerMenuHandler.getPlayer(), playerMenuHandler.getWorldId(), option.getName(), playerMenuHandler.getCurrentValue());
                break;
        }
    }

    @Override
    public void setMenuItems() {
        final ItemStack n1Arrow = makeItem(Material.RED_WOOL, (ChatColor.RED + "-1"), "");
        final ItemStack n3Arrow = makeItem(Material.RED_WOOL, (ChatColor.RED + "-3"), "");
        final ItemStack n5Arrow = makeItem(Material.RED_WOOL, (ChatColor.RED + "-5"), "");
        final ItemStack p1Arrow = makeItem(Material.GREEN_WOOL, (ChatColor.GREEN + "+1"), "");
        final ItemStack p3Arrow = makeItem(Material.GREEN_WOOL, (ChatColor.GREEN + "+3"), "");
        final ItemStack p5Arrow = makeItem(Material.GREEN_WOOL, (ChatColor.GREEN + "+5"), "");

        inventory.setItem(0, n5Arrow);
        inventory.setItem(1, n3Arrow);
        inventory.setItem(2, n1Arrow);
        inventory.setItem(4, option.getItem());
        inventory.setItem(6, p1Arrow);
        inventory.setItem(7, p3Arrow);
        inventory.setItem(8, p5Arrow);

        final ItemStack confirmItem = makeItem(Material.LIME_CONCRETE, (ChatColor.GREEN + "Confirm"), option.getName());
        final ItemStack cancelItem = makeItem(Material.BARRIER, (ChatColor.RED + "Cancel"), "");

        inventory.setItem(13, confirmItem);
        inventory.setItem(17, cancelItem);
    }
}
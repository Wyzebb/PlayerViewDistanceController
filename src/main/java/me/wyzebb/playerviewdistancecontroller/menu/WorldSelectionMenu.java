package me.wyzebb.playerviewdistancecontroller.menu;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class WorldSelectionMenu extends PaginatedMenu {
    private Option option;

    public WorldSelectionMenu(PlayerMenuHandler playerMenuHandler, Option option) {
        super(playerMenuHandler);
        this.option = option;
    }

    public String getTitle() {
        return "Select World";
    }

    @Override
    public int getTotalSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem().getType() == Material.LIME_WOOL) {
            e.getWhoClicked().closeInventory();
            playerMenuHandler.setWorldId(Bukkit.getWorld(e.getCurrentItem().getItemMeta().getDisplayName()).getUID().toString());

            playerMenuHandler.setCurrentValue(option.getValues().get(e.getCurrentItem().getItemMeta().getDisplayName()));
            new NumberSelectionMenu(playerMenuHandler, option).open();
        }
    }

    @Override
    public void setMenuItems() {
        addPageControls();
        ArrayList<Option> options = getOptions();

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            slotI = getMaxItemsPerPage() * page + i;
            if (slotI >= options.size()) break;

            inventory.setItem(i, makeItem(Material.LIME_WOOL, options.get(i).getName(), "Click to select"));
        }
    }

    private ArrayList<Option> getOptions() {
        ArrayList<Option> options = new ArrayList<>();

        ArrayList<String> worlds = Storage.getWorlds(playerMenuHandler.getPlayer());

        for (String world : worlds) {
            options.add(new Option(world, Material.LIME_WOOL, new HashMap<>(), Option.OptionType.INTEGER));
        }

        return options;
    }
}

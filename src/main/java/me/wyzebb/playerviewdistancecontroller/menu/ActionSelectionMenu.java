package me.wyzebb.playerviewdistancecontroller.menu;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ActionSelectionMenu extends Menu {
    public ActionSelectionMenu(PlayerMenuHandler playerMenuHandler) {
        super(playerMenuHandler);
    }

    public String getTitle() {
        return "Select Action";
    }

    @Override
    public int getTotalSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (e.getCurrentItem().getType() == Material.GREEN_WOOL) {
            e.getWhoClicked().closeInventory();

            if (Option.OptionType.valueOf(e.getCurrentItem().getItemMeta().getLore().getLast()) == Option.OptionType.INTEGER) {
                if (plugin.getPluginConfig().isWorldIndependent()) {
                    new WorldSelectionMenu(playerMenuHandler, getOptions().get(e.getSlot()));
                } else {
                    final int chunks = Storage.getColumn(playerMenuHandler.getPlayer(), e.getCurrentItem().getItemMeta().getDisplayName());
                    playerMenuHandler.setCurrentValue(chunks);
                    new NumberSelectionMenu(playerMenuHandler, getOptions().get(e.getSlot()));
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        ArrayList<Option> options = getOptions();

        for (int i = 0; i < options.size(); i++) {
            inventory.setItem(i, makeItem(Material.LIME_WOOL, options.get(i).getName(), "Click to change"));
        }
    }

    private ArrayList<Option> getOptions() {
        ArrayList<Option> options = new ArrayList<>();

        options.add(new Option("vd", Material.LIME_WOOL, Storage.getIntegersAndWorlds(playerMenuHandler.getPlayer(), "vd"), Option.OptionType.INTEGER));
        options.add(new Option("vdAdmin", Material.RED_WOOL, Storage.getIntegersAndWorlds(playerMenuHandler.getPlayer(), "vdAdmin"), Option.OptionType.INTEGER));

        return options;
    }
}

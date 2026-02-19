package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class Option {
    private final String name;
    private final ItemStack item;
    private HashMap<String, Integer> values;

    public Option(String name, Material material, HashMap<String, Integer> values) {
        this.name = name;
        this.item = new ItemStack(material);
        this.values = values;

        updateLore();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public HashMap<String, Integer> getValues() {
        return values;
    }

    public void setValues(HashMap<String, Integer> values) {
        this.values = values;
        updateLore();
    }

    private void updateLore() {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        lore.add("Click to modify");
        values.forEach((world, value) -> {
            lore.add(world + ": " + value.toString());
        });

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
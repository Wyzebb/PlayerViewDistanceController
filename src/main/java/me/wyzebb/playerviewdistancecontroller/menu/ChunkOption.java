package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ChunkOption {
    private final String name;
    private final ItemStack item;
    private int value;
    private final Option.OptionType type;

    public ChunkOption(String name, Material material, int value, Option.OptionType type) {
        this.name = name;
        this.item = new ItemStack(material);
        this.value = value;
        this.type = type;

        updateLore();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public int getValue() {
        return value;
    }

    public Option.OptionType getType() {
        return type;
    }

    public void setValues(int value) {
        this.value = value;
        updateLore();
    }

    private void updateLore() {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        lore.add("Click to modify");
        lore.add(String.valueOf(value));

        lore.add(type.toString());

        meta.setLore(lore);
        item.setItemMeta(meta);
    }
}
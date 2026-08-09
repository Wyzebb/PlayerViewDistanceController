package me.wyzebb.playerviewdistancecontroller.menu;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Option {
    private final String name;
    private final ItemStack item;
    private Map<String, Integer> values;
    private final OptionType type;

    public Option(String name, Material material, Map<String, Integer> values, OptionType type) {
        this.name = name;
        this.item = new ItemStack(material);

        final ItemMeta itemMeta = this.item.getItemMeta();
        itemMeta.displayName(Component.text(name));
        this.item.setItemMeta(itemMeta);

        this.values = values;
        this.type = type;

        updateLore();
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public Map<String, Integer> getValues() {
        return values;
    }

    public OptionType getType() {
        return type;
    }

    public void setValues(HashMap<String, Integer> values) {
        this.values = values;
        updateLore();
    }

    public ItemStack createCurrentValueItem(int currentValue) {
        final ItemStack currentValueItem = item.clone();
        currentValueItem.setAmount(currentValue);
        final ItemMeta currentValueItemMeta = currentValueItem.getItemMeta();
        currentValueItemMeta.displayName(Component.text(currentValue));
        currentValueItem.setItemMeta(currentValueItemMeta);
        return currentValueItem;
    }

    private void updateLore() {
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> lore = new ArrayList<>();

        lore.add("Click to modify");
        values.forEach((world, value) -> lore.add(world + ": " + value.toString()));

        lore.add(type.toString());

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public enum OptionType {
        BOOLEAN,
        INTEGER
    }
}
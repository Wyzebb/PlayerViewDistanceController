package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Collection;
import java.util.List;

public class MenuCommand extends SubCommand {
    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (commandSender instanceof Player player) {
            Inventory menu = Bukkit.createInventory(player, 27, "PVDC Menu");

            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
            int size = onlinePlayers.size();

            if (size > 26) {
                // New pages
            }

            for (int i = 0; i < 18; i++) {
                ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) skull.getItemMeta();
                meta.setOwningPlayer(player);
                skull.setItemMeta(meta);
            }

            ItemStack infiniteGame = new ItemStack(Material.JIGSAW);
            ItemStack generateTrack = new ItemStack(Material.RED_WOOL);

            ItemMeta infiniteGameMeta = infiniteGame.getItemMeta();
            infiniteGameMeta.setDisplayName(ChatColor.BLUE + "Infinite Game");
            infiniteGameMeta.setLore(List.of("Begin infinite track generation"));
            infiniteGame.setItemMeta(infiniteGameMeta);

            ItemMeta generateTrackMeta = generateTrack.getItemMeta();
            generateTrackMeta.setDisplayName(ChatColor.BLUE + "Generate Track");
            generateTrackMeta.setLore(List.of("Instantly generate track"));
            generateTrack.setItemMeta(generateTrackMeta);

            menu.setItem(11, infiniteGame);
            menu.setItem(15, generateTrack);

            player.openInventory(menu);
        } else {
            commandSender.sendMessage("This command can only be run by a player.");
        }
    }

    @Override
    public String getName() {
        return "menu";
    }

    @Override
    public String getDescription() {
        return "Opens SlipStream menu";
    }

    @Override
    public String getSyntax() {
        return "/slipstream menu";
    }
}
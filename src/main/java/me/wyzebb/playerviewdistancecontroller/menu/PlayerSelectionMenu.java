package me.wyzebb.playerviewdistancecontroller.menu;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PlayerSelectionMenu extends PaginatedMenu {
    public PlayerSelectionMenu(PlayerMenuHandler playerMenuHandler) {
        super(playerMenuHandler);
    }

    @Override
    public String getTitle() {
        return "Select a Player";
    }

    @Override
    public int getTotalSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ArrayList<Player> players = new ArrayList<>(getServer().getOnlinePlayers());

        if (event.getCurrentItem().getType().equals(Material.PLAYER_HEAD)) {
            PlayerMenuHandler playerMenuHandler = MenuHandlerUtility.getPlayerMenuUtility(player);

            final List<String> lore = event.getCurrentItem().getItemMeta().getLore();
            if (lore != null && !lore.isEmpty()) {
                try {
                    Player target = Bukkit.getPlayer(UUID.fromString(lore.get(0)));
                    if (target != null) {
                        playerMenuHandler.setTargetPlayer(target);
                        new ActionSelectionMenu(playerMenuHandler).open();
                    } else {
                        player.sendMessage(ChatColor.RED + "Player is no longer online.");
                    }
                } catch (IllegalArgumentException e) {
                    player.sendMessage(ChatColor.RED + "Invalid player data.");
                }
            }

        } else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.closeInventory();

        } else if (event.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            final String displayName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
            
            if (displayName.equals("Back")) {
                if (page == 0) {
                    player.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                } else {
                    page -= 1;
                    super.open();
                }
            } else if (displayName.equals("Next")) {
                if (!((slotI + 1) >= players.size())) {
                    page += 1;
                    super.open();
                } else {
                    player.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        addPageControls();
        ArrayList<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        for (int i = 0; i < getMaxItemsPerPage(); i++) {
            slotI = getMaxItemsPerPage() * page + i;
            if (slotI >= players.size()) break;

            final PlayerProfile profile = Bukkit.createProfileExact(players.get(slotI).getUniqueId(), players.get(slotI).getName());
            final ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
            final SkullMeta skullMeta = (SkullMeta) playerItem.getItemMeta();
            skullMeta.setDisplayName(ChatColor.AQUA + players.get(slotI).getDisplayName());
            skullMeta.setLore(List.of(players.get(slotI).getUniqueId().toString()));
            skullMeta.setPlayerProfile(profile);
            playerItem.setItemMeta(skullMeta);

            inventory.addItem(playerItem);
        }
    }
}

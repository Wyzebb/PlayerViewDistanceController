package me.wyzebb.playerviewdistancecontroller.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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
            playerMenuHandler.setTargetPlayer(Bukkit.getPlayer(UUID.fromString(event.getCurrentItem().getItemMeta().getLore().get(0))));
            new ActionSelectionMenu(playerMenuHandler).open();

        } else if (event.getCurrentItem().getType().equals(Material.BARRIER)) {
            player.closeInventory();

        } else if (event.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Left")) {
                if (page == 0){
                    player.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                } else {
                    page = page - 1;
                    super.open();
                }
            }else if (event.getCurrentItem().getItemMeta().getDisplayName().equals("Right")){
                if (!((slotI + 1) >= players.size())){
                    page = page + 1;
                    super.open();
                }else{
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

            ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta playerItemMeta = playerItem.getItemMeta();
            playerItemMeta.setDisplayName(ChatColor.AQUA + players.get(slotI).getDisplayName());
            playerItemMeta.setLore(List.of(players.get(slotI).getUniqueId().toString()));
            playerItem.setItemMeta(playerItemMeta);

            inventory.addItem(playerItem);
        }
    }
}

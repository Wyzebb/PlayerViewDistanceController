package me.wyzebb.playerviewdistancecontroller.menu;

import me.wyzebb.playerviewdistancecontroller.data.Storage;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.lang.MessageType;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ViewDistanceUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        final Material material = e.getCurrentItem().getType();
        final int currentValue = playerMenuHandler.getCurrentValue();

        if (material == Material.GREEN_WOOL || material == Material.RED_WOOL) {
            boolean matched = true;
            int changeTo = 0;
            switch (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName())) {
                case ("-1"):
                    changeTo = currentValue - 1;
                    break;
                case ("-3"):
                    changeTo = currentValue - 3;
                    break;
                case ("-5"):
                    changeTo = currentValue - 5;
                    break;
                case ("+1"):
                    changeTo = currentValue + 1;
                    break;
                case ("+3"):
                    changeTo = currentValue + 3;
                    break;
                case ("+5"):
                    changeTo = currentValue + 5;
                    break;
                default:
                    matched = false;
                    break;
            }
            if (matched) {
                playerMenuHandler.setCurrentValue(ClampAmountUtility.clampChunkValue(changeTo));
                e.getInventory().setItem(4, option.createCurrentValueItem(playerMenuHandler.getCurrentValue()));
            }

        } else if (material == Material.BARRIER) {
            // Return to the previous menu
            new PlayerSelectionMenu(playerMenuHandler).open();
        } else if (material == Material.LIME_CONCRETE) {
            this.inventory.close();
            Storage.setChunksToWorld(playerMenuHandler.getPlayer(), playerMenuHandler.getWorldId(), option.getName(), currentValue);

            if (e.getWhoClicked() == playerMenuHandler.getTargetPlayer()) {
                final ViewDistanceCalculationContext ctx = ViewDistanceContextFactory.createCommandContext(playerMenuHandler.getTargetPlayer(), currentValue);

                final ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(ctx);
                final int appliedAmount = result.getViewDistance();

                MessageProcessor.processMessage("target-view-distance-change", MessageType.SUCCESS, playerMenuHandler.getTargetPlayer(), appliedAmount, e.getWhoClicked());
            }
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
        inventory.setItem(4, option.createCurrentValueItem(playerMenuHandler.getCurrentValue()));
        inventory.setItem(6, p1Arrow);
        inventory.setItem(7, p3Arrow);
        inventory.setItem(8, p5Arrow);

        final ItemStack confirmItem = makeItem(Material.LIME_CONCRETE, (ChatColor.GREEN + "Confirm"), option.getName());
        final ItemStack cancelItem = makeItem(Material.BARRIER, (ChatColor.RED + "Cancel"), "");

        inventory.setItem(13, confirmItem);
        inventory.setItem(17, cancelItem);
    }
}
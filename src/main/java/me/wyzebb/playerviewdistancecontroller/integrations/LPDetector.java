package me.wyzebb.playerviewdistancecontroller.integrations;

import org.bukkit.OfflinePlayer;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LPDetector {
    public static int getLuckpermsDistance(OfflinePlayer player) {
        try {
            Class.forName("net.luckperms.api.LuckPerms"); // Use reflection to check if LuckPerms is available
            return LPMaxDistanceHandler.getLuckpermsDistance(player);
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while accessing LuckPerms data: " + e.getMessage());
            return 32; // Return default distance if LuckPerms is not available
        }
    }

    public static boolean initialLuckPermsCheck() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            plugin.getLogger().info("Enabling LuckPerms Hook");
            return true;

        } catch (Exception ex) {
            plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but recommended!");
            return false;
        }
    }
}

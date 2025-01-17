package me.wyzebb.playerviewdistancecontroller.data;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsDetector {
    public static boolean detectLuckPerms() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            plugin.getLogger().info("Enabling LuckPerms Hook");
            return true;

        } catch (Exception ex) {
            plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but it extends the plugin's functionality!");
            return false;
        }
    }
}

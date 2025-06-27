package me.wyzebb.playerviewdistancecontroller.data;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsDetector {
    public static boolean detectLuckPermsWithMsg() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            plugin.getLogger().info("Enabling LuckPerms Hook");
            return true;

        } catch (Exception ex) {
            plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but recommended!");
            return false;
        }
    }

    public static boolean detectLuckPerms() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
            return true;

        } catch (Exception ex) {
            return false;
        }
    }
}

package me.wyzebb.playerviewdistancecontroller;

import org.bukkit.plugin.java.JavaPlugin;

public final class PlayerViewDistanceController extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");

        // Config
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Register join and leave events
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(this), this);

        // Register Commands
        getCommand("viewdistance").setExecutor(new ViewDistanceCommand(this));

    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin shut down!");
    }
}

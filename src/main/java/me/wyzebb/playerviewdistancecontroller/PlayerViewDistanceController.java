package me.wyzebb.playerviewdistancecontroller;

import me.wyzebb.playerviewdistancecontroller.commands.GlobalViewDistanceCommand;
import me.wyzebb.playerviewdistancecontroller.commands.ViewDistanceCommand;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
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
        getCommand("globalviewdistance").setExecutor(new GlobalViewDistanceCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin shut down!");
    }
}

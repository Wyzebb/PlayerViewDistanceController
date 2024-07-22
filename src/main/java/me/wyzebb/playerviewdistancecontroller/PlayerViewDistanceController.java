package me.wyzebb.playerviewdistancecontroller;

import me.wyzebb.playerviewdistancecontroller.commands.GlobalViewDistanceCommand;
import me.wyzebb.playerviewdistancecontroller.commands.ViewDistanceCommand;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class PlayerViewDistanceController extends JavaPlugin {
    private File prefixesConfigFile;
    private FileConfiguration prefixesConfig;

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");

        // Config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        createPrefixesConfig();

        // Register join and leave events
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(this), this);

        // Register Commands
        getCommand("viewdistance").setExecutor(new ViewDistanceCommand(this));
        getCommand("globalviewdistance").setExecutor(new GlobalViewDistanceCommand(this));
    }

    public FileConfiguration getPrefixesConfig() {
        return this.prefixesConfig;
    }

    private void createPrefixesConfig() {
        prefixesConfigFile = new File(getDataFolder(), "prefixes.yml");
        if (!prefixesConfigFile.exists()) {
            prefixesConfigFile.getParentFile().mkdirs();
            saveResource("prefixes.yml", false);
        }

        prefixesConfig = YamlConfiguration.loadConfiguration(prefixesConfigFile);
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin shut down!");
    }
}

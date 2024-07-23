package me.wyzebb.playerviewdistancecontroller;

import me.wyzebb.playerviewdistancecontroller.commands.BaseViewDistanceCommand;
import me.wyzebb.playerviewdistancecontroller.commands.SetOnlineSubCommand;
import me.wyzebb.playerviewdistancecontroller.commands.SetSubCommand;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class PlayerViewDistanceController extends JavaPlugin {
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
        BaseViewDistanceCommand baseCommand = new BaseViewDistanceCommand(this);
        getCommand("viewdistance").setExecutor(baseCommand);
        baseCommand.registerCommand("set", new SetSubCommand(this));
        baseCommand.registerCommand("setonline", new SetOnlineSubCommand(this));
    }

    public FileConfiguration getPrefixesConfig() {
        return this.prefixesConfig;
    }

    private void createPrefixesConfig() {
        File prefixesConfigFile = new File(getDataFolder(), "prefixes.yml");
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

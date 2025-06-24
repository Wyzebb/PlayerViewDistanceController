package me.wyzebb.playerviewdistancecontroller.utility;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ConfigLoader {

    private final JavaPlugin plugin;

    public ConfigLoader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public FileConfiguration loadAndMerge(String fileName) {
        File configFile = new File(plugin.getDataFolder(), fileName);

        // Save the default file from JAR if it doesn't exist
        if (!configFile.exists()) {
            plugin.saveResource(fileName, false);
        }

        // Load current config
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        // Load default config from inside JAR
        try (InputStreamReader reader = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8)) {
            YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
            config.setDefaults(defaults);
            config.options().copyDefaults(true);
        } catch (Exception e) {
            plugin.getLogger().warning("Could not load defaults for " + fileName + ": " + e.getMessage());
        }

        // Save updated config with any missing keys
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save " + fileName + ": " + e.getMessage());
        }

        return config;
    }
}

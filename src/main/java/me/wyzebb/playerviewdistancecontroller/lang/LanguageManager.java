package me.wyzebb.playerviewdistancecontroller.lang;

import com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LanguageManager {
    private final Map<String, FileConfiguration> languages = new HashMap<>();
    private String defaultLanguage;

    public LanguageManager() {
        loadConfig();
        copyDefaultLanguages();
        loadLanguages();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();
        defaultLanguage = config.getString("language", "en_US"); // Defaults to "en_US" if not set
    }

    private void copyDefaultLanguages() {
        String[] languages = {"en_US.yml", "ru_RU.yml", "zh_CN.yml"};

        File languagesFolder = new File(plugin.getDataFolder(), "lang");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();
        }

        for (String langFileName : languages) {
            File langFile = new File(plugin.getDataFolder(), ("lang/" + langFileName));

            try {
                if (!langFile.exists()) {
                    langFile.createNewFile();
                    ConfigUpdater.update(plugin, ("lang/" + langFileName), langFile);
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create lang file '" + langFileName + "'!");
            }
        }
    }

    private void loadLanguages() {
        File languagesFolder = new File(plugin.getDataFolder(), "lang");
        File[] files = languagesFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    String lang = file.getName().replace(".yml", "");
                    FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                    languages.put(lang, config);
                }
            }
        }
    }

    public FileConfiguration getLanguageFile() {
        return languages.getOrDefault(defaultLanguage, languages.get("en_US")); // Default to English if language not found
    }
}
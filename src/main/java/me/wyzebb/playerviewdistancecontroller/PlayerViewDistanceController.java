package me.wyzebb.playerviewdistancecontroller;

import com.tchristofferson.configupdater.ConfigUpdater;
import com.tcoded.folialib.FoliaLib;
import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.integrations.PlaceholderAPIExpansion;
import me.wyzebb.playerviewdistancecontroller.listeners.UpdateVDListeners;
import me.wyzebb.playerviewdistancecontroller.listeners.LuckPermsListeners;
import me.wyzebb.playerviewdistancecontroller.listeners.AFKListeners;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import net.luckperms.api.LuckPerms;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerViewDistanceController extends JavaPlugin {
    public static PlayerViewDistanceController plugin;
    public static final Map<UUID, Integer> playerAfkMap = new HashMap<>();

    private FileConfiguration pingOptimiserConfig;
    private FileConfiguration dynamicModeConfig;

    private final FoliaLib foliaLib = new FoliaLib(this);

    private LanguageManager languageManager;

    public static boolean luckPermsDetected = false;

    public static boolean dynamicModeEnabled = false;
    public static int dynamicReducedChunks = 0;

    public static boolean pingModeDisabled = true;

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");
        plugin = this;

        // Config
        saveDefaultConfig();

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        File dynamicConfigFile = new File(getDataFolder(), "dynamic-mode.yml");
        File pingConfigFile = new File(getDataFolder(), "ping-mode.yml");

        try {
            if (!configFile.exists()) configFile.createNewFile();
            if (!dynamicConfigFile.exists()) dynamicConfigFile.createNewFile();
            if (!pingConfigFile.exists()) pingConfigFile.createNewFile();
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create config files!");
        }

        try {
            ConfigUpdater.update(plugin, "config.yml", configFile);
            ConfigUpdater.update(plugin, "dynamic-mode.yml", dynamicConfigFile, "mspt");
            ConfigUpdater.update(plugin, "ping-mode.yml", pingConfigFile, "pings");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to update config files!");
        }

        reloadConfig();

        dynamicModeConfig = YamlConfiguration.loadConfiguration(dynamicConfigFile);
        pingOptimiserConfig = YamlConfiguration.loadConfiguration(pingConfigFile);

        dynamicModeEnabled = dynamicModeConfig.getBoolean("enabled");


        int pluginId = 24498;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("used_language", () -> getConfig().getString("language", "en_US")));

        languageManager = new LanguageManager();

        luckPermsDetected = LPDetector.initialLuckPermsCheck();

        if (luckPermsDetected) {
            LuckPerms luckPerms;
            try {
                luckPerms = getServer().getServicesManager().load(LuckPerms.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            new LuckPermsListeners(luckPerms).register();
        }

        // Register join and leave listeners
        getServer().getPluginManager().registerEvents(new UpdateVDListeners(), this);
        getServer().getPluginManager().registerEvents(new AFKListeners(), this);

        // Register commands and tab completer
        Objects.requireNonNull(getCommand("pvdc")).setExecutor(new CommandManager());
        Objects.requireNonNull(getCommand("pvdc")).setTabCompleter(new CommandManager());

        // Check for updates if enabled in the config
        if (getConfig().getBoolean("update-checker-enabled")) {
            UpdateChecker updateChecker = new UpdateChecker();
            Thread updateCheck = new Thread(updateChecker, "Update Check Thread");
            updateCheck.start();
        }

        // Start AFK checker if enabled in the config
        if (getConfig().getBoolean("afk-chunk-limiter")) {
            scheduleAfkChecker();
        }

        if (getPingOptimiserConfig().getBoolean("enabled")) {
            startPingOptimiser();
            plugin.getLogger().warning("Ping mode enabled");
        } else {
            plugin.getLogger().warning("Ping mode disabled");
        }

        if (getDynamicModeConfig().getBoolean("enabled")) {
            startDynamicMode();
            plugin.getLogger().warning("Dynamic mode enabled");
        } else {
            plugin.getLogger().warning("Dynamic mode disabled");
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().info("Enabling PlaceholderAPI Hook");
            PlaceholderAPIExpansion.registerHook();
        }
    }

    public FileConfiguration getPingOptimiserConfig() {
        return this.pingOptimiserConfig;
    }

    public FileConfiguration getDynamicModeConfig() {
        return this.dynamicModeConfig;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }


    public void updateLastMoved(Player player) {
        final UUID playerId = player.getUniqueId();

        if (playerAfkMap.containsKey(playerId)) {
            if (playerAfkMap.get(playerId) == 0) {
                MessageProcessor.processMessage("messages.afk-return", 2, 0, player);
                VdCalculator.calcVdSet(Bukkit.getPlayer(playerId), true, true, false);
            }
        }

        playerAfkMap.put(playerId, (int) System.currentTimeMillis());
    }

    private void scheduleAfkChecker() {
        foliaLib.getScheduler().runTimer(this::checkAfk, 0, 20);
    }

    private void startPingOptimiser() {
        pingModeDisabled = false;
        foliaLib.getScheduler().runTimer(PingModeHandler::optimisePingPerPlayer, 0, getPingOptimiserConfig().getInt("interval"));
    }

    public void startDynamicMode() {
        DynamicModeHandler.checkServerMSPT();
        foliaLib.getScheduler().runTimer(DynamicModeHandler::checkServerMSPT, 0, getDynamicModeConfig().getInt("interval"));
    }

    public void stopDynamicMode() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            VdCalculator.calcVdSet(player, true, false, false);
        }
    }

    public void stopPingMode() {
        pingModeDisabled = true;
        for (Player player: Bukkit.getOnlinePlayers()) {
            VdCalculator.calcVdSet(player, true, false, false);
        }
    }

    private void checkAfk() {
        int currentTime = (int) System.currentTimeMillis();
        for (Player player : Bukkit.getOnlinePlayers()) {
            final UUID playerId = player.getUniqueId();
            int lastMoved = playerAfkMap.getOrDefault(playerId, currentTime);

            if ((currentTime - lastMoved > (getConfig().getInt("afkTime")) * 1000) && playerAfkMap.get(playerId) != 0) {
                if (getConfig().getBoolean("spectators-can-afk") && player.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }

                if (player.hasPermission("pvdc.bypass-afk")) {
                    continue;
                }

                int afkChunks = 0;

                if (!plugin.getConfig().getBoolean("zero-chunks-afk")) {
                    afkChunks = ClampAmountUtility.clampChunkValue(plugin.getConfig().getInt("afkChunks"));
                }

                player.setViewDistance(afkChunks);

                if (plugin.getConfig().getBoolean("sync-simulation-distance")) {
                    player.setSimulationDistance(afkChunks);
                }

                playerAfkMap.put(playerId, 0);

                MessageProcessor.processMessage("messages.afk", 3, afkChunks, player);
            }
        }
    }

    public static boolean isPlayerDataSavingEnabled() {
        return plugin.getConfig().getBoolean("save-player-data", true);
    }

    @Override
    public void onDisable() {
        playerAfkMap.clear();

        File dynamicModeConfigFile = new File(getDataFolder(), "dynamic-mode.yml");
        YamlConfiguration dynamicConfig = YamlConfiguration.loadConfiguration(dynamicModeConfigFile);
        dynamicConfig.set("enabled", dynamicModeEnabled);

        try {
            dynamicConfig.save(dynamicModeConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("Plugin shut down!");
    }
}
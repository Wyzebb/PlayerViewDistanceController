package me.wyzebb.playerviewdistancecontroller;

import com.tchristofferson.configupdater.ConfigUpdater;
import com.tcoded.folialib.FoliaLib;
import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.config.PluginConfig;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceCalculationContext;
import me.wyzebb.playerviewdistancecontroller.data.ViewDistanceContextFactory;
import me.wyzebb.playerviewdistancecontroller.integrations.ClientViewDistanceTracker;
import me.wyzebb.playerviewdistancecontroller.integrations.LPDetector;
import me.wyzebb.playerviewdistancecontroller.integrations.PlaceholderAPIExpansion;
import me.wyzebb.playerviewdistancecontroller.listeners.UpdateVDListeners;
import me.wyzebb.playerviewdistancecontroller.listeners.LuckPermsListeners;
import me.wyzebb.playerviewdistancecontroller.listeners.AFKListeners;
import me.wyzebb.playerviewdistancecontroller.utility.*;
import me.wyzebb.playerviewdistancecontroller.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.lang.MessageProcessor;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import me.wyzebb.playerviewdistancecontroller.state.PlayerStateManager;
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
import java.util.Objects;
import java.util.UUID;

public final class PlayerViewDistanceController extends JavaPlugin {
    public static PlayerViewDistanceController plugin;

    private PlayerStateManager stateManager;

    private FileConfiguration pingOptimiserConfig;
    private FileConfiguration dynamicModeConfig;

    private final FoliaLib foliaLib = new FoliaLib(this);

    private LanguageManager languageManager;

    public static boolean luckPermsDetected = false;

    public static boolean dynamicModeEnabled = false;
    public static int dynamicReducedChunks = 0;

    public static boolean pingModeDisabled = true;

    private PluginConfig pluginConfig;

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

        this.pluginConfig = new PluginConfig(getConfig());

        // Initialize state manager
        stateManager = new PlayerStateManager(this);


        int pluginId = 24498;
        Metrics metrics = new Metrics(this, pluginId);
        metrics.addCustomChart(new SimplePie("used_language", () -> getPluginConfig().getLanguage()));

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
        if (getPluginConfig().isUpdateCheckerEnabled()) {
            UpdateChecker updateChecker = new UpdateChecker();
            Thread updateCheck = new Thread(updateChecker, "Update Check Thread");
            updateCheck.start();
        }

        // Start AFK checker if enabled in the config
        if (getPluginConfig().isAfkChunkLimiterEnabled()) {
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

        // Initialize client view distance tracking with PacketEvents
        try {
            ClientViewDistanceTracker.initialize();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to initialize client view distance tracking: " + e.getMessage());
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

    public PlayerStateManager getStateManager() {
        return stateManager;
    }

    public FoliaLib getFoliaLib() {
        return foliaLib;
    }


    public void updateLastMoved(Player player) {
        final UUID playerId = player.getUniqueId();

        // Update activity in state manager
        PlayerState previousState = stateManager.getPlayerState(playerId);
        stateManager.updatePlayerActivity(player);
        PlayerState currentState = stateManager.getPlayerState(playerId);

        // Handle state-based actions
        if (previousState == PlayerState.AFK && currentState == PlayerState.RETURNING_FROM_AFK) {
            // Player is returning from AFK
            MessageProcessor.processMessage("afk-return", 2, 0, player);

            // Apply any pending client view distance changes
            ClientViewDistanceTracker.applyPendingClientViewDistance(player);

            // Recalculate view distance with new state using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createReturnFromAfkContext(player);

            ViewDistanceUtility.applyOptimalViewDistance(context);

            // Complete transition to ACTIVE state
            foliaLib.getScheduler().runLater(() -> {
                if (player.isOnline()) {
                    stateManager.transitionState(player, PlayerState.ACTIVE);
                }
            }, 1L); // Next tick
        }
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
            // Build context for stopping dynamic mode using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createStopDynamicModeContext(player);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }

    public void stopPingMode() {
        pingModeDisabled = true;
        for (Player player: Bukkit.getOnlinePlayers()) {
            // Build context for stopping ping mode using factory
            ViewDistanceCalculationContext context = ViewDistanceContextFactory.createStopPingModeContext(player);

            ViewDistanceUtility.applyOptimalViewDistance(context);
        }
    }

    private void checkAfk() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (getPluginConfig().canSpectatorsAfk() && player.getGameMode() == GameMode.SPECTATOR) {
                continue;
            }

            // Check if player should be marked as AFK
            if (stateManager.shouldMarkAsAfk(player)) {
                // Transition to AFK state
                stateManager.transitionState(player, PlayerState.AFK);

                // Calculate and apply AFK view distance
                int afkChunks = 0;
                if (!plugin.getPluginConfig().isVoidAfkEnabled()) {
                    afkChunks = ClampAmountUtility.clampChunkValue(plugin.getPluginConfig().getAfkChunks());
                }

                // Build context for AFK transition using factory
                ViewDistanceCalculationContext context = ViewDistanceContextFactory.createAfkContext(player, afkChunks);

                ViewDistanceUtility.ViewDistanceResult result = ViewDistanceUtility.applyOptimalViewDistance(context);
                int appliedAfkChunks = result.getViewDistance();

                MessageProcessor.processMessage("afk", 3, appliedAfkChunks, player);
            }
        }
    }

    @Override
    public void onDisable() {
        // Cleanup client view distance tracking
        ClientViewDistanceTracker.shutdown();

        // Cleanup state manager
        if (stateManager != null) {
            stateManager.clearAllStates();
        }

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

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }
}
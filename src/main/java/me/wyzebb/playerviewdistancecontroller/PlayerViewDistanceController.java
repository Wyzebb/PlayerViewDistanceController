package me.wyzebb.playerviewdistancecontroller;

import com.tcoded.folialib.FoliaLib;
import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDetector;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.events.LuckPermsEvents;
import me.wyzebb.playerviewdistancecontroller.events.NotAfkEvents;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlaceholderAPIExpansion;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.lang.LanguageManager;
import me.wyzebb.playerviewdistancecontroller.utility.lang.MessageProcessor;
import net.luckperms.api.LuckPerms;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

    private final FoliaLib foliaLib = new FoliaLib(this);

    private LanguageManager languageManager;

    public static boolean luckPermsDetected = false;

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");
        plugin = this;


        int pluginId = 24498;
        Metrics metrics = new Metrics(this, pluginId);

        metrics.addCustomChart(new SimplePie("used_language", () -> {
            return getConfig().getString("language", "en_US");
        }));


        luckPermsDetected = LuckPermsDetector.detectLuckPermsWithMsg();

        languageManager = new LanguageManager();

        if (luckPermsDetected) {
            LuckPerms luckPerms;
            try {
                luckPerms = getServer().getServicesManager().load(LuckPerms.class);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            new LuckPermsEvents(luckPerms).register();
        }

        // Config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        // Register join and leave events
        getServer().getPluginManager().registerEvents(new JoinLeaveEvent(), this);
        getServer().getPluginManager().registerEvents(new NotAfkEvents(), this);

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

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            plugin.getLogger().info("Enabling PlaceholderAPI Hook");
            PlaceholderAPIExpansion.registerHook();
        }
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }


    public void updateLastMoved(Player player) {
        final UUID playerId = player.getUniqueId();

        if (playerAfkMap.containsKey(playerId)) {
            if (playerAfkMap.get(playerId) == 0) {
                PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);
                player.setViewDistance(dataHandler.getChunks());

                MessageProcessor.processMessage("messages.afk-return", 2, 0, player);
            }
        }

        playerAfkMap.put(playerId, (int) System.currentTimeMillis());
    }

    private void scheduleAfkChecker() {
        foliaLib.getScheduler().runTimer(this::checkAfk, 0, 20);
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

                int afkChunks = ClampAmountUtility.clampChunkValue(getConfig().getInt("afkChunks"));

                player.setViewDistance(afkChunks);
                playerAfkMap.put(playerId, 0);

                MessageProcessor.processMessage("messages.afk", 2, afkChunks, player);
            }
        }
    }

    @Override
    public void onDisable() {
        playerAfkMap.clear();

        getLogger().info("Plugin shut down!");
    }
}

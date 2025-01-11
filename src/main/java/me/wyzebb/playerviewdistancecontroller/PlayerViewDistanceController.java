package me.wyzebb.playerviewdistancecontroller;

import com.tcoded.folialib.FoliaLib;
import me.wyzebb.playerviewdistancecontroller.commands.CommandManager;
import me.wyzebb.playerviewdistancecontroller.data.PlayerDataHandler;
import me.wyzebb.playerviewdistancecontroller.events.JoinLeaveEvent;
import me.wyzebb.playerviewdistancecontroller.events.NotAfkEvents;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerViewDistanceController extends JavaPlugin {
    public static PlayerViewDistanceController plugin;
    public static Map<UUID, Integer> playerAfkMap = new HashMap<>();

    FoliaLib foliaLib = new FoliaLib(this);

    @Override
    public void onEnable() {
        getLogger().info("Plugin started!");
        plugin = this;

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

        try {
            Class.forName("net.luckperms.api.LuckPerms");
            plugin.getLogger().info("LuckPerms detected!");
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but it extends the plugin's functionality!");
        }
    }


    public void updateLastMoved(Player player) {
        final UUID playerId = player.getUniqueId();

        if (playerAfkMap.containsKey(playerId)) {
            if (playerAfkMap.get(playerId) == 0) {
                PlayerDataHandler dataHandler = PlayerUtility.getPlayerDataHandler(player);
                player.setViewDistance(dataHandler.getChunks());

                ProcessConfigMessagesUtility.processMessage("afk-return-msg", player);
                ProcessConfigMessagesUtility.processMessage("afk-return-msg-console", player, getServer().getConsoleSender());
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

                ProcessConfigMessagesUtility.processMessage("afk-msg", player);
                ProcessConfigMessagesUtility.processMessage("afk-msg-console", player, getServer().getConsoleSender());
            }
        }
    }

    @Override
    public void onDisable() {
        playerAfkMap.clear();

        getLogger().info("Plugin shut down!");
    }
}

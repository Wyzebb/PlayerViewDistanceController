package me.wyzebb.playerviewdistancecontroller.config;

import me.wyzebb.playerviewdistancecontroller.Database;
import org.bukkit.configuration.file.FileConfiguration;

public class PluginConfig {

    private final FileConfiguration config;

    public PluginConfig(FileConfiguration config) {
        this.config = config;
    }

    // View Distance Settings
    public int getDefaultDistance() {
        return config.getInt(ConfigKeys.DEFAULT_DISTANCE, 32);
    }

    public int getBedrockDefaultDistance() {
        return config.getInt(ConfigKeys.BEDROCK_DEFAULT_DISTANCE, 32);
    }

    public int getMaxDistance() {
        return config.getInt(ConfigKeys.MAX_DISTANCE, 32);
    }

    public int getMinDistance() {
        return config.getInt(ConfigKeys.MIN_DISTANCE, 2);
    }

    // AFK Settings
    public boolean isAfkChunkLimiterEnabled() {
        return config.getBoolean(ConfigKeys.AFK_CHUNK_LIMITER, true);
    }

    public boolean isAfkOnJoinEnabled() {
        return config.getBoolean(ConfigKeys.AFK_ON_JOIN, false);
    }

    public int getAfkTime() {
        return config.getInt(ConfigKeys.AFK_TIME, 120);
    }

    public int getAfkChunks() {
        return config.getInt(ConfigKeys.AFK_CHUNKS, 2);
    }

    public boolean canSpectatorsAfk() {
        return config.getBoolean(ConfigKeys.AFK_SPECTATORS, true);
    }

    public boolean isVoidAfkEnabled() {
        return config.getBoolean(ConfigKeys.AFK_VOID_WORLD, false);
    }

    // Display Settings
    public boolean msgOnJoin() {
        return config.getBoolean(ConfigKeys.MSG_ON_JOIN, true);
    }

    public boolean msgOnJoinMax() {
        return config.getBoolean(ConfigKeys.MSG_ON_JOIN_MAX, false);
    }

    public boolean msgOnJoinMaxView() {
        return config.getBoolean(ConfigKeys.MSG_ON_JOIN_MAX_VIEW, true);
    }

    public boolean msgOnWorldChange() {
        return config.getBoolean(ConfigKeys.MSG_ON_WORLD_CHANGE, false);
    }

    // Color Settings
    public String getPrefix() {
        return config.getString(ConfigKeys.PREFIX, "§l(!) ");
    }

    public String getColor() {
        return config.getString(ConfigKeys.INFO_COLOR, "§e");
    }

    public String getSuccessColor() {
        return config.getString(ConfigKeys.SUCCESS_COLOR, "§a");
    }

    public String getErrorColor() {
        return config.getString(ConfigKeys.ERROR_COLOR, "§c");
    }

    // Feature Settings
    public boolean useClientViewDistance() {
        return config.getBoolean(ConfigKeys.USE_CLIENT_VIEW_DISTANCE, false);
    }

    public boolean isSyncSimulationDistanceEnabled() {
        return config.getBoolean(ConfigKeys.SYNC_SIMULATION_DISTANCE, true);
    }

    public boolean isUpdateCheckerEnabled() {
        return config.getBoolean(ConfigKeys.UPDATE_CHECKER_ENABLED, true);
    }

    public boolean recalculateViewDistanceOnWorldChange() {
        return config.getBoolean(ConfigKeys.RECALCULATE_VD_ON_WORLD_CHANGE, false);
    }

    public boolean savePlayerData() {
        return config.getBoolean(ConfigKeys.SAVE_PLAYER_DATA, true);
    }

    // Language Settings
    public String getLanguage() {
        return config.getString(ConfigKeys.LANGUAGE, "en_US");
    }

    public boolean isListOfflinePlayers() {
        return config.getBoolean(ConfigKeys.LIST_OFFLINE_PLAYERS, false);
    }

    public Database.StorageType getDatabaseType() {
        return Database.StorageType.valueOf(config.getString(ConfigKeys.STORAGE_METHOD, "sqlite").toUpperCase());
    }

    public String getDatabaseHost() {
        return config.getString(ConfigKeys.DB_HOST, "");
    }

    public String getDatabasePort() {
        return config.getString(ConfigKeys.DB_PORT, "");
    }

    public String getDatabaseDatabase() {
        return config.getString(ConfigKeys.DB_DATABASE, "");
    }

    public String getDatabaseUsername() {
        return config.getString(ConfigKeys.DB_USERNAME, "");
    }

    public String getDatabasePassword() {
        return config.getString(ConfigKeys.DB_PASSWORD, "");
    }

    public String getSqliteFile() {
        return config.getString(ConfigKeys.SQLITE_FILE, "");
    }

    public boolean isWorldIndependent() {
        return config.getBoolean(ConfigKeys.WORLD_INDEPENDENT, false);
    }
}
package me.wyzebb.playerviewdistancecontroller.config;

/**
 * Configuration key constants
 */
public final class ConfigKeys {
    // Colour and Prefix Settings
    public static final String PREFIX = "prefix";
    public static final String GENERIC_COLOR = "color";
    public static final String SUCCESS_COLOR = "success-color";
    public static final String ERROR_COLOR = "error-color";

    // View Distance Settings
    public static final String DEFAULT_DISTANCE = "default-distance";
    public static final String BEDROCK_DEFAULT_DISTANCE = "bedrock-default-distance";
    public static final String MAX_DISTANCE = "max-distance";
    public static final String MIN_DISTANCE = "min-distance";

    // AFK Settings
    public static final String AFK_CHUNK_LIMITER = "afk-chunk-limiter";
    public static final String AFK_ON_JOIN = "afk-on-join";
    public static final String AFK_TIME = "afk-time";
    public static final String AFK_CHUNKS = "afk-chunks";
    public static final String AFK_SPECTATORS = "afk-spectators";
    public static final String AFK_VOID_WORLD = "afk-void-world";

    // Message Settings
    public static final String LANGUAGE = "language";
    public static final String MSG_ON_JOIN = "msg-on-join";
    public static final String MSG_ON_JOIN_MAX = "msg-on-join-max";
    public static final String MSG_ON_JOIN_MAX_VIEW = "msg-on-join-max-view";
    public static final String MSG_ON_WORLD_CHANGE = "msg-on-world-change";

    // Feature Settings
    public static final String USE_CLIENT_VIEW_DISTANCE = "use-client-view-distance";
    public static final String SYNC_SIMULATION_DISTANCE = "sync-simulation-distance";
    public static final String UPDATE_CHECKER_ENABLED = "update-checker-enabled";
    public static final String RECALCULATE_VD_ON_WORLD_CHANGE = "world-change-recalculate-view";

    // Private constructor to prevent instantiation
    private ConfigKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}
package me.wyzebb.playerviewdistancecontroller.config;

/**
 * Configuration key constants
 */
public final class ConfigKeys {
    //TODO: make config keys more consistent and use these values throughout codebase

    // View Distance Settings
    public static final String DEFAULT_DISTANCE = "default-distance";
    public static final String BEDROCK_DEFAULT_DISTANCE = "bedrock-default-distance";
    public static final String MAX_DISTANCE = "max-distance";
    public static final String MIN_DISTANCE = "min-distance";

    // AFK Settings
    public static final String AFK_CHUNK_LIMITER = "afk-chunk-limiter";
    public static final String AFK_ON_JOIN = "afkOnJoin";
    public static final String AFK_TIME = "afkTime";
    public static final String AFK_CHUNKS = "afkChunks";
    public static final String ZERO_CHUNKS_AFK = "zero-chunks-afk";
    public static final String SPECTATORS_CAN_AFK = "spectators-can-afk";

    // Message Settings
    public static final String LANGUAGE = "language";
    public static final String DISPLAY_MSG_ON_JOIN = "display-msg-on-join";
    public static final String DISPLAY_MAX_JOIN_MSG = "display-max-join-msg";
    public static final String DISPLAY_MAX_CHANGE_JOIN_MSG = "display-max-change-join-msg";
    public static final String SEND_MSG_ON_WORLD_CHANGE = "send-msg-on-world-change";

    // Colour Settings
    public static final String COLOUR = "colour";
    public static final String ERROR_COLOUR = "error-colour";
    public static final String SUCCESS_COLOUR = "success-colour";

    // Feature Settings
    public static final String USE_CLIENT_VIEW_DISTANCE = "use-client-view-distance";
    public static final String SYNC_SIMULATION_DISTANCE = "sync-simulation-distance";
    public static final String UPDATE_CHECKER_ENABLED = "update-checker-enabled";
    public static final String RECALCULATE_VD_ON_WORLD_CHANGE = "recalculate-vd-on-world-change";

    // Private constructor to prevent instantiation
    private ConfigKeys() {
        throw new UnsupportedOperationException("Utility class");
    }
}
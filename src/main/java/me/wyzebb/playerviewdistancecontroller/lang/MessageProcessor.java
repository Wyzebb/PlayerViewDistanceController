package me.wyzebb.playerviewdistancecontroller.lang;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class MessageProcessor {
    private static String prefix;
    private static String colour;
    private static String errorColour;
    private static String successColour;

    static {
        loadColors();
    }

    public static void loadColors() {
        prefix = plugin.getPluginConfig().getPrefix();
        colour = plugin.getPluginConfig().getColor();
        errorColour = plugin.getPluginConfig().getErrorColor();
        successColour = plugin.getPluginConfig().getSuccessColor();
    }

    public static void processMessage(String langPath, MessageType type, OfflinePlayer target, int amount, CommandSender sendTo) {
        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg = generateColouredPrefix(type) + langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                sendTo.sendMessage(processPlaceholders(msg, target, amount));
            }
        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                if (type == MessageType.ERROR) {
                    plugin.getLogger().warning(processPlaceholders(msg, target, amount));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, target, amount));
                }
            }
        }
    }

    public static void processMessage(String langPath, MessageType type, int amount, CommandSender sendTo) {
        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg = generateColouredPrefix(type) + langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                sendTo.sendMessage(processPlaceholders(msg, amount));
            }
        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                if (type == MessageType.ERROR) {
                    plugin.getLogger().warning(processPlaceholders(msg, amount));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, amount));
                }
            }
        }
    }

    public static void processMessage(String langPath, MessageType type, boolean pingMode, CommandSender sendTo) {
        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg = generateColouredPrefix(type) + langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                sendTo.sendMessage(processPlaceholders(msg, pingMode));
            }
        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                if (type == MessageType.ERROR) {
                    plugin.getLogger().warning(processPlaceholders(msg, pingMode));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, pingMode));
                }
            }
        }
    }

    public static void processMessage(String langPath, MessageType type, CommandSender sendTo) {
        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg = generateColouredPrefix(type) + langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                sendTo.sendMessage(msg);
            }
        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                if (type == MessageType.ERROR) {
                    plugin.getLogger().warning(msg);
                } else {
                    plugin.getLogger().info(msg);
                }
            }
        }
    }

    public static void processMessage(String langPath, MessageType type, int chunks, int maxChunks, CommandSender sendTo) {
        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String msg = generateColouredPrefix(type) + langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                sendTo.sendMessage(processPlaceholders(msg, chunks, maxChunks));
            }
        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found!");
            if (!(msg.equals("none") || msg.isEmpty())) {
                if (type == MessageType.ERROR) {
                    plugin.getLogger().warning(processPlaceholders(msg, chunks, maxChunks));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, chunks, maxChunks));
                }
            }
        }
    }


    private static String processPlaceholders(String msg, OfflinePlayer target, int amount) {
        msg = msg.replace("{chunks}", String.valueOf(amount));
        msg = msg.replace("{target-player}", target.getName());

        return msg;
    }

    private static String processPlaceholders(String msg, int amount) {
        msg = msg.replace("{chunks}", String.valueOf(amount));

        return msg;
    }

    private static String processPlaceholders(String msg, boolean pingMode) {
        msg = msg.replace("{mode}", pingMode ? "on" : "off");

        return msg;
    }

    private static String processPlaceholders(String msg, int chunks, int maxChunks) {
        msg = msg.replace("{chunks}", String.valueOf(chunks));
        msg = msg.replace("{maxChunks}", String.valueOf(maxChunks));

        return msg;
    }

    private static String generateColouredPrefix(MessageType type) {
        return switch (type) {
            case SUCCESS -> prefix + successColour;
            case ERROR -> prefix + errorColour;
            case INFO -> prefix + colour;
        };
    }
}

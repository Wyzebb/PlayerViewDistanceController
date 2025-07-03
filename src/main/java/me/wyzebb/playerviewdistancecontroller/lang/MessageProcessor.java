package me.wyzebb.playerviewdistancecontroller.lang;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class MessageProcessor {
    public static void processMessage(String langPath, int status, OfflinePlayer target, int amount, CommandSender sendTo) {
        String colour = plugin.getConfig().getString("colour");
        String errorColour = plugin.getConfig().getString("error-colour");
        String successColour = plugin.getConfig().getString("success-colour");

        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String col;
            if (status == 1) {
                col = errorColour;
            } else if (status == 2) {
                col = successColour;
            } else {
                col = colour;
            }

            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");
            if (!msg.equals("none")) {
                sendTo.sendMessage(processPlaceholders(col + msg, target, amount));
            }

        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");

            if (!msg.equals("none")) {
                if (status == 1) {
                    plugin.getLogger().warning(processPlaceholders(msg, target, amount));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, target, amount));
                }
            }
        }
    }

    public static void processMessage(String langPath, int status, int amount, CommandSender sendTo) {
        String colour = plugin.getConfig().getString("colour");
        String errorColour = plugin.getConfig().getString("error-colour");
        String successColour = plugin.getConfig().getString("success-colour");

        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String col;
            if (status == 1) {
                col = errorColour;
            } else if (status == 2) {
                col = successColour;
            } else {
                col = colour;
            }

            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");
            if (!msg.equals("none")) {
                sendTo.sendMessage(processPlaceholders(col + msg, amount));
            }

        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");

            if (!msg.equals("none")) {
                if (status == 1) {
                    plugin.getLogger().warning(processPlaceholders(msg, amount));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, amount));
                }
            }
        }
    }

    public static void processMessage(String langPath, int status, boolean pingMode, CommandSender sendTo) {
        String colour = plugin.getConfig().getString("colour");
        String errorColour = plugin.getConfig().getString("error-colour");
        String successColour = plugin.getConfig().getString("success-colour");

        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String col;
            if (status == 1) {
                col = errorColour;
            } else if (status == 2) {
                col = successColour;
            } else {
                col = colour;
            }

            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");
            if (!msg.equals("none")) {
                sendTo.sendMessage(processPlaceholders(col + msg, pingMode));
            }

        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");

            if (!msg.equals("none")) {
                if (status == 1) {
                    plugin.getLogger().warning(processPlaceholders(msg, pingMode));
                } else {
                    plugin.getLogger().info(processPlaceholders(msg, pingMode));
                }
            }
        }
    }

    public static void processMessage(String langPath, int status, CommandSender sendTo) {
        String colour = plugin.getConfig().getString("colour");
        String errorColour = plugin.getConfig().getString("error-colour");
        String successColour = plugin.getConfig().getString("success-colour");

        LanguageManager languageManager = plugin.getLanguageManager();
        FileConfiguration langConfig = languageManager.getLanguageFile();

        if (sendTo instanceof Player) {
            String col;
            if (status == 1) {
                col = errorColour;
            } else if (status == 2) {
                col = successColour;
            } else {
                col = colour;
            }

            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");
            if (!msg.equals("none")) {
                sendTo.sendMessage(col + msg);
            }

        } else {
            String msg = langConfig.getString(langPath, "Message key '" + langPath + "' not found. Please report to the developer!");

            if (!msg.equals("none")) {
                if (status == 1) {
                    plugin.getLogger().warning(msg);
                } else {
                    plugin.getLogger().info(msg);
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
}

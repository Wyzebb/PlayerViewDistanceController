package me.wyzebb.playerviewdistancecontroller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class UpdateChecker implements Runnable {
    private static boolean upToDate = false;
    private static boolean experimental = false;
    private static String latest = "";
    private static String pluginVersion = "";

    @Override
    public void run() {
        plugin.getLogger().info("Checking for updates...");

        pluginVersion = plugin.getDescription().getVersion();

        String versionUrl = "https://raw.githubusercontent.com/Wyzebb/PlayerViewDistanceController/refs/heads/master/version.txt";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    latest = reader.readLine();
                }

                if (!pluginVersion.contains("-EXPERIMENTAL")) {
                    if (pluginVersion.equals(latest)) {
                        plugin.getLogger().info("Plugin is up to date!");
                        upToDate = true;
                    } else {
                        plugin.getLogger().warning("Plugin is out of date! Please update from v" + pluginVersion + " to v" + latest + "!");
                    }
                } else {
                    plugin.getLogger().warning("You are using an experimental version of PVDC. Proceed with caution!");
                    experimental = true;
                    upToDate = true;
                }
            } else {
                plugin.getLogger().warning("Unable to check for updates! HTTP response code: " + connection.getResponseCode());
            }

        } catch (IOException exception) {
            plugin.getLogger().warning("Error while checking for updates: " + exception.getMessage());
        }
    }

    public static boolean isUpToDate() {
        return upToDate;
    }

    public static boolean isExperimental() {
        return experimental;
    }

    public static String getLatestVersion() {
        return latest;
    }

    public static String getPluginVersion() {
        return pluginVersion;
    }
}
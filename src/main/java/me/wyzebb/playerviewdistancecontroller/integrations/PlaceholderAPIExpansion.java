package me.wyzebb.playerviewdistancecontroller.integrations;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.wyzebb.playerviewdistancecontroller.utility.PlayerDataManager;
import me.wyzebb.playerviewdistancecontroller.state.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "pvdc";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Wyzebb";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer != null && offlinePlayer.isOnline()) {
            Player player = offlinePlayer.getPlayer();

            if (params.contains("chunks_")) {
                String playerName = params.replace("chunks_", "");

                try {
                    Player target = Bukkit.getPlayer(playerName);
                    return String.valueOf(PlayerDataManager.getCurrentViewDistance(target));

                } catch (Exception e) {
                    plugin.getLogger().warning("Couldn't get info for placeholder: " + e);
                }


                return String.valueOf(PlayerDataManager.getCurrentViewDistance(player));
            } else if (params.equalsIgnoreCase("chunks")) {
                return String.valueOf(PlayerDataManager.getCurrentViewDistance(player));
            } else if (params.contains("afk_")) {
                String playerName = params.replace("afk_", "");

                try {
                    Player target = Bukkit.getPlayer(playerName);

                    if (target != null && plugin.getStateManager().getPlayerState(target.getUniqueId()) == PlayerState.AFK) {
                        return "AFK";
                    } else {
                        return "Not AFK";
                    }

                } catch (Exception e) {
                    plugin.getLogger().warning("Couldn't get info for placeholder: " + e);
                }
            } else if (params.equalsIgnoreCase("afk")) {
                if (plugin.getStateManager().getPlayerState(player.getUniqueId()) == PlayerState.AFK) {
                    return "AFK";
                } else {
                    return "Not AFK";
                }
            }
        }

        return null;
    }

    public static void registerHook() {
        new PlaceholderAPIExpansion().register();
    }
}
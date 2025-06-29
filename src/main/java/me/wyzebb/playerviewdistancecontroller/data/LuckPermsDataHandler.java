package me.wyzebb.playerviewdistancecontroller.data;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsDataHandler {

    public static int getLuckpermsDistance(OfflinePlayer player) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

            if (provider == null) {
                return 32; // Return default distance if LuckPerms is not available
            }

            LuckPerms api = provider.getProvider();
            User user = api.getPlayerAdapter(Player.class).getUser((Player) player);

            // Regular expression to match permissions like pvdc.maxdistance.7
            Pattern pattern = Pattern.compile("pvdc\\.maxdistance\\.(\\d+)");

            int max = getMaxDistance(user, pattern, player);

            if (max == 0) {
                return 32;
            } else {
                return max;
            }
    }

    private static int getMaxDistance(User user, Pattern pattern, OfflinePlayer player) {
        int maxDistance = 0;
        int maxDistanceContext = 0;

        for (var node : user.resolveInheritedNodes(QueryOptions.nonContextual())) {
            String permission = node.getKey();
            Matcher matcher = pattern.matcher(permission);

            if (matcher.matches()) {
                ContextSet contextSet = node.getContexts();

                if (player.isOnline()) {
                    if (contextSet.containsKey("world")) {
                        String playerWorldName = player.getPlayer().getWorld().getName();
                        plugin.getLogger().warning("plrworld: " + playerWorldName);
                        String worldName = contextSet.getAnyValue("world").orElse("unknown");
                        plugin.getLogger().warning("world: " + worldName);

                        // Skip the node if it's for a different world
                        if (playerWorldName.equals(worldName)) {
                            plugin.getLogger().warning("same1");
                            // Extract the number from the permission
                            int distance = Integer.parseInt(matcher.group(1));
                            if (distance > maxDistanceContext) {
                                maxDistanceContext = distance;
                                plugin.getLogger().warning("max context: " + maxDistanceContext);
                            }
                            continue;
                        } else {
                            plugin.getLogger().warning("skip");
                        }
                    } else {
                        plugin.getLogger().warning("part2");

                        // Extract the number from the permission
                        int distance = Integer.parseInt(matcher.group(1));
                        if (distance > maxDistance) {
                            maxDistance = distance;
                            plugin.getLogger().warning("max2: " + maxDistance);
                        }
                    }
                }
            }
        }

        return Math.max(maxDistance, maxDistanceContext);
    }
}

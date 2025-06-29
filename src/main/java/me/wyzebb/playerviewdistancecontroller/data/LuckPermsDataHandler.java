package me.wyzebb.playerviewdistancecontroller.data;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            int max = getMaxDistance(user, pattern);

            if (max == 0) {
                return 32;
            } else {
                return max;
            }
    }

    private static int getMaxDistance(User user, Pattern pattern) {
        int maxDistance = 0;

        for (var node : user.resolveInheritedNodes(QueryOptions.nonContextual())) {
            String permission = node.getKey();
            Matcher matcher = pattern.matcher(permission);

            if (matcher.matches()) {
                // Extract the number from the permission
                int distance = Integer.parseInt(matcher.group(1));
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
        }

        return maxDistance;
    }
}

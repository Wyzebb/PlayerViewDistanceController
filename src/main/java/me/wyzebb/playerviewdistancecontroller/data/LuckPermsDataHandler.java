package me.wyzebb.playerviewdistancecontroller.data;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LuckPermsDataHandler {

    public static int getLuckpermsDistance(PlayerJoinEvent e, PlayerViewDistanceController plugin) {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);

            if (provider == null) {
                plugin.getLogger().warning("LuckPerms is not running on this server: it is optional, but it extends the plugin's functionality!");
                return 32; // Return default distance if LuckPerms is not available
            }

            LuckPerms api = provider.getProvider();
            User user = api.getPlayerAdapter(Player.class).getUser(e.getPlayer());

            // Regular expression to match permissions like pvdc.maxdistance.7
            Pattern pattern = Pattern.compile("viewdistance\\.maxdistance\\.(\\d+)");

            return getMaxDistance(user, pattern);
    }

    private static int getMaxDistance(User user, Pattern pattern) {
        int maxDistance = 32;
        for (var node : user.getNodes()) {
            String permission = node.getKey();

            Matcher matcher = pattern.matcher(permission);
            if (matcher.matches()) {
                // Extract the number from the permission
                int distance = Integer.parseInt(matcher.group(1));
                if (maxDistance == 32 || distance < maxDistance) {
                    maxDistance = distance;
                }
            }
        }
        return maxDistance;
    }
}

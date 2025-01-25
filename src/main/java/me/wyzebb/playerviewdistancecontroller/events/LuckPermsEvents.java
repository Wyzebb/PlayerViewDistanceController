package me.wyzebb.playerviewdistancecontroller.events;

import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeClearEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsEvents {
    private final LuckPerms luckPerms;
    private static final Map<UUID, Integer> lastUpdates = new HashMap<>();

    public LuckPermsEvents(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    private void messageIfNotAlready(UUID playerId) {
        int currentTime = (int) System.currentTimeMillis();
        int lastMoved = lastUpdates.getOrDefault(playerId, 0);

        lastUpdates.put(playerId, currentTime);

        if (currentTime - lastMoved > (1000)) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);

            if (offlinePlayer.isOnline()) {
                VdCalculator.calcVdSet(Bukkit.getPlayer(playerId), true);
            }
        }
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();

        eventBus.subscribe(plugin, NodeAddEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && ((PermissionNode) e.getNode()).getPermission().contains("pvdc")) {
                    messageIfNotAlready(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    messageIfNotAlready(player.getUniqueId());
                }
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && ((PermissionNode) e.getNode()).getPermission().contains("pvdc")) {
                    messageIfNotAlready(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    messageIfNotAlready(player.getUniqueId());
                }
            }
        });

        eventBus.subscribe(plugin, NodeClearEvent.class, e -> {
            if (e.isUser()) {
                for (int i = 1; i <= e.getDataBefore().size(); i++) {
                    if (e.getDataBefore().stream().toList().get(i - 1).toString().contains("pvdc")) {
                        messageIfNotAlready(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                    }
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    messageIfNotAlready(player.getUniqueId());
                }
            }

        });
    }


}
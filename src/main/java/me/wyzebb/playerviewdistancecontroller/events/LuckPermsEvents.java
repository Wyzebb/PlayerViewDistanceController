package me.wyzebb.playerviewdistancecontroller.events;

import com.tcoded.folialib.FoliaLib;
import me.wyzebb.playerviewdistancecontroller.data.VdCalculator;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.event.node.NodeRemoveEvent;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.node.NodeType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsEvents {
    private final LuckPerms luckPerms;
    public static Map<UUID, Integer> lastUpdates = new HashMap<>();
    private boolean messaged = false;
    private final FoliaLib foliaLib = new FoliaLib(plugin);

    public LuckPermsEvents(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    private void messageIfNotAlready(UUID playerId) {
        if (Bukkit.getPlayer(playerId).isOnline()) {
            if (!messaged) {
                messaged = true;
                foliaLib.getScheduler().runLater(() -> {
                    VdCalculator.calcVdSet(Bukkit.getPlayer(playerId), true);
                    messaged = false;
                }, 20);
            }
        }
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();

        eventBus.subscribe(plugin, NodeAddEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && e.getNode().getKey().contains("pvdc")) {
                    messageIfNotAlready(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                if (e.getNode().getKey().contains("pvdc")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        messageIfNotAlready(player.getUniqueId());
                    }
                }
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && e.getNode().getKey().contains("pvdc")) {
                    messageIfNotAlready(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                if (e.getNode().getKey().contains("pvdc")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        messageIfNotAlready(player.getUniqueId());
                    }
                }
            }
        });

        eventBus.subscribe(plugin, UserPromoteEvent.class, e -> {
            messageIfNotAlready(e.getUser().getUniqueId());
        });

        eventBus.subscribe(plugin, UserDemoteEvent.class, e -> {
            messageIfNotAlready(e.getUser().getUniqueId());
        });
    }
}
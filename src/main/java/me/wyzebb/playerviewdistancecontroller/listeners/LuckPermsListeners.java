package me.wyzebb.playerviewdistancecontroller.listeners;

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

import java.util.UUID;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsListeners {
    private final LuckPerms luckPerms;
    private boolean messaged = false;
    private final FoliaLib foliaLib = new FoliaLib(plugin);

    public LuckPermsListeners(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    private void lastNodeUpdateVd(UUID playerId) {
        if (Bukkit.getPlayer(playerId).isOnline()) {
            if (!messaged) {
                messaged = true;
                foliaLib.getScheduler().runLater(() -> {
                    VdCalculator.calcVdSet(Bukkit.getPlayer(playerId), true, false);
                    messaged = false;
                }, 30);
            }
        }
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();

        eventBus.subscribe(plugin, NodeAddEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && e.getNode().getKey().contains("pvdc")) {
                    lastNodeUpdateVd(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                if (e.getNode().getKey().contains("pvdc")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        lastNodeUpdateVd(player.getUniqueId());
                    }
                }
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, e -> {
            if (e.isUser()) {
                if (e.getNode().getType() == NodeType.PERMISSION && e.getNode().getKey().contains("pvdc")) {
                    lastNodeUpdateVd(Bukkit.getPlayerUniqueId(e.getTarget().getFriendlyName()));
                }
            } else {
                if (e.getNode().getKey().contains("pvdc")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        lastNodeUpdateVd(player.getUniqueId());
                    }
                }
            }
        });

        eventBus.subscribe(plugin, UserPromoteEvent.class, e -> lastNodeUpdateVd(e.getUser().getUniqueId()));

        eventBus.subscribe(plugin, UserDemoteEvent.class, e -> lastNodeUpdateVd(e.getUser().getUniqueId()));
    }
}
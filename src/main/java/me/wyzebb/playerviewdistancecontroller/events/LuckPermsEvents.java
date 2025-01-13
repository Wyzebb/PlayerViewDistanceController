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

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class LuckPermsEvents {
    private final LuckPerms luckPerms;

    public LuckPermsEvents(LuckPerms luckPerms) {
        this.luckPerms = luckPerms;
    }

    public void register() {
        EventBus eventBus = this.luckPerms.getEventBus();

        eventBus.subscribe(plugin, NodeAddEvent.class, e -> {
            if (!e.isUser()) {
                return;
            }

            if (e.getNode().getType() == NodeType.PERMISSION && ((PermissionNode) e.getNode()).getPermission().contains("pvdc")) {
                VdCalculator.calcVdAndSetNoReset(Bukkit.getPlayer(e.getTarget().getFriendlyName()));
            }
        });

        eventBus.subscribe(plugin, NodeRemoveEvent.class, e -> {
            if (!e.isUser()) {
                return;
            }

            if (e.getNode().getType() == NodeType.PERMISSION && ((PermissionNode) e.getNode()).getPermission().contains("pvdc")) {
                VdCalculator.calcVdAndSetNoReset(Bukkit.getPlayer(e.getTarget().getFriendlyName()));
            }
        });

        eventBus.subscribe(plugin, NodeClearEvent.class, e -> {
            if (!e.isUser()) {
                return;
            }

            for (int i = 1; i <= e.getDataBefore().size(); i++) {
                if (e.getDataBefore().stream().toList().get(i - 1).toString().contains("pvdc")) {
                    VdCalculator.calcVdAndSetNoReset(Bukkit.getPlayer(e.getTarget().getFriendlyName()));
                }
            }

        });
    }


}
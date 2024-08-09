package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.command.CommandSender;

import java.io.File;

public class ReloadCommand extends SubCommand {

    private final PlayerViewDistanceController plugin;

    public ReloadCommand(PlayerViewDistanceController plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin's config files";
    }

    @Override
    public String getSyntax() {
        return "/vd reload";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        plugin.saveDefaultConfig();
        plugin.getConfig().options().copyDefaults(true);
        createPrefixesConfig();
        ProcessConfigMessagesUtility.processMessage("reload-config-msg", commandSender);
    }

    private void createPrefixesConfig() {
        File prefixesConfigFile = new File(plugin.getDataFolder(), "prefixes.yml");
        if (!prefixesConfigFile.exists()) {
            boolean folder = prefixesConfigFile.getParentFile().mkdirs();
            if (!folder) {
                plugin.getLogger().warning("An error occured when reloading config");
            }
            plugin.saveResource("prefixes.yml", false);
        }
    }
}
package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import org.bukkit.command.CommandSender;

import java.io.File;

import static me.wyzebb.playerviewdistancecontroller.PlayerViewDistanceController.plugin;

public class ReloadCommand extends SubCommand {

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
        return "/pvdc reload";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (commandSender.hasPermission("pvdc.reload")) {
            plugin.getConfig().options().copyDefaults(true);
            createPrefixesConfig();
            plugin.reloadConfig();
            ProcessConfigMessagesUtility.processMessage("reload-config-msg", commandSender);
        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }

    private void createPrefixesConfig() {
        File prefixesConfigFile = new File(plugin.getDataFolder(), "prefixes.yml");
        if (!prefixesConfigFile.exists()) {
            prefixesConfigFile.getParentFile().mkdirs();
            plugin.saveResource("prefixes.yml", false);
        }
    }
}
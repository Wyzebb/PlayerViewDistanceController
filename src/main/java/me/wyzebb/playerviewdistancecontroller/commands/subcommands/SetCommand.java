package me.wyzebb.playerviewdistancecontroller.commands.subcommands;

import me.wyzebb.playerviewdistancecontroller.data.LuckPermsDataHandler;
import me.wyzebb.playerviewdistancecontroller.utility.ClampAmountUtility;
import me.wyzebb.playerviewdistancecontroller.utility.DataProcessorUtility;
import me.wyzebb.playerviewdistancecontroller.utility.ProcessConfigMessagesUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCommand extends SubCommand {

    private final MiniMessage mm;

    public SetCommand() {
        this.mm = MiniMessage.miniMessage();
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set your own max view distance or the max view distance of another player";
    }

    @Override
    public String getSyntax() {
        return "/pvdc set <chunks> [player]";
    }

    @Override
    public void performCommand(CommandSender commandSender, String[] args) {
        if (args.length < 2 || args.length > 3) {
            ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
        } else {
            int amount = ClampAmountUtility.getMaxPossible();

            try {
                amount = Integer.parseInt(args[1]);
                amount = ClampAmountUtility.clampChunkValue(amount);
            } catch (Exception e) {
                ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
            }

            if (args.length == 2) {
                if (commandSender instanceof Player) {
                    setSelf(commandSender, amount);
                } else {
                    ProcessConfigMessagesUtility.processMessage("incorrect-args", commandSender);
                }

            } else {
                String targetName = args[2];
                Player target = Bukkit.getServer().getPlayerExact(targetName);

                if (target == null) {
                    ProcessConfigMessagesUtility.processMessage("player-offline-msg", commandSender);

                } else if (commandSender == target) {
                    setSelf(commandSender, amount);

                } else {
                    if (commandSender.hasPermission("pvdc.set-others")) {
                        ProcessConfigMessagesUtility.processMessage("sender-view-distance-change-msg", amount, target, commandSender);
                        ProcessConfigMessagesUtility.processMessage("target-view-distance-change-msg", amount, target, target);
                        DataProcessorUtility.processDataOthers(target, amount);
                    } else {
                        ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
                    }
                }
            }
        }
    }

    private void setSelf(CommandSender commandSender, int amount) {
        if (commandSender.hasPermission("pvdc.set-self")) {
            int luckpermsMax = LuckPermsDataHandler.getLuckpermsDistance((Player) commandSender);
            if (luckpermsMax > amount || commandSender.hasPermission("pvdc.bypass-maxdistance")) {
                ProcessConfigMessagesUtility.processMessage("self-view-distance-change-msg", commandSender, amount);
                DataProcessorUtility.processData((Player) commandSender, amount);
            } else {
                Component parsed = mm.deserialize("<yellow><b>(!)</b> <u><click:open_url:'https://modrinth.com/plugin/pvdc'><hover:show_text:'<green>Click to go to the plugin page</green>'>New update available for PVDC</hover></click></u>! Update from <red><b>v1.6.0</b></red> to <green><b>v2.0.0</b></green> to get the best experience!</yellow>");

                commandSender.sendMessage(parsed);
//                ProcessConfigMessagesUtility.processMessage("chunks-too-high", commandSender, luckpermsMax);
            }

        } else {
            ProcessConfigMessagesUtility.processMessage("no-permission", commandSender);
        }
    }
}
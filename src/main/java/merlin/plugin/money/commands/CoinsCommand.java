package merlin.plugin.money.commands;

import merlin.plugin.money.Money;
import merlin.plugin.money.handlers.CommandHandler;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

public class CoinsCommand implements CommandExecutor, TabCompleter {
    private final Money plugin;
    private final Map<String, ISubCommand> subCommands = new HashMap<>();

    public CoinsCommand(Money plugin) {
        subCommands.put("balance", CommandHandler::showPlayerBalance);
        subCommands.put("scoreboard", CommandHandler::showScoreboard);
        subCommands.put("blocks", CommandHandler::showBlockValues);
        subCommands.put("blocks.add", CommandHandler::addBlockToValue);
        subCommands.put("job", CommandHandler::showPlayerProfession);
        subCommands.put("npc.add", CommandHandler::createNPC);

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if(!(sender instanceof Player)) { return true; }
            Player player = (Player)sender;

            if (args.length == 0) {
                sender.sendMessage("An example: " + ChatColor.AQUA + " /coins scoreboard");
                return true;
            }

            final CommandWithParameters cmdWithArgs = getPotentialCommandWithParameters(args);

            if(cmdWithArgs.subCommand == null) {
                player.sendMessage(ChatColor.DARK_RED + "Command does not exist.");
                return true;
            }

            return cmdWithArgs.subCommand.execute(player, plugin, cmdWithArgs.parameters);
        } catch (Exception exception) {
            sender.sendMessage(ChatColor.DARK_RED + "Something went wrong executing the command.");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return CommandHandler.getAutoCompletion(args);
    }

    /**
     * Helper method to get the potential command with parameters.
     * @param args Parts of the command.
     * @return The command with parameters.
     */
    private CommandWithParameters getPotentialCommandWithParameters(String[] args) {
        ISubCommand subCommand = null;
        String[] parameterForCommand = new String[0];
        String potentialKey;

        for(int i = args.length; i > 0; i--) {
            potentialKey = String.join(".", Arrays.copyOfRange(args, 0, i));
            parameterForCommand = Arrays.copyOfRange(args, i, args.length);

            if(subCommands.containsKey(potentialKey)) {
                subCommand = subCommands.get(potentialKey);
                break;
            }
        }

        return new CommandWithParameters(subCommand, parameterForCommand);
    }

}

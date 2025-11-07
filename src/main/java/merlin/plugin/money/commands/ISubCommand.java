package merlin.plugin.money.commands;

import merlin.plugin.money.Money;
import org.bukkit.entity.Player;

@FunctionalInterface
public interface ISubCommand {
    /**
     * Executes the implemented command.
     * @param player The player who executed the command.
     * @param plugin The plugin for accessing data.
     * @param args Arguments for the command.
     * @return True on successful execution, otherwise false.
     */
    boolean execute(final Player player, final Money plugin, final String[] args);
}

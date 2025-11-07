package merlin.plugin.money.commands;

/**
 * Helper object to return a command with the parameters.
 */
public class CommandWithParameters {
    public final ISubCommand subCommand;
    public final String[] parameters;

    public CommandWithParameters(final ISubCommand subCommand, final String[] parameters) {
        this.subCommand = subCommand;
        this.parameters = parameters;
    }
}

package net.azalealibrary.configuration.command;

import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.command.property.InfoSubcommand;
import net.azalealibrary.configuration.command.property.ResetSubcommand;
import net.azalealibrary.configuration.command.property.SetSubcommand;
import net.azalealibrary.configuration.config.Configuration;

public class ConfigureCommand extends CommandNode {

    public ConfigureCommand(Configuration configuration) {
        this(configuration.getName(), configuration);
    }

    public ConfigureCommand(String name, Configuration configuration) {
        super(name,
                new ReloadSubcommand(configuration),
                new SaveSubcommand(configuration),
                new InfoSubcommand(configuration),
                new ResetSubcommand(configuration),
                new SetSubcommand(configuration)
        );
    }
}

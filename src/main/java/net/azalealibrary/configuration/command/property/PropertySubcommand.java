package net.azalealibrary.configuration.command.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.config.Configuration;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class PropertySubcommand extends CommandNode {

    private final List<ConfigurableProperty<?, ?>> properties;

    public PropertySubcommand(String name, Configuration configuration) {
        super(name);

        properties = configuration.getProperties().stream()
                .filter(ConfigurableProperty::isEditable)
                .toList();
    }

    protected abstract boolean isSelected(Arguments arguments, ConfigurableProperty<?, ?> property);

    @Override
    public List<String> complete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return properties.stream().map(ConfigurableProperty::getName).toList();
        }
        List<ConfigurableProperty<?, ?>> selected = properties.stream()
                .filter(p -> isSelected(arguments, p))
                .toList();

        if (selected.isEmpty()) {
            throw new AzaleaException("No properties found.");
        }
        return selected.stream().findFirst()
                .map(p -> p.onComplete(sender, arguments.subArguments(1)))
                .orElse(List.of());
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        for (ConfigurableProperty<?, ?> property : properties.stream().filter(p -> isSelected(arguments, p)).toList()) {
            execute(sender, arguments, property);
        }
    }

    protected abstract void execute(CommandSender sender, Arguments arguments, ConfigurableProperty<?, ?> property);
}

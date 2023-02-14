package net.azalealibrary.configuration;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ConfigureCommand extends CommandNode {

    private static final String SET = "set";
    private static final String RESET = "reset";
    private static final String INFO = "info";

    public ConfigureCommand() {
        super("configure");
    }

    @Override
    public List<String> complete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return ConfigurationApi.getConfigurations().stream().map(Configurable::getName).toList();
        } else {
            Configurable configuration = arguments.find(0, "configuration", ConfigurationApi::getConfiguration);
            List<ConfigurableProperty<?, ?>> properties = configuration.getProperties();

            if (arguments.size() == 2) {
                return properties.stream().map(ConfigurableProperty::getName).toList();
            } else if (arguments.size() == 3) {
                return List.of(SET, RESET, INFO);
            } else if (arguments.size() > 3 && arguments.is(2, SET)) {
                return properties.stream()
                        .filter(p -> p.getName().equals(arguments.get(1)))
                        .findFirst().map(p -> p.onComplete(sender, arguments.subArguments(3)))
                        .orElse(List.of());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Configurable configuration = arguments.find(0, "configuration", ConfigurationApi::getConfiguration);
        ConfigurableProperty<?, ?> property = arguments.find(1, "property", input -> configuration.getProperties().stream().filter(p -> p.getName().equals(input)).findFirst().orElse(null));
        String action = arguments.matchesAny(2, "action", SET, RESET, INFO);

        switch (action) {
            case SET -> {
                property.onExecute(sender, arguments.subArguments(3));
                sender.sendMessage("Property " + TextUtil.getName(property) + " updated.");
            }
            case RESET -> {
                property.reset();
                sender.sendMessage("Property " + TextUtil.getName(property) + " has been reset.");
            }
            case INFO -> {
                sender.sendMessage(TextUtil.printable(property, 60).toArray(String[]::new));
            }
        }
    }
}

package com.azalealibrary.configuration.command;

import com.azalealibrary.configuration.ConfigurationApi;
import com.azalealibrary.configuration.FileConfiguration;
import com.azalealibrary.configuration.TextUtil;
import com.azalealibrary.configuration.property.ConfigurableProperty;
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
            return ConfigurationApi.getConfigurations().stream().map(FileConfiguration::getName).toList();
        } else {
            FileConfiguration configuration = arguments.find(0, "configuration", input -> ConfigurationApi.getConfigurations().stream().filter(c -> c.getName().equals(input)).findFirst().orElse(null));
            List<ConfigurableProperty<?, ?>> properties = configuration.getConfigurable().getProperties();

            if (arguments.size() == 2) {
                return properties.stream().map(ConfigurableProperty::getName).toList();
            } else if (arguments.size() == 3) {
                return List.of(SET, RESET, INFO);
            } else if (arguments.size() > 3 && arguments.is(2, SET)) {
                return properties.stream()
                        .filter(p -> p.getName().equals(arguments.get(1)))
                        .findFirst().map(p -> p.get(sender, arguments.subArguments(3)))
                        .orElse(List.of());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        FileConfiguration configuration = arguments.find(0, "configuration", input -> ConfigurationApi.getConfigurations().stream().filter(c -> c.getName().equals(input)).findFirst().orElse(null));
        ConfigurableProperty<?, ?> property = arguments.find(1, "property", input -> configuration.getConfigurable().getProperties().stream().filter(p -> p.getName().equals(input)).findFirst().orElse(null));
        String action = arguments.matchesAny(2, "action", SET, RESET, INFO);

        switch (action) {
            case SET -> {
                property.set(sender, arguments.subArguments(3));
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

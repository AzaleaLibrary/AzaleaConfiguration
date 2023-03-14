package net.azalealibrary.configuration;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.command.TextUtil;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigureCommand extends CommandNode {

    public ConfigureCommand() {
        super("configure");
    }

    @Override
    public List<String> complete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return AzaleaConfigurationApi.getConfigurations().stream().map(Configurable::getName).toList();
        } else if (arguments.size() == 2) {
            return Arrays.stream(Action.values()).map(v -> v.name().toLowerCase()).toList();
        } else {
            Configurable configuration = arguments.find(0, "configuration", AzaleaConfigurationApi::getConfiguration);
            Action action = arguments.find(1, "action", input -> Action.valueOf(input.toUpperCase()));
            List<ConfigurableProperty<?, ?>> properties = configuration.getProperties();

            if (arguments.size() == 3) {
                return properties.stream().map(ConfigurableProperty::getName).toList();
            } else if (arguments.size() == 4 && action == Action.SET) {
                return properties.stream()
                        .filter(p -> p.getName().equals(arguments.get(2)))
                        .findFirst().map(p -> p.onComplete(sender, arguments.subArguments(3)))
                        .orElse(List.of());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        Configurable configuration = arguments.find(0, "configuration", AzaleaConfigurationApi::getConfiguration);
        Action action = arguments.find(1, "action", input -> Action.valueOf(input.toUpperCase()));
        Arguments sub = arguments.subArguments(3);
        List<ConfigurableProperty<?, ?>> properties = arguments.find(2, "property", input -> configuration.getProperties().stream()
                .filter(c -> action.predicate.test(sender, sub, input, c))
                .toList());

        if (properties.isEmpty()) {
            throw new AzaleaException("No properties found.");
        }

        switch (action) {
            case SET -> {
                sender.sendMessage(getMessage(properties, "updated"));

                for (ConfigurableProperty<?, ?> property : properties) {
                    property.onExecute(sender, sub);
                    sender.sendMessage("  " + ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET);
                }
            }
            case RESET -> {
                sender.sendMessage(getMessage(properties, "reset"));

                for (ConfigurableProperty<?, ?> property : properties) {
                    property.reset();
                    sender.sendMessage("  " + ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET);
                }
            }
            case INFO -> {
                ConfigurableProperty<?, ?> property = properties.get(0);
                List<String> info = new ArrayList<>();
                String type = property.getType().getExpected() + (property instanceof ListProperty<?> ? " (list)" : "");
                info.add("Property: " + ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET + " " + type);
                info.add("Default: " + ChatColor.AQUA + property.getDefault());
                info.add("Value: " + ChatColor.YELLOW + property);
                property.getDescription().forEach(l -> info.addAll(TextUtil.split(l, 55).stream().map(i -> "  " + i).toList()));
                sender.sendMessage(info.toArray(String[]::new));
            }
        }
    }

    private static String getMessage(List<ConfigurableProperty<?, ?>> properties, String action) {
        return String.valueOf(ChatColor.YELLOW) + properties.size() + ChatColor.RESET +
                (properties.size() > 1 ? " properties" : " property") +
                (properties.size() > 1 ? " have" : " has") + " been " + action + ":";
    }

    private enum Action {
        SET((s, a, i, c) -> c.getName().matches(i) && c.getType().test(s, a)),
        RESET((s, a, i, c) -> c.getName().matches(i)),
        INFO((s, a, i, c) -> c.getName().equals(i));

        final ActionTester predicate;

        Action(ActionTester predicate) {
            this.predicate = predicate;
        }

        @FunctionalInterface
        private interface ActionTester {
            boolean test(CommandSender sender, Arguments arguments, String input, ConfigurableProperty<?, ?> property);
        }
    }
}

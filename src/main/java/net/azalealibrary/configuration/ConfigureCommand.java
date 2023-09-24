package net.azalealibrary.configuration;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.command.TextUtil;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigureCommand extends CommandNode {

    private static final String INDENT = "  ";

    public ConfigureCommand() {
        super("configure");
    }

    @Override
    public @Nullable String getPermission() {
        return "azalea.configure";
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
                return properties.stream().filter(ConfigurableProperty::isEditable).map(ConfigurableProperty::getName).toList();
            } else if (arguments.size() > 3 && action == Action.SET) {
                return properties.stream()
                        .filter(ConfigurableProperty::isEditable)
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
                .filter(ConfigurableProperty::isEditable)
                .filter(c -> action.predicate.test(sender, sub, input, c))
                .toList());

        if (properties.isEmpty()) {
            throw new AzaleaException("No properties found.");
        }

        switch (action) {
            case SET -> {
                properties.forEach(p -> p.onExecute(sender, sub));
                sender.sendMessage(getMessage(properties, "updated"));
            }
            case RESET -> {
                properties.forEach(ConfigurableProperty::reset);
                sender.sendMessage(getMessage(properties, "reset"));
            }
            case INFO -> {
                ConfigurableProperty<?, ?> property = properties.get(0);

                List<String> info = new ArrayList<>();
                info.add(ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET + "=" + ChatColor.YELLOW + property);
                property.getDescription().forEach(l -> info.addAll(TextUtil.split(l, 55).stream().map(i -> ChatColor.GRAY + INDENT + i).toList()));
                sender.sendMessage(info.toArray(String[]::new));
            }
        }
    }

    private static String[] getMessage(List<ConfigurableProperty<?, ?>> properties, String action) {
        List<String> lines = new ArrayList<>();
        lines.add(String.valueOf(ChatColor.YELLOW) + properties.size() + ChatColor.RESET +
                (properties.size() > 1 ? " properties" : " property") +
                (properties.size() > 1 ? " have" : " has") + " been " + action + ":");
        lines.addAll(properties.stream().map(p -> INDENT + ChatColor.LIGHT_PURPLE + p.getName()).toList());
        return lines.toArray(String[]::new);
    }

    private enum Action {
        SET((s, a, i, c) -> c instanceof ListProperty<?> ? c.getName().equals(i) : c.getName().matches(i) && c.getPropertyType().test(s, a)),
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

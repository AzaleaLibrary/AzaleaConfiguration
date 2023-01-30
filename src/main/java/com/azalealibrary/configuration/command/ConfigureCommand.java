package com.azalealibrary.configuration.command;

import com.azalealibrary.configuration.ConfigurationApi;
import com.azalealibrary.configuration.FileConfiguration;
import com.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            List<ConfigurableProperty<?>> properties = configuration.getConfigurable().getProperties();

            if (arguments.size() == 2) {
                return properties.stream().map(ConfigurableProperty::getName).toList();
            } else if (arguments.size() == 3) {
                return List.of(SET, RESET, INFO);
            } else if (arguments.size() > 3 && arguments.is(2, SET)) {
                return properties.stream()
                        .filter(p -> p.getName().equals(arguments.get(1)))
                        .findFirst().map(p -> p.onSuggest(sender, arguments.subArguments(3)))
                        .orElse(List.of());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        FileConfiguration configuration = arguments.find(0, "configuration", input -> ConfigurationApi.getConfigurations().stream().filter(c -> c.getName().equals(input)).findFirst().orElse(null));
        ConfigurableProperty<?> property = arguments.find(1, "property", input -> configuration.getConfigurable().getProperties().stream().filter(p -> p.getName().equals(input)).findFirst().orElse(null));
        String action = arguments.matchesAny(2, "action", SET, RESET, INFO);

        switch (action) {
            case SET -> {
                property.onExecute(sender, arguments.subArguments(3));
                sender.sendMessage("Property " + property.getName() + " updated.");
            }
            case RESET -> {
                property.reset();
                sender.sendMessage("Property " + property.getName() + " has been reset.");
            }
            case INFO -> {
                sender.sendMessage("|> " + ChatColor.BLUE + property.getName() + ": " + ChatColor.YELLOW + property.get());
                sender.sendMessage(split(property.getDescription(), 58).toArray(String[]::new));
            }
        }
    }

    private static List<String> split(String text, int max) {
        List<String> words = new ArrayList<>(Arrays.stream(text.split("\\s+")).toList());

        String raw = ChatColor.stripColor(text);
        int width = MinecraftFont.Font.getWidth(raw);
        int copies = (int) Math.ceil((float) width / max);
        List<String> lines = new ArrayList<>(Collections.nCopies(copies, ""));

        for (int i = 0; i < lines.size(); i++) {
            while (words.size() > 0 && lines.get(i).length() + words.get(0).length() <= max) {
                lines.add(i, lines.remove(i) + " " + words.remove(0));
            }
        }

        lines = lines.stream().map(String::trim).collect(Collectors.toList());
        lines.removeIf(l -> l.isEmpty() || l.isBlank());
        return lines.stream().map(line -> "| " + line).toList();
    }
}

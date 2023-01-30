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
            List<ConfigurableProperty<?, ?>> properties = configuration.getConfigurable().getProperties();

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
        ConfigurableProperty<?, ?> property = arguments.find(1, "property", input -> configuration.getConfigurable().getProperties().stream().filter(p -> p.getName().equals(input)).findFirst().orElse(null));
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
                sender.sendMessage(ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET + ": " + ChatColor.YELLOW + property.get());
                sender.sendMessage(split(property.getDescription(), 60, "  "));
            }
        }
    }

    private static String[] split(String text, int loggerWidth, String prefix) {
        int targetWidth = loggerWidth - prefix.length();
        List<String> words = Arrays.stream(text.split("\\s+")).collect(Collectors.toList());

        String raw = ChatColor.stripColor(text);
        int textWidth = MinecraftFont.Font.getWidth(raw);
        int lineCount = (int) Math.ceil((float) textWidth / targetWidth);
        List<String> lines = new ArrayList<>(Collections.nCopies(lineCount, ""));

        for (int i = 0; i < lines.size(); i++) {
            while (words.size() > 0 && lines.get(i).length() + words.get(0).length() <= targetWidth) {
                lines.add(i, lines.remove(i) + " " + words.remove(0));
            }
        }
        return lines.stream().map(l -> prefix + l.trim()).filter(l -> !l.isBlank()).toArray(String[]::new);
    }
}

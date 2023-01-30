package com.azalealibrary.configuration;

import com.azalealibrary.configuration.property.ConfigurableProperty;
import com.azalealibrary.configuration.property.ListProperty;
import org.bukkit.ChatColor;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class TextUtil {

    public static String getName(ConfigurableProperty<?, ?> property) {
        return ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET;
    }

    public static List<String> printable(ConfigurableProperty<?, ?> property, int width) {
        List<String> lines = new ArrayList<>();
        String type = property instanceof ListProperty<?> ? "List of " + property.getType().getExpected() : property.getType().getExpected();
        lines.add("Property: " + getName(property) + " (" + type + ")");
        lines.add("> Required: " + (property.isRequired() ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
        lines.add("> Default: " + ChatColor.AQUA + property.getDefault().toString());
        lines.add("> Value: " + ChatColor.YELLOW + property);
        lines.addAll(split(property.getDescription(), width, "  ").stream().map(line -> ChatColor.GRAY + line).toList());
        return lines;
    }

    public static List<String> split(String text, int loggerWidth, String prefix) {
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
        return lines.stream().map(line -> prefix + line.trim()).filter(line -> !line.isBlank()).collect(Collectors.toList());
    }
}

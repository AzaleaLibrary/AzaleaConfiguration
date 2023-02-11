package net.azalealibrary.configuration;

import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConfigurationApi {

    private static final List<Configurable> CONFIGURABLES = new ArrayList<>();

    public static List<Configurable> getConfigurables() {
        return CONFIGURABLES;
    }

    public static <C extends Configurable> C register(C configurable) {
        if (CONFIGURABLES.stream().anyMatch(c -> c.getName().equals(configurable.getName()))) {
            throw new AzaleaException("Configuration with name '" + configurable.getName() + "' already exists.");
        }

        CONFIGURABLES.add(configurable);
        return configurable;
    }

    public static <C extends Configurable> C unregister(C configurable) {
        if (CONFIGURABLES.stream().noneMatch(c -> c.getName().equals(configurable.getName()))) {
            throw new AzaleaException("Configuration with name '" + configurable.getName() + "' does not exists.");
        }

        CONFIGURABLES.remove(configurable);
        return configurable;
    }

    public static List<FileConfiguration> load(Plugin plugin, String path) {
        return load(plugin, new File(plugin.getDataFolder(), path));
    }

    public static List<FileConfiguration> load(Plugin plugin, File directory) {
        if (!directory.exists() & !directory.mkdir() || !directory.isDirectory()) {
            throw new AzaleaException(directory.getName() + " is not a directory.");
        }

        File[] files = directory.listFiles(file -> {
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            String extension = dotIndex > 0 ? name.substring(dotIndex + 1) : "";
            return extension.equals("yaml") || extension.equals("yml");
        });
        return files != null ? Arrays.stream(files).map(file -> new FileConfiguration(plugin, file)).toList() : List.of();
    }

    public static FileConfiguration create(Plugin plugin, String path, Configurable configurable) {
        return create(plugin, new File(plugin.getDataFolder(), path), configurable);
    }

    public static FileConfiguration create(Plugin plugin, File directory, Configurable configurable) {
        return new FileConfiguration(plugin, new File(directory, configurable.getName() + ".yml"));
    }
}

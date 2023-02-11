package net.azalealibrary.configuration;

import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ConfigurationApi {

    private static final List<Configuration> CONFIGURATIONS = new ArrayList<>();

    public static List<Configuration> getConfigurations() {
        return CONFIGURATIONS;
    }

    public static @Nullable Configuration getConfiguration(String name) {
        return CONFIGURATIONS.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(null);
    }

    public static <C extends Configurable> C register(C configurable) {
        Configuration configuration = getConfiguration(configurable.getName());

        if (configuration != null) {
            configuration.merge(configurable);
        } else {
            Configuration newConfiguration = new Configuration(configurable.getName());
            newConfiguration.merge(configurable);
            CONFIGURATIONS.add(newConfiguration);
        }
        return configurable;
    }

    public static <C extends Configurable> C unregister(C configurable) {
        Configuration configuration = getConfiguration(configurable.getName());

        if (configuration != null) {
            configuration.unmerge(configurable);
        }
        return configurable;
    }

    public static FileConfiguration load(Plugin plugin, String name) {
        return load(plugin, "/", name);
    }

    public static FileConfiguration load(Plugin plugin, String path, String name) {
        File directory = new File(plugin.getDataFolder(), path);

        if (!directory.exists() & !directory.mkdir() || !directory.isDirectory()) {
            throw new AzaleaException(directory.getName() + " is not a directory.");
        }

        File file = new File(directory, name + ".yml");

        try {
            if (!file.exists() & !file.createNewFile()) {
                throw new AzaleaException(directory.getName() + " is not a directory.");
            }
        } catch (Exception exception) {
            throw new AzaleaException("Could not create configuration file '" + name + "'.", exception.getMessage());
        }
        return new FileConfiguration(plugin, file);
    }

    public static List<FileConfiguration> loadAll(Plugin plugin) {
        return loadAll(plugin, "/");
    }

    public static List<FileConfiguration> loadAll(Plugin plugin, String path) {
        File directory = new File(plugin.getDataFolder(), path);

        if (!directory.exists() & !directory.mkdir() || !directory.isDirectory()) {
            throw new AzaleaException(directory.getName() + " is not a directory.");
        }

        File[] files = directory.listFiles(file -> {
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            String extension = dotIndex > 0 ? name.substring(dotIndex + 1) : "";
            return extension.equals("yaml") || extension.equals("yml");
        });
        return files != null ? Arrays.stream(files).map(f -> new FileConfiguration(plugin, f)).toList() : List.of();
    }
}

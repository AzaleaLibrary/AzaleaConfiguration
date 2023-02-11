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
        return files != null ? Arrays.stream(files).map(f -> new FileConfiguration(plugin, f)).toList() : List.of();
    }

    public static FileConfiguration create(Plugin plugin, String path, String name) {
        return create(plugin, new File(plugin.getDataFolder(), path), name);
    }

    public static FileConfiguration create(Plugin plugin, File directory, String name) {
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
}

package com.azalealibrary.configuration;

import com.google.common.collect.ImmutableList;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public final class ConfigurationApi {

    private static final List<FileConfiguration> CONFIGURATIONS = new ArrayList<>();

    public static ImmutableList<FileConfiguration> getConfigurations() {
        return ImmutableList.copyOf(CONFIGURATIONS);
    }

    public static List<FileConfiguration> registerAll(Plugin plugin, Supplier<Configurable> supplier) {
        return registerAll(plugin, plugin.getDataFolder(), supplier);
    }

    public static List<FileConfiguration> registerAll(Plugin plugin, String path, Supplier<Configurable> supplier) {
        return registerAll(plugin, new File(plugin.getDataFolder(), path), supplier);
    }

    public static List<FileConfiguration> registerAll(Plugin plugin, File directory, Supplier<Configurable> supplier) {
        if (!directory.isDirectory()) {
            throw new AzaleaException(directory + " is not a directory.");
        } else if (!directory.exists()) {
            directory.mkdir();
        }

        List<FileConfiguration> configurations = new ArrayList<>();
        File[] files = directory.listFiles(file -> {
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            String extension = dotIndex > 0 ? name.substring(dotIndex + 1) : "";
            return extension.equals("yaml") || extension.equals("yml");
        });

        if (files != null) {
            for (File file : files) {
                configurations.add(register(new FileConfiguration(plugin, supplier.get(), file)));
            }
        }
        return configurations;
    }

    public static FileConfiguration register(Plugin plugin, String name, Configurable configurable) {
        return register(new FileConfiguration(plugin, configurable, name));
    }

    public static FileConfiguration register(FileConfiguration configuration) {
        if (CONFIGURATIONS.contains(configuration)) {
            throw new AzaleaException("Configuration '" + configuration.getName() + "' already exists. (" + configuration + ")");
        }
        CONFIGURATIONS.add(configuration);
        return configuration;
    }
}

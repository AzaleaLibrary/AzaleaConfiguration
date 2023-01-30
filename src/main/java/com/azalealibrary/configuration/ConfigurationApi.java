package com.azalealibrary.configuration;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ConfigurationApi {

    private static final List<FileConfiguration> CONFIGURATIONS = new ArrayList<>();

    public static void register(final String name, final JavaPlugin plugin, final Configurable configurable) {
        FileConfiguration configuration = new FileConfiguration(name, plugin, configurable);

        if (CONFIGURATIONS.contains(configuration)) {
            throw new RuntimeException("Configuration '" + name + "' already exists.");
        }
        CONFIGURATIONS.add(configuration);
    }

    public static List<FileConfiguration> getConfigurations() {
        return CONFIGURATIONS;
    }
}

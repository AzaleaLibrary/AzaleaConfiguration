package com.azalealibrary.configuration;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ConfigurationApi {

    private static final List<FileConfiguration> CONFIGURATIONS = new ArrayList<>();

    public static List<FileConfiguration> getConfigurations() {
        return CONFIGURATIONS;
    }

    public static void register(String name, JavaPlugin plugin, Configurable configurable) {
        FileConfiguration fileConfiguration = new FileConfiguration(name, plugin, configurable);

        if (CONFIGURATIONS.contains(fileConfiguration)) {
            throw new RuntimeException("Configuration '" + name + "' already exists. (" + fileConfiguration + ")");
        }
        CONFIGURATIONS.add(fileConfiguration);
    }
}

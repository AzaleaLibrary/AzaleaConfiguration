package com.azalealibrary.configuration;

import com.azalealibrary.configuration.command.CommandNode;
import com.azalealibrary.configuration.command.ConfigureCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Plugin(name = "AzaleaConfiguration", version = "1.0")
public final class AzaleaConfiguration extends JavaPlugin {

    private static final List<FileConfiguration> CONFIGURATIONS = new ArrayList<>();

    public AzaleaConfiguration() { }

    public AzaleaConfiguration(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    public static List<FileConfiguration> getConfigurations() {
        return CONFIGURATIONS;
    }

    @Override
    public void onLoad() {
        CommandNode.register(this, ConfigureCommand.class);
    }

    @Override
    public void onEnable() {
        CONFIGURATIONS.forEach(FileConfiguration::load);
    }

    @Override
    public void onDisable() {
        CONFIGURATIONS.forEach(FileConfiguration::save);
    }

    public static void register(String name, JavaPlugin plugin, Configurable configurable) {
        FileConfiguration configuration = new FileConfiguration(name, plugin, configurable);

        if (CONFIGURATIONS.contains(configuration)) {
            throw new RuntimeException("Configuration '" + name + "' already exists.");
        }
        CONFIGURATIONS.add(configuration);
    }
}

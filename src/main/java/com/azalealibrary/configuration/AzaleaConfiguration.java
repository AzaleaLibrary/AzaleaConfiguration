package com.azalealibrary.configuration;

import com.azalealibrary.configuration.command.CommandNode;
import com.azalealibrary.configuration.command.ConfigureCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.io.File;

@Plugin(name = "AzaleaConfiguration", version = "0.0.1")
public final class AzaleaConfiguration extends JavaPlugin {

    public AzaleaConfiguration() { }

    public AzaleaConfiguration(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        CommandNode.register(this, ConfigureCommand.class);
    }

    @Override
    public void onEnable() {
        ConfigurationApi.getConfigurations().forEach(FileConfiguration::load);
    }

    @Override
    public void onDisable() {
        ConfigurationApi.getConfigurations().forEach(FileConfiguration::save);
    }
}

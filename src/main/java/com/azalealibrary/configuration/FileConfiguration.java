package com.azalealibrary.configuration;

import com.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class FileConfiguration {

    private final Plugin plugin;
    private final File file;
    private final YamlConfiguration configuration;

    public FileConfiguration(Plugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public String getConfigurationName() {
        return file.getName();
    }

    public YamlConfiguration getYamlConfiguration() {
        return configuration;
    }

    public void load(Configurable configurable) {
        try {
            plugin.getLogger().log(Level.INFO, "Loading '" + configurable.getName() + "' data.");

            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.deserialize(configuration);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not getConfigurables '" + configurable.getName() + "' data.");
        }
    }

    public void save(Configurable configurable) {
        try {
            plugin.getLogger().log(Level.INFO, "Saving '" + configurable.getName() + "' data.");

            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.serialize(configuration);
                List<String> comments = TextUtil.printable(property, 80).stream().map(ChatColor::stripColor).toList();
                configuration.setComments(property.getName(), comments);
            }
            configuration.save(file);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not save '" + configurable.getName() + "'.");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FileConfiguration fileConfiguration) {
            return plugin.equals(fileConfiguration.plugin) && file.equals(fileConfiguration.file);
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return String.format("path:%s, plugin:%s", file.getPath(), plugin.getName());
    }
}

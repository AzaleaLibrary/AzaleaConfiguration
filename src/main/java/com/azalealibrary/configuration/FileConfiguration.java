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
    private final Configurable configurable;
    private final File file;

    public FileConfiguration(Plugin plugin, Configurable configurable, String name) {
        this(plugin, configurable, new File(plugin.getDataFolder(), name + ".yml"));
    }

    public FileConfiguration(Plugin plugin, Configurable configurable, File file) {
        this.plugin = plugin;
        this.configurable = configurable;
        this.file = file;
    }

    public String getName() {
        return file.getName().substring(0, file.getName().lastIndexOf('.'));
    }

    public Configurable getConfigurable() {
        return configurable;
    }

    public void load() {
        plugin.getLogger().log(Level.INFO, "Loading '" + getName() + "' data.");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.deserialize(config);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not load '" + getName() + "' data.");
        }
    }

    public void save() {
        plugin.getLogger().log(Level.INFO, "Saving '" + getName() + "' data.");
        YamlConfiguration config = new YamlConfiguration();

        try {
            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.serialize(config);
                List<String> comments = TextUtil.printable(property, 80).stream().map(ChatColor::stripColor).toList();
                config.setComments(property.getName(), comments);
            }
            config.save(file);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not save '" + getName() + "'.");
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FileConfiguration fileConfiguration) {
            return file.equals(fileConfiguration.file) && plugin.equals(fileConfiguration.plugin);
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return String.format("path:%s, plugin:%s", file.getPath(), plugin.getName());
    }
}

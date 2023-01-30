package com.azalealibrary.configuration;

import com.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class FileConfiguration {

    private final String name;
    private final JavaPlugin plugin;
    private final Configurable configurable;

    public FileConfiguration(String name, JavaPlugin plugin, Configurable configurable) {
        this.name = name;
        this.plugin = plugin;
        this.configurable = configurable;
    }

    public String getName() {
        return name;
    }

    public Configurable getConfigurable() {
        return configurable;
    }

    public void load() {
        loadFromFile(getConfigFile());
    }

    public void loadFromFile(final File file) {
        plugin.getLogger().log(Level.INFO, "Loading '" + name + "' data.");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        try {
            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.deserialize(config);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not loadFromFile '" + name + "' data.");
        }
    }

    public void save() {
        saveToFile(getConfigFile());
    }

    public void saveToFile(final File file) {
        plugin.getLogger().log(Level.INFO, "Saving '" + name + "' data.");
        YamlConfiguration config = new YamlConfiguration();

        try {
            for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
                property.serialize(config);
                List<String> comments = TextUtil.printable(property, 80).stream().map(ChatColor::stripColor).toList();
                config.setComments(property.getName(), comments);
            }
            config.save(file);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not save '" + name + "'.");
        }
    }

    private File getConfigFile() {
        File file = new File(plugin.getDataFolder(), name + ".yml");

        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    plugin.getLogger().warning("Could not create file for configuration '" + name + "'.");
                }
            } catch (Exception exception) {
                plugin.getLogger().log(Level.WARNING, exception, () -> "Could not create file for configuration '" + name + "'.");
            }
        }
        return file;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof FileConfiguration fileConfiguration) {
            return name.equals(fileConfiguration.name) && plugin.equals(fileConfiguration.plugin);
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return String.format("name:%s, owner:%s", name, plugin.getName());
    }
}

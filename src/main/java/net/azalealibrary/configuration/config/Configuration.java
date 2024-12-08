package net.azalealibrary.configuration.config;

import net.azalealibrary.command.AzaleaException;
import net.azalealibrary.command.TextUtil;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class Configuration {

    private final JavaPlugin plugin;
    private final String name;
    private final File file;
    private final YamlConfiguration configuration;

    public Configuration(JavaPlugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;
        this.file = getFile();
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

    public String getName() {
        return name;
    }

    public void load() {
        try {
            plugin.getLogger().log(Level.INFO, "Loading '" + getName() + "' data.");

            for (ConfigurableProperty<?, ?> property : getProperties()) {
                property.deserialize(configuration);
            }
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not load '" + getName() + "' data.");
        }
    }

    public void save() {
        try {
            plugin.getLogger().log(Level.INFO, "Saving '" + getName() + "' data.");

            for (ConfigurableProperty<?, ?> property : getProperties()) {
                property.serialize(configuration);
                List<String> comments = new ArrayList<>();
                String type = property.getPropertyType().getExpected() + (property instanceof ListProperty<?> ? " (list)" : "");
                comments.add("Property: " + property.getName() + " of " + type);
                comments.add("Default: " + property.getDefault());
                property.getDescription().forEach(l -> comments.addAll(TextUtil.split(l, 55).stream().map(i -> "  " + i).toList()));
                configuration.setComments(property.getName(), comments);
            }
            configuration.save(file);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.WARNING, exception, () -> "Could not save '" + getName() + "'.");
        }
    }

    private File getFile() {
        try {
            File file = new File(plugin.getDataFolder(), getName() + ".yml");

            if (!plugin.getDataFolder().exists() && plugin.getDataFolder().mkdir() && !file.exists()) {
                file.createNewFile();
                save(); // set initial config data
            }
            return file;
        } catch (Exception exception) {
            throw new AzaleaException("Could not create configuration file '" + file + "'.", exception);
        }
    }

    public List<ConfigurableProperty<?, ?>> getProperties() {
        List<ConfigurableProperty<?, ?>> properties = new ArrayList<>();

        for (Field field : getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigProperty.class)) {
                try {
                    field.setAccessible(true);

                    if (field.get(this) instanceof ConfigurableProperty<?, ?> property) {
                        properties.add(property);
                    }
                } catch (IllegalAccessException ignored) { }
            }
        }
        return properties;
    }
}

package com.azalealibrary.configuration;

import com.azalealibrary.configuration.command.CommandNode;
import com.azalealibrary.configuration.command.ConfigureCommand;
import com.azalealibrary.configuration.property.ConfigurableProperty;
import com.azalealibrary.configuration.property.ListProperty;
import com.azalealibrary.configuration.property.Property;
import com.azalealibrary.configuration.property.PropertyType;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Plugin(name = "AzaleaConfiguration", version = "0.0.1")
public final class AzaleaConfiguration extends JavaPlugin {

    private static final Configurable CONFIGURABLE = new Configurable() {

        private final Property<Integer> number = new Property<>(PropertyType.INTEGER, 21, "number", "In publishing and graphic design, Lorem ipsum is a placeholder text commonly used to demonstrate the visual form of a document or a typeface without relying on meaningful content. Lorem ipsum may be used as a placeholder before final copy is available.", true);
        private final ListProperty<Vector> spawns = new ListProperty<>(PropertyType.VECTOR, ArrayList::new, "spawns", "This is a description too.", false);

        @Override
        public List<ConfigurableProperty<?, ?>> getProperties() {
            return List.of(number, spawns);
        }
    };

    public AzaleaConfiguration() { }

    public AzaleaConfiguration(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        CommandNode.register(this, ConfigureCommand.class);
        ConfigurationApi.register("configs", this, CONFIGURABLE);
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

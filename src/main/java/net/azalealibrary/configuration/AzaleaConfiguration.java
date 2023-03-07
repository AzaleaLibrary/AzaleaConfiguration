package net.azalealibrary.configuration;

import net.azalealibrary.command.AzaleaCommandApi;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Plugin;

import java.io.File;

@SuppressWarnings("unused")
@Plugin(name = "AzaleaConfiguration", version = "1.0")
@Dependency("AzaleaCommand")
@ApiVersion(ApiVersion.Target.v1_19)
public final class AzaleaConfiguration extends JavaPlugin {

    public AzaleaConfiguration() { }

    public AzaleaConfiguration(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public void onLoad() {
        AzaleaCommandApi.register(this, ConfigureCommand.class);
    }
}

// TODO - callback when property is updated?
// TODO - regex to set/update many props with shared name shop.item.*

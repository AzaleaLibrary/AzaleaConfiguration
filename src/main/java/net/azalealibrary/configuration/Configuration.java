package net.azalealibrary.configuration;

import net.azalealibrary.command.AzaleaException;
import net.azalealibrary.configuration.property.ConfigurableProperty;

import java.util.ArrayList;
import java.util.List;

public class Configuration implements Configurable {

    private final String name;
    private final List<ConfigurableProperty<?, ?>> properties;

    public Configuration(String name) {
        this.name = name;
        this.properties = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return properties;
    }

    public void merge(Configurable configurable) {
        if (!configurable.getName().equals(name)) {
            throw new AzaleaException(String.format("Attempting to merge incompatible configurations '%s' into '%s'.", configurable.getName(), name));
        }

        for (ConfigurableProperty<?, ?> property : configurable.getProperties()) {
            if (properties.contains(property)) {
                throw new AzaleaException(String.format("Attempting to merge configuration with existing property: %s", property.getName()));
            }
            properties.add(property);
        }
    }

    public void unmerge(Configurable configurable) {
        if (!configurable.getName().equals(name)) {
            throw new AzaleaException(String.format("Attempting to unmerge incompatible configurations '%s' into '%s'.", configurable.getName(), name));
        }
        properties.removeAll(configurable.getProperties());
    }
}

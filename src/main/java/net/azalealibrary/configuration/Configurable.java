package net.azalealibrary.configuration;

import net.azalealibrary.configuration.property.ConfigurableProperty;

import java.util.List;
import java.util.Optional;

public interface Configurable {

    String getName();

    List<ConfigurableProperty<?, ?>> getProperties();

    default Optional<ConfigurableProperty<?, ?>> getProperty(ConfigurableProperty<?, ?> property) {
        return getProperties().stream().filter(p -> p.equals(property)).findFirst();
    }

    @SuppressWarnings("unchecked")
    default <T, P> T getValue(ConfigurableProperty<T, P> property) {
        return (T) getProperty(property).orElseThrow(() -> new RuntimeException("Could not find property '" + property.getName() + "'.")).get();
    }

    @SuppressWarnings("unchecked")
    default <T, P> void setValue(ConfigurableProperty<T, P> property, T value) {
        ((ConfigurableProperty<T, P>) getProperty(property).orElseThrow(() -> new RuntimeException("Could not find property '" + property.getName() + "'."))).set((P) value);
    }
}

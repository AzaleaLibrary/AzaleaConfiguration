package com.azalealibrary.configuration;

import com.azalealibrary.configuration.property.ConfigurableProperty;

import java.util.List;
import java.util.Optional;

public interface Configurable {

    List<ConfigurableProperty<?>> getProperties();

    default Optional<ConfigurableProperty<?>> getProperty(ConfigurableProperty<?> property) {
        return getProperties().stream().filter(p -> p.equals(property)).findFirst();
    }

    @SuppressWarnings("unchecked")
    default <T> T getValue(ConfigurableProperty<T> property) {
        return (T) getProperty(property).orElseThrow(() -> new RuntimeException("Could not find property '" + property.getName() + "'.")).get();
    }

    @SuppressWarnings("unchecked")
    default <T> void setValue(ConfigurableProperty<T> property, T value) {
        ((ConfigurableProperty<T>) getProperty(property).orElseThrow(() -> new RuntimeException("Could not find property '" + property.getName() + "'."))).set(value);
    }
}

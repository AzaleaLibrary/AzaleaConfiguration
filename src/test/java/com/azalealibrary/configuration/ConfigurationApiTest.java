package com.azalealibrary.configuration;

import be.seeseemelk.mockbukkit.MockBukkit;
import com.azalealibrary.configuration.property.ListProperty;
import com.azalealibrary.configuration.property.ConfigurableProperty;
import com.azalealibrary.configuration.property.Property;
import com.azalealibrary.configuration.property.PropertyType;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ConfigurationApiTest {

    private AzaleaConfiguration plugin;
    private Configurable configurable;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(AzaleaConfiguration.class);
        configurable = new Configurable() {
            private final Property<Integer> aNumber = new Property<>(PropertyType.INTEGER, 21, "aNumber", "This is a number", true);
            private final Property<Boolean> aBoolean = new Property<>(PropertyType.BOOLEAN, true, "aBoolean", "This is a boolean", true);
            private final ListProperty<Vector> someVectors = new ListProperty<>(PropertyType.VECTOR, ArrayList::new, "someVectors", "This is a list of vectors.", true);

            @Override
            public List<ConfigurableProperty<?, ?>> getProperties() {
                return List.of(aNumber, aBoolean, someVectors);
            }
        };
    }

    @AfterEach
    public void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void register() {
        Assertions.assertTrue(ConfigurationApi.getConfigurations().isEmpty());

        ConfigurationApi.register("configs", plugin, configurable);
        Assertions.assertEquals(1, ConfigurationApi.getConfigurations().size());

        Assertions.assertThrows(RuntimeException.class, () -> ConfigurationApi.register("configs", plugin, configurable));
        Assertions.assertDoesNotThrow(() -> ConfigurationApi.register("configs_too", plugin, configurable));
        Assertions.assertEquals(2, ConfigurationApi.getConfigurations().size());
    }
}
package com.azalealibrary.configuration.property;

import com.azalealibrary.configuration.command.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class ConfigurableProperty<T, P> {

    private final PropertyType<T> type;
    private final List<AssignmentPolicy<T>> policies;

    private final String name;
    private final String description;
    private final boolean required;
    private final Supplier<P> defaultValue;
    private @Nullable P value;

    @SafeVarargs
    protected ConfigurableProperty(PropertyType<T> type, Supplier<P> defaultValue, String name, String description, boolean required, AssignmentPolicy<T>... policies) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.required = required;
        this.defaultValue = defaultValue;
        this.policies = List.of(policies);
    }

    public PropertyType<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public P getDefault() {
        return defaultValue.get();
    }

    public P get() {
        return Optional.ofNullable(value).orElseGet(defaultValue);
    }

    public void set(P value) {
        this.value = value;
    }

    public void reset() {
        value = defaultValue.get();
    }

    public boolean isSet() {
        return get() != null;
    }

    protected T verify(@Nullable T value) throws RuntimeException {
        AssignmentPolicy<T> failedCheck = policies.stream()
                .filter(validator -> !validator.canAssign(value))
                .findAny().orElse(null);

        if (failedCheck != null) {
            throw new RuntimeException(failedCheck.getMessage(value));
        }
        return value;
    }

    public abstract void set(CommandSender sender, Arguments arguments);

    public abstract List<String> get(CommandSender sender, Arguments arguments);

    public abstract void serialize(@Nonnull ConfigurationSection configuration);

    public abstract void deserialize(@Nonnull ConfigurationSection configuration);

    @Override
    public boolean equals(Object object) {
        if (object instanceof ConfigurableProperty<?, ?> property) {
            return property.name.equals(name) && property.type.getType().equals(type.getType());
        }
        return super.equals(object);
    }
}

package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ConfigurableProperty<T, P> {

    protected final PropertyType<T> propertyType;
    protected final List<AssignmentPolicy<T>> policies;
    protected final String name;
    protected final Consumer<P> callback;
    protected final List<String> description;
    protected final Supplier<P> defaultValue;
    protected @Nullable P value;

    protected ConfigurableProperty(PropertyType<T> propertyType, Supplier<P> defaultValue, String name, List<String> description, Consumer<P> callback, List<AssignmentPolicy<T>> policies) {
        this.propertyType = propertyType;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.callback = callback;
        this.policies = policies;
        this.value = defaultValue.get();
    }

    public PropertyType<T> getPropertyType() {
        return propertyType;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
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
            throw new AzaleaException(failedCheck.getMessage(value));
        }
        return value;
    }

    public void onExecute(CommandSender sender, Arguments arguments) {
        set(sender, arguments);
    }

    public List<String> onComplete(CommandSender sender, Arguments arguments) {
        return propertyType.complete(sender, arguments);
    }

    protected abstract void set(CommandSender sender, Arguments arguments);

    public abstract void serialize(@Nonnull ConfigurationSection configuration);

    public abstract void deserialize(@Nonnull ConfigurationSection configuration);
}

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

    protected final PropertyType<T> type;
    protected final List<AssignmentPolicy<T>> policies;
    protected final String name;
    protected final Consumer<P> callback;
    private final String description;
    private final Supplier<P> defaultValue;
    private @Nullable P value;

    protected ConfigurableProperty(PropertyType<T> type, Supplier<P> defaultValue, String name, String description, Consumer<P> callback, List<AssignmentPolicy<T>> policies) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
        this.callback = callback;
        this.policies = policies;
        this.value = defaultValue.get();
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

    @SuppressWarnings("unchecked")
    public List<String> onComplete(CommandSender sender, Arguments arguments) {
        return type.complete(sender, arguments, (T) get());
    }

    protected abstract void set(CommandSender sender, Arguments arguments);

    public abstract void serialize(@Nonnull ConfigurationSection configuration);

    public abstract void deserialize(@Nonnull ConfigurationSection configuration);
}

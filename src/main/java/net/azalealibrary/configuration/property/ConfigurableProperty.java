package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import org.apache.commons.lang.StringUtils;
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
    private final Supplier<P> defaultValue;
    private @Nullable P value;

    @SafeVarargs
    protected ConfigurableProperty(PropertyType<T> type, Supplier<P> defaultValue, String name, String description, AssignmentPolicy<T>... policies) {
        this.type = type;
        this.name = name;
        this.description = description;
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
        try {
            set(sender, arguments);
        } catch (Exception exception) {
            throw new AzaleaException(
                    "Error updating property '" + name + "' with arguments: " + arguments + ".",
                    StringUtils.capitalize(type.getExpected()) + " expected."
            );
        }
    }

    @SuppressWarnings("unchecked")
    public List<String> onComplete(CommandSender sender, Arguments arguments) {
        return type.complete(sender, arguments, (T) get());
    }

    protected abstract void set(CommandSender sender, Arguments arguments);

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

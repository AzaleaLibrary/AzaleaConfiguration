package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Property<T> extends ConfigurableProperty<T, T> {

    @SafeVarargs
    public Property(PropertyType<T> type, Supplier<T> defaultValue, String name, AssignmentPolicy<T>... policies) {
        this(type, defaultValue, name, name, v -> {}, policies);
    }

    @SafeVarargs
    public Property(PropertyType<T> type, Supplier<T> defaultValue, String name, Consumer<T> callback, AssignmentPolicy<T>... policies) {
        this(type, defaultValue, name, name, callback, policies);
    }

    @SafeVarargs
    public Property(PropertyType<T> type, Supplier<T> defaultValue, String name, String description, Consumer<T> callback, AssignmentPolicy<T>... policies) {
        super(type, defaultValue, name, description, callback, policies);
    }

    @Override
    protected void set(CommandSender sender, Arguments arguments) {
        set(verify(getType().parse(sender, arguments, get())));
        callback.accept(get());
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), getType().serialize(value)));
    }

    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(configuration.get(getName())).ifPresent(object -> set(getType().deserialize(object)));
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Property<?> property) {
            return property.name.equals(name) && property.type.getType().equals(type.getType());
        }
        return super.equals(object);
    }

    @Override
    public String toString() {
        return isSet() ? getType().print(get()) : "<empty>";
    }
}

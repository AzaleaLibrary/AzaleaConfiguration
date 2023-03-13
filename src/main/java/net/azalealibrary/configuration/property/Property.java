package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class Property<T> extends ConfigurableProperty<T, T> {

    private Property(PropertyType<T> type, Supplier<T> defaultValue, String name, String description, Consumer<T> callback, List<AssignmentPolicy<T>> policies) {
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

    public static <T> Builder<T> create(String name, PropertyType<T> type, Supplier<T> defaultValue) {
        return new Builder<>(name, type, defaultValue);
    }

    public static final class Builder<T> {

        private final String name;
        private final PropertyType<T> type;
        private final Supplier<T> defaultValue;
        private final List<AssignmentPolicy<T>> policies = new ArrayList<>();
        private String description;
        private Consumer<T> callback;

        private Builder(String name, PropertyType<T> type, Supplier<T> defaultValue) {
            this.name = name;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        public Builder<T> addPolicy(AssignmentPolicy<T> policy) {
            this.policies.add(policy);
            return this;
        }

        public Builder<T> addDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder<T> onChange(Consumer<T> callback) {
            this.callback = callback;
            return this;
        }

        public Property<T> done() {
            return new Property<>(type, defaultValue, name, description, callback, policies);
        }
    }
}

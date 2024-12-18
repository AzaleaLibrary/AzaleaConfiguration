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

    private Property(PropertyType<T> type, Supplier<T> defaultValue, String name, List<String> description, boolean editable, Consumer<T> callback, List<AssignmentPolicy<T>> policies) {
        super(type, defaultValue, name, description, editable, callback, policies);
    }

    @Override
    protected void set(CommandSender sender, Arguments arguments) {
        set(verify(propertyType.parse(sender, arguments)));
        callback.accept(get());
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), propertyType.serialize(value)));
    }

    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(configuration.get(getName())).ifPresent(object -> set(propertyType.deserialize(object)));
    }

    @Override
    public String toString() {
        return isSet() ? propertyType.print(get()) : "<empty>";
    }

    public static <T> Builder<T> create(PropertyType<T> type, String name, Supplier<T> defaultValue) {
        if (!name.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Property '" + name + "' contains non-alphanumeric characters.");
        }
        return new Builder<>(type, name, defaultValue);
    }

    public static final class Builder<T> {

        private final PropertyType<T> type;
        private final String name;
        private final Supplier<T> defaultValue;
        private final List<AssignmentPolicy<T>> policies = new ArrayList<>();
        private final List<String> description = new ArrayList<>();
        private Consumer<T> callback;
        private boolean editable = true;

        private Builder(PropertyType<T> type, String name, Supplier<T> defaultValue) {
            this.type = type;
            this.name = name;
            this.defaultValue = defaultValue;
            this.callback = v -> {};
        }

        public Builder<T> addPolicy(AssignmentPolicy<T> policy) {
            this.policies.add(policy);
            return this;
        }

        public Builder<T> description(String line) {
            this.description.add(line);
            return this;
        }

        public Builder<T> onChange(Consumer<T> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<T> locked() {
            editable = false;
            return this;
        }

        public Property<T> done() {
            return new Property<>(type, defaultValue, name, description, editable, callback, policies);
        }
    }
}

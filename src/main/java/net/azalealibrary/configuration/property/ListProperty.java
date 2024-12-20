package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ListProperty<T> extends ConfigurableProperty<T, List<T>> {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String REPLACE = "replace";

    private ListProperty(PropertyType<T> type, Supplier<List<T>> defaultValue, String name, List<String> description, boolean editable, Consumer<List<T>> callback, List<AssignmentPolicy<T>> policies) {
        super(type, defaultValue, name, description, editable, callback, policies);
    }

    @Override
    protected void set(CommandSender sender, Arguments arguments) {
        String action = arguments.matchesAny(0, "list operation", ADD, REMOVE, REPLACE);

        if (action.equals(ADD)) {
            get().add(verify(propertyType.parse(sender, arguments.subArguments(1))));
            callback.accept(get());
        } else {
            int index = arguments.find(1, "position", input -> Integer.parseInt(input.replace("@", "")));

            if (index >= get().size()) {
                throw new AzaleaException("Specified list position " + index +" too large for list of size " + get().size() + ".");
            }

            if (action.equals(REPLACE)) {
                get().set(index, verify(propertyType.parse(sender, arguments.subArguments(2))));
                callback.accept(get());
            } else {
                get().remove(index);
                callback.accept(get());
            }
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return List.of(ADD, REMOVE, REPLACE);
        } else if (!get().isEmpty() && arguments.size() == 2 && !arguments.is(0, ADD)) {
            return List.of("@" + (get().size() - 1));
        } else if (!arguments.is(0, REMOVE)) {
            int offset = arguments.is(0, ADD) ? 0 : 1;
            return arguments.size() == 2 + offset ? propertyType.complete(sender, arguments.subArguments(offset)) : List.of();
        }
        return List.of();
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), value.stream().map(propertyType::serialize).toList()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        List<Object> objects = (List<Object>) configuration.getList(getName());

        if (objects != null) {
            set(objects.stream().map(propertyType::deserialize).collect(Collectors.toList()));
        } else {
            set(new ArrayList<>(getDefault()));
        }
    }

    @Override
    public String toString() {
        return isSet() && !get().isEmpty() ? get().stream().map(propertyType::print).collect(Collectors.joining(", ")) : "<empty>";
    }

    public static <T> Builder<T> create(PropertyType<T> type, String name, Supplier<List<T>> defaultValue) {
        if (!name.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Property '" + name + "' contains non-alphanumeric characters.");
        }
        return new Builder<>(type, name, defaultValue);
    }

    public static final class Builder<T> {

        private final PropertyType<T> type;
        private final String name;
        private final Supplier<List<T>> defaultValue;
        private final List<AssignmentPolicy<T>> policies = new ArrayList<>();
        private final List<String> description = new ArrayList<>();
        private Consumer<List<T>> callback;
        private boolean editable = true;

        private Builder(PropertyType<T> type, String name, Supplier<List<T>> defaultValue) {
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

        public Builder<T> onChange(Consumer<List<T>> callback) {
            this.callback = callback;
            return this;
        }

        public Builder<T> locked() {
            editable = false;
            return this;
        }

        public ListProperty<T> done() {
            return new ListProperty<>(type, defaultValue, name, description, editable, callback, policies);
        }
    }
}

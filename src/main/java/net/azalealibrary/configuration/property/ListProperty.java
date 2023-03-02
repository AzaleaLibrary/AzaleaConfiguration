package net.azalealibrary.configuration.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.AzaleaException;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class ListProperty<T> extends ConfigurableProperty<T, List<T>> {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String REPLACE = "replace";

    @SafeVarargs
    public ListProperty(PropertyType<T> type, Supplier<List<T>> defaultValue, String name, AssignmentPolicy<T>... policies) {
        this(type, defaultValue, name, name, policies);
    }

    @SafeVarargs
    public ListProperty(PropertyType<T> type, Supplier<List<T>> defaultValue, String name, String description, AssignmentPolicy<T>... policies) {
        super(type, defaultValue, name, description, policies);
        set(defaultValue.get());
    }

    @Override
    protected void set(CommandSender sender, Arguments arguments) {
        String action = arguments.matchesAny(0, "list operation", ADD, REMOVE, REPLACE);

        if (action.equals(ADD)) {
            get().add(verify(getType().parse(sender, arguments.subArguments(1), null)));
        } else {
            int index = arguments.find(1, "position", input -> Integer.parseInt(input.replace("@", "")));

            if (index >= get().size()) {
                throw new AzaleaException("Specified list position '" + index +"' too large for list of size " + get().size() + ".");
            }

            if (action.equals(REPLACE)) {
                get().set(index, verify(getType().parse(sender, arguments.subArguments(2), null)));
            } else {
                get().remove(index);
            }
        }
    }

    @Override
    public List<String> onComplete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return List.of(ADD, REMOVE, REPLACE);
        } else if (arguments.size() == 2 && !get().isEmpty() && !arguments.is(0, ADD)) {
            return List.of("@" + (get().size() - 1));
        } else if (arguments.is(0, ADD) || arguments.is(0, REPLACE)) {
            // avoid suggesting more than necessary
            Arguments data = arguments.subArguments(arguments.is(0, ADD) ? 0 : 1);
            List<String> suggestion = getType().complete(sender, data, null);
            return arguments.size() -1 <= suggestion.size() ? suggestion : List.of();
        }
        return List.of();
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), value.stream().map(getType()::serialize).toList()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        List<Object> objects = (List<Object>) configuration.getList(getName());

        if (objects != null) {
            set(objects.stream().map(object -> getType().deserialize(object)).collect(Collectors.toList()));
        } else {
            set(new ArrayList<>(getDefault()));
        }
    }

    @Override
    public String toString() {
        return isSet() ? get().stream().map(getType()::print).collect(Collectors.joining(", ")) : "<empty>";
    }
}

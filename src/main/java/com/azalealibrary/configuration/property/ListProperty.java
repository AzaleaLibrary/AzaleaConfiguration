package com.azalealibrary.configuration.property;

import com.azalealibrary.configuration.command.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ListProperty<T> extends ConfigurableProperty<T, List<T>> {

    private static final String ADD = "add";
    private static final String REMOVE = "remove";
    private static final String REPLACE = "replace";

    @SafeVarargs
    public ListProperty(PropertyType<T> type, List<T> defaultValue, String name, String description, boolean required, AssignmentPolicy<T>... policies) {
        super(type, defaultValue, name, description, required, policies);
    }

    @Override
    public void onExecute(CommandSender sender, Arguments arguments) {
        String action = arguments.matchesAny(0, "list operation", ADD, REMOVE, REPLACE);

        if (action.equals(ADD)) {
            get().add(verify(getType().parse(sender, arguments.subArguments(1), null)));
        } else {
            int index = arguments.find(1, "position", input -> Integer.parseInt(input.replace("@", "")));

            if (index >= get().size()) {
                throw new RuntimeException("Specified list position '" + index +"' too large for list of size " + get().size() + ".");
            }

            if (action.equals(REPLACE)) {
                get().set(index, verify(getType().parse(sender, arguments.subArguments(2), null)));
            } else {
                get().remove(index);
            }
        }
    }

    @Override
    public List<String> onSuggest(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return List.of(ADD, REMOVE, REPLACE);
        } else if (arguments.size() == 2 && !get().isEmpty() && !arguments.is(0, ADD)) {
            return List.of("@" + (get().size() - 1));
        } else if (arguments.is(0, ADD) || arguments.is(0, REPLACE)) {
            // avoid suggesting more than necessary
            Arguments data = arguments.subArguments(arguments.is(0, ADD) ? 0 : 1);
            List<String> suggestion = getType().suggest(sender, data, null);
            return arguments.size() -1 <= suggestion.size() ? suggestion : List.of();
        }
        return List.of();
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), value.stream().map(getType()::toObject).toList()));
    }

    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(configuration.getList(getName())).ifPresent(objects -> objects.forEach(object -> get().add(getType().toValue(object))));
    }

    @Override
    public String toString() {
        return isSet() ? Arrays.toString(get().stream().map(getType()::toString).toArray()) : "<empty>";
    }
}

package com.azalealibrary.configuration.property;

import com.azalealibrary.configuration.command.Arguments;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public final class Property<T> extends ConfigurableProperty<T, T> {

    @SafeVarargs
    public Property(PropertyType<T> type, T defaultValue, String name, String description, boolean required, AssignmentPolicy<T>... policies) {
        super(type, defaultValue, name, description, required, policies);
    }

    @Override
    public void onExecute(CommandSender sender, Arguments arguments) {
        set(verify(getType().parse(sender, arguments, get())));
    }

    @Override
    public List<String> onSuggest(CommandSender sender, Arguments arguments) {
        return getType().suggest(sender, arguments, get());
    }

    @Override
    public void serialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(get()).ifPresent(value -> configuration.set(getName(), getType().toObject(value)));
    }

    @Override
    public void deserialize(@Nonnull ConfigurationSection configuration) {
        Optional.ofNullable(configuration.get(getName())).ifPresent(object -> set(getType().toValue(object)));
    }

    @Override
    public String toString() {
        return isSet() ? getType().toString(get()) : "<empty>";
    }
}

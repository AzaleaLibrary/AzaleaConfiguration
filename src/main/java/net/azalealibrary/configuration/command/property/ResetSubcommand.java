package net.azalealibrary.configuration.command.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configuration;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class ResetSubcommand extends PropertySubcommand {

    public ResetSubcommand(Configuration configuration) {
        super("reset", configuration);
    }

    @Override
    protected boolean isSelected(Arguments arguments, ConfigurableProperty<?, ?> property) {
        return arguments.isEmpty() || property.getName().matches(arguments.get(0).replace("*", ".*"));
    }

    @Override
    protected void execute(CommandSender sender, Arguments arguments, ConfigurableProperty<?, ?> property) {
        property.reset();
        String name = ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET;
        String value = ChatColor.YELLOW + property.get().toString();
        sender.sendMessage("Property " + name + " has been reset to " + value + ".");
    }
}

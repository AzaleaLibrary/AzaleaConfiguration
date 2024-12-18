package net.azalealibrary.configuration.command.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.config.Configuration;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

public class SetSubcommand extends PropertySubcommand {

    public SetSubcommand(Configuration configuration) {
        super("set", configuration);
    }

    @Override
    protected boolean isSelected(Arguments arguments, ConfigurableProperty<?, ?> property) {
        return property.getName().equals(arguments.get(0));
    }

    @Override
    protected void execute(CommandSender sender, Arguments arguments, ConfigurableProperty<?, ?> property) {
        property.onExecute(sender, arguments.subArguments(1));
        String name = ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET;
        String value = ChatColor.YELLOW + property.get().toString();
        sender.sendMessage("Property " + name + " has updated to " + value + ".");
    }
}

package net.azalealibrary.configuration.command.property;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.TextUtil;
import net.azalealibrary.configuration.Configuration;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class InfoSubcommand extends PropertySubcommand {

    public InfoSubcommand(Configuration configuration) {
        super("info", configuration);
    }

    @Override
    protected boolean isSelected(Arguments arguments, ConfigurableProperty<?, ?> property) {
        return arguments.isEmpty() || property.getName().matches(arguments.get(0).replace("*", ".*"));
    }

    @Override
    protected void execute(CommandSender sender, Arguments arguments, ConfigurableProperty<?, ?> property) {
        List<String> info = new ArrayList<>();
        info.add(ChatColor.LIGHT_PURPLE + property.getName() + ChatColor.RESET + "=" + ChatColor.YELLOW + property);
        property.getDescription().stream().flatMap(l -> TextUtil.split(l, 55).stream().map(i -> ChatColor.GRAY + "  " + i)).forEach(info::add);
        sender.sendMessage(info.toArray(String[]::new));
    }
}

package net.azalealibrary.configuration.command;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubcommand extends CommandNode {

    private final Configuration configuration;

    public ReloadSubcommand(Configuration configuration) {
        super("reload");
        this.configuration = configuration;
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        configuration.load();
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Reloaded '" + configuration.getName() + "' to file system.");
    }
}

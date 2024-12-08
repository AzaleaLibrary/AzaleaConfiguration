package net.azalealibrary.configuration.command;

import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.Configuration;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SaveSubcommand extends CommandNode {

    private final Configuration configuration;

    public SaveSubcommand(Configuration configuration) {
        super("save");
        this.configuration = configuration;
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        configuration.save();
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "Saved '" + configuration.getName() + "' to file system.");
    }
}

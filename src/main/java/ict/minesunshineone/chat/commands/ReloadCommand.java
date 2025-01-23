package ict.minesunshineone.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import ict.minesunshineone.chat.SimpleChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ReloadCommand implements CommandExecutor {

    private final SimpleChat plugin;

    public ReloadCommand(SimpleChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.reloadConfig();
        sender.sendMessage(Component.text("SimpleChat配置已重载！").color(TextColor.color(255, 170, 0)));
        return true;
    }
}

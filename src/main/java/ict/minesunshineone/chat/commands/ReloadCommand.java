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
        if (!sender.hasPermission("simplechat.admin.reload")) {
            sender.sendMessage(Component.text("你没有权限执行此命令！")
                    .color(TextColor.color(255, 85, 85)));
            return true;
        }

        plugin.reload();
        sender.sendMessage(Component.text("SimpleChat 配置已重载！")
                .color(TextColor.color(85, 255, 85)));
        return true;
    }
}

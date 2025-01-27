package ict.minesunshineone.chat.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.ChatChannel;
import ict.minesunshineone.chat.channel.impl.PrivateChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class ChannelCommand implements CommandExecutor, TabCompleter {

    private final SimpleChat plugin;

    public ChannelCommand(SimpleChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("该命令只能由玩家执行！").color(TextColor.color(255, 85, 85)));
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            sendChannelList(player);
            return true;
        }

        String channelName = args[0].toLowerCase();
        ChatChannel channel = plugin.getChannelManager().getChannel(channelName);

        if (channel == null) {
            player.sendMessage(Component.text("找不到该频道！").color(TextColor.color(255, 85, 85)));
            return true;
        }

        if (!channel.canJoin(player)) {
            player.sendMessage(Component.text("你没有权限加入该频道！").color(TextColor.color(255, 85, 85)));
            return true;
        }

        if (channel instanceof PrivateChannel) {
            if (args.length < 2) {
                player.sendMessage(Component.text("请指定私聊对象！用法: /channel private <玩家>").color(TextColor.color(255, 85, 85)));
                return true;
            }

            Player target = plugin.getServer().getPlayer(args[1]);
            if (target == null || !target.isOnline()) {
                player.sendMessage(Component.text("找不到该玩家或玩家不在线！").color(TextColor.color(255, 85, 85)));
                return true;
            }

            ((PrivateChannel) channel).setPrivateChat(player, target);
        }

        plugin.getChannelManager().setPlayerChannel(player, channelName);
        player.sendMessage(Component.text("已切换到").color(TextColor.color(85, 255, 85))
                .append(channel.getDisplayName())
                .append(Component.text("！").color(TextColor.color(85, 255, 85))));

        return true;
    }

    private void sendChannelList(Player player) {
        player.sendMessage(Component.text("=== 可用的聊天频道 ===").color(TextColor.color(255, 170, 0)));

        Map<String, ChatChannel> channels = plugin.getChannelManager().getChannels();
        for (ChatChannel channel : channels.values()) {
            if (channel.canJoin(player)) {
                player.sendMessage(Component.text("- ").color(TextColor.color(170, 170, 170))
                        .append(channel.getDisplayName())
                        .append(Component.text(": ").color(TextColor.color(170, 170, 170)))
                        .append(channel.getDescription()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return completions;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            Map<String, ChatChannel> channels = plugin.getChannelManager().getChannels();

            for (ChatChannel channel : channels.values()) {
                if (channel.canJoin(player) && channel.getName().toLowerCase().startsWith(partial)) {
                    completions.add(channel.getName());
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("private")) {
            String partial = args[1].toLowerCase();
            plugin.getServer().getOnlinePlayers().forEach(p -> {
                if (p.getName().toLowerCase().startsWith(partial)) {
                    completions.add(p.getName());
                }
            });
        }

        return completions;
    }
}

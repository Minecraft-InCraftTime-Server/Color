package ict.minesunshineone.chat.channel.impl;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class StaffChannel extends AbstractChatChannel {

    public StaffChannel(SimpleChat plugin) {
        super(plugin,
                "staff",
                "simplechat.channel.staff",
                Component.text("管理频道").color(TextColor.color(255, 85, 85)),
                Component.text("管理员专用的聊天频道").color(TextColor.color(170, 170, 170)));
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // 权限检查
        if (!canJoin(player)) {
            player.sendMessage(Component.text("你没有权限使用管理员频道！").color(TextColor.color(255, 85, 85)));
            return;
        }

        // 替换服务器名称占位符
        String serverName = PlaceholderAPI.setPlaceholders(player, "%server_name%");
        if (serverName.isEmpty()) {
            serverName = plugin.getServer().getName();
        }

        Component message = formatMessage(player, event.message());

        if (isCrossServer()) {
            // 发送跨服消息
            plugin.getCrossServerManager().sendCrossServerMessage(getName(), player, message);
            // 在本地显示消息
            sendMessage(player, message);
        } else {
            sendMessage(player, message);
        }

        event.setCancelled(true);
    }

    @Override
    protected boolean canReceiveMessage(Player player) {
        return player.hasPermission(getPermission());
    }

    @Override
    public boolean isCrossServer() {
        return true;
    }
}

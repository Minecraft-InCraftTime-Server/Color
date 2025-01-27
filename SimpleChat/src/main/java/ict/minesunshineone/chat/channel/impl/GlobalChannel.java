package ict.minesunshineone.chat.channel.impl;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class GlobalChannel extends AbstractChatChannel {

    public GlobalChannel(SimpleChat plugin) {
        super(plugin,
                "global",
                "simplechat.channel.global",
                Component.text("全局频道").color(TextColor.color(85, 170, 255)),
                Component.text("跨服务器的全局聊天频道").color(TextColor.color(170, 170, 170)));
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        if (!canJoin(event.getPlayer())) {
            event.getPlayer().sendMessage(Component.text("你没有权限使用全局频道！").color(TextColor.color(255, 85, 85)));
            return;
        }

        // 替换服务器名称占位符
        String serverName = PlaceholderAPI.setPlaceholders(event.getPlayer(), "%server_name%");
        if (serverName.isEmpty()) {
            serverName = plugin.getServer().getName();
        }

        Component message = formatMessage(event.getPlayer(), event.message());

        if (isCrossServer()) {
            // 发送跨服消息
            plugin.getCrossServerManager().sendCrossServerMessage(getName(), event.getPlayer(), message);
            // 在本地显示消息
            sendMessage(event.getPlayer(), message);
        } else {
            sendMessage(event.getPlayer(), message);
        }

        event.setCancelled(true);
    }

    @Override
    public boolean isCrossServer() {
        return true;
    }

    @Override
    protected boolean canReceiveMessage(Player player) {
        return canJoin(player);
    }
}

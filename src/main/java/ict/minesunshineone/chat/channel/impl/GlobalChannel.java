package ict.minesunshineone.chat.channel.impl;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import ict.minesunshineone.chat.config.ChannelConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;

public class GlobalChannel extends AbstractChatChannel {

    private final boolean crossServer;

    public GlobalChannel(SimpleChat plugin) {
        super(plugin,
                "global",
                "simplechat.channel.global",
                Component.text("全局频道"),
                Component.text("跨服务器的全局聊天频道"));

        ChannelConfig config = ChannelConfig.load(plugin, "global");
        if (config != null) {
            this.format = config.getFormat();
            this.crossServer = config.isCrossServer();
        } else {
            this.format = "&b[全局] &7[%server_name%] %luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%";
            this.crossServer = true;
        }
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        if (!canJoin(event.getPlayer())) {
            event.getPlayer().sendMessage(Component.text("你没有权限使用全局频道！"));
            return;
        }

        Component message = formatMessage(event.getPlayer(), event.message());

        if (crossServer) {
            plugin.getCrossServerManager().sendCrossServerMessage(getName(), event.getPlayer(), message);
        } else {
            sendMessage(event.getPlayer(), message);
        }

        event.setCancelled(true);
    }

    @Override
    public boolean isCrossServer() {
        return crossServer;
    }
}

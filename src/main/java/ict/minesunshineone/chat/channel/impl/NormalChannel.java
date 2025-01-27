package ict.minesunshineone.chat.channel.impl;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class NormalChannel extends AbstractChatChannel {

    public NormalChannel(SimpleChat plugin) {
        super(plugin,
                "normal",
                null,
                Component.text("普通频道").color(TextColor.color(85, 255, 85)),
                Component.text("服务器的公共聊天频道").color(TextColor.color(170, 170, 170)));
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        Component message = formatMessage(event.getPlayer(), event.message());
        sendMessage(event.getPlayer(), message);
        event.setCancelled(true);
    }
}

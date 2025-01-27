package ict.minesunshineone.chat.channel.impl;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class StaffChannel extends AbstractChatChannel {

    public StaffChannel(SimpleChat plugin) {
        super(plugin,
                "staff",
                "simplechat.channel.staff",
                Component.text("管理频道").color(TextColor.color(255, 85, 85)),
                Component.text("管理员专用的聊天频道").color(TextColor.color(170, 170, 170)));
        this.format = "&c[Staff] " + plugin.getConfig().getString("chat.format", "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        if (!canJoin(event.getPlayer())) {
            event.getPlayer().sendMessage(Component.text("你没有权限使用管理员频道！").color(TextColor.color(255, 85, 85)));
            return;
        }

        Component message = formatMessage(event.getPlayer(), event.message());
        sendMessage(event.getPlayer(), message);
        event.setCancelled(true);
    }

    @Override
    protected boolean canReceiveMessage(Player player) {
        return player.hasPermission(getPermission());
    }
}

package ict.minesunshineone.chat.channel.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PrivateChannel extends AbstractChatChannel {

    private final Map<UUID, UUID> privateChats = new HashMap<>();

    public PrivateChannel(SimpleChat plugin) {
        super(plugin,
                "private",
                null,
                Component.text("私聊频道").color(TextColor.color(255, 170, 0)),
                Component.text("与其他玩家私聊的频道").color(TextColor.color(170, 170, 170)));
        this.format = "&6[私聊] &e%player_name% &6-> &e%target_name% &6>>&r %message%";
    }

    public void setPrivateChat(Player sender, Player target) {
        privateChats.put(sender.getUniqueId(), target.getUniqueId());
    }

    public Player getPrivateChatTarget(Player sender) {
        UUID targetUUID = privateChats.get(sender.getUniqueId());
        if (targetUUID == null) {
            return null;
        }
        return plugin.getServer().getPlayer(targetUUID);
    }

    public void removePrivateChat(Player player) {
        privateChats.remove(player.getUniqueId());
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Player target = getPrivateChatTarget(sender);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text("你没有选择私聊对象，或者对方已离线！").color(TextColor.color(255, 85, 85)));
            return;
        }

        String formatStr = format.replace("%target_name%", target.getName());
        this.format = formatStr;

        Component message = formatMessage(sender, event.message());
        sender.sendMessage(message);
        target.sendMessage(message);

        event.setCancelled(true);
    }

    @Override
    protected void sendMessage(Player sender, Component message) {
        // 私聊消息已在handleChat中发送，这里不需要额外处理
    }

    @Override
    protected boolean canReceiveMessage(Player player) {
        return true;
    }
}

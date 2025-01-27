package ict.minesunshineone.chat.channel.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.AbstractChatChannel;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class PrivateChannel extends AbstractChatChannel {

    private final Map<UUID, UUID> privateChats = new HashMap<>();
    private final Map<UUID, String> targetNames = new HashMap<>();

    public PrivateChannel(SimpleChat plugin) {
        super(plugin,
                "private",
                null,
                Component.text("私聊频道").color(TextColor.color(255, 170, 0)),
                Component.text("与其他玩家私聊的频道").color(TextColor.color(170, 170, 170)));
    }

    public void setPrivateChat(Player sender, Player target) {
        privateChats.put(sender.getUniqueId(), target.getUniqueId());
        targetNames.put(sender.getUniqueId(), target.getName());
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
        targetNames.remove(player.getUniqueId());
    }

    @Override
    public void handleChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Player target = getPrivateChatTarget(sender);

        if (target == null || !target.isOnline()) {
            sender.sendMessage(Component.text("你没有选择私聊对象，或者对方已离线！").color(TextColor.color(255, 85, 85)));
            return;
        }

        // 替换目标玩家名称占位符
        String targetName = targetNames.get(sender.getUniqueId());
        String message = PlaceholderAPI.setPlaceholders(sender, "%message%")
                .replace("%target_name%", targetName);

        Component formattedMessage = formatMessage(sender, event.message());
        sender.sendMessage(formattedMessage);
        target.sendMessage(formattedMessage);

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

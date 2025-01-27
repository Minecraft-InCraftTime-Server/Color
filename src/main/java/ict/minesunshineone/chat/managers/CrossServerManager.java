package ict.minesunshineone.chat.managers;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.ChatChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

public class CrossServerManager implements PluginMessageListener {

    private static final String BUNGEE_CHANNEL = "simplechat:chat";
    private static final String VELOCITY_CHANNEL = "simplechat:vchat";
    private final SimpleChat plugin;
    private boolean isVelocity = false;

    public CrossServerManager(SimpleChat plugin) {
        this.plugin = plugin;
        try {
            // 尝试注册Velocity通道
            try {
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, VELOCITY_CHANNEL);
                plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, VELOCITY_CHANNEL, this);
                isVelocity = true;
                plugin.getLogger().info("已启用Velocity跨服支持");
            } catch (Exception e) {
                // 如果Velocity注册失败，尝试BungeeCord
                plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
                plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, BUNGEE_CHANNEL, this);
                plugin.getLogger().info("已启用BungeeCord跨服支持");
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("注册跨服通信通道失败: %s", e.getMessage()));
        }
    }

    public void sendCrossServerMessage(String channelName, Player sender, Component message) {
        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(channelName);
            out.writeUTF(sender.getName());
            out.writeUTF(GsonComponentSerializer.gson().serialize(message));

            // 发送到所有在线玩家所在的服务器
            if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
                Player player = plugin.getServer().getOnlinePlayers().iterator().next();
                String channel = isVelocity ? VELOCITY_CHANNEL : BUNGEE_CHANNEL;
                player.sendPluginMessage(plugin, channel, out.toByteArray());
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("发送跨服消息失败: %s", e.getMessage()));
            sender.sendMessage(Component.text("发送跨服消息失败，请联系管理员！").color(TextColor.color(255, 85, 85)));
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(BUNGEE_CHANNEL) && !channel.equals(VELOCITY_CHANNEL)) {
            return;
        }

        try {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);
            String channelName = in.readUTF();
            String senderName = in.readUTF();
            String serializedMessage = in.readUTF();

            Component messageComponent = GsonComponentSerializer.gson().deserialize(serializedMessage);

            // 获取频道并广播消息
            ChatChannel chatChannel = plugin.getChannelManager().getChannel(channelName);
            if (chatChannel != null && chatChannel.isCrossServer()) {
                // 检查接收消息的玩家是否有权限
                for (Player target : plugin.getServer().getOnlinePlayers()) {
                    if (chatChannel.canJoin(target)) {
                        target.sendMessage(messageComponent);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("处理跨服消息失败: %s", e.getMessage()));
        }
    }

    public void disable() {
        try {
            if (isVelocity) {
                plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, VELOCITY_CHANNEL);
                plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, VELOCITY_CHANNEL);
            } else {
                plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, BUNGEE_CHANNEL);
                plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, BUNGEE_CHANNEL);
            }
        } catch (Exception e) {
            plugin.getLogger().warning(String.format("注销跨服通信通道失败: %s", e.getMessage()));
        }
    }
}

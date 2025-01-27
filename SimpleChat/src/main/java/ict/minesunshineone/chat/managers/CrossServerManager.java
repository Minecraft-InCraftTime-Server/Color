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

    private static final String CHANNEL = "simplechat:chat";
    private final SimpleChat plugin;
    private boolean isEnabled = false;

    public CrossServerManager(SimpleChat plugin) {
        this.plugin = plugin;
        setupCrossServer();
    }

    private void setupCrossServer() {
        try {
            // 注册插件消息通道
            plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
            isEnabled = true;
            plugin.getLogger().info("已启用跨服支持");
        } catch (Exception e) {
            plugin.getLogger().warning("注册跨服通信通道失败: " + e.getMessage());
            plugin.getLogger().warning("请检查服务器是否正确配置了BungeeCord/Velocity支持");
            isEnabled = false;
        }
    }

    public void sendCrossServerMessage(String channelName, Player sender, Component message) {
        if (!isEnabled) {
            sender.sendMessage(Component.text("跨服通信未启用，请联系管理员！").color(TextColor.color(255, 85, 85)));
            return;
        }

        try {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF(channelName);
            out.writeUTF(sender.getName());
            out.writeUTF(GsonComponentSerializer.gson().serialize(message));

            // 发送到代理服务器
            if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
                Player player = plugin.getServer().getOnlinePlayers().iterator().next();
                player.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
                plugin.getLogger().info("已发送跨服消息: " + sender.getName() + " -> " + channelName);
                plugin.getLogger().info("消息内容: " + GsonComponentSerializer.gson().serialize(message));
            } else {
                plugin.getLogger().warning("无法发送跨服消息：没有在线玩家");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("发送跨服消息失败: " + e.getMessage());
            e.printStackTrace();
            sender.sendMessage(Component.text("发送跨服消息失败，请联系管理员！").color(TextColor.color(255, 85, 85)));
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!isEnabled || !channel.equals(CHANNEL)) {
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
                plugin.getLogger().info("收到跨服消息: " + senderName + " -> " + channelName);
                plugin.getLogger().info("消息内容: " + GsonComponentSerializer.gson().serialize(messageComponent));

                // 检查接收消息的玩家是否有权限
                for (Player target : plugin.getServer().getOnlinePlayers()) {
                    if (chatChannel.canJoin(target)) {
                        target.sendMessage(messageComponent);
                        plugin.getLogger().info("向玩家 " + target.getName() + " 发送消息");
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("处理跨服消息失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disable() {
        if (!isEnabled) {
            return;
        }

        try {
            plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
            plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
            isEnabled = false;
        } catch (Exception e) {
            plugin.getLogger().warning("注销跨服通信通道失败: " + e.getMessage());
        }
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}

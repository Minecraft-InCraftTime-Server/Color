package ict.minesunshineone.chat.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.ChatChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CrossServerManager implements PluginMessageListener {

    private static final String CHANNEL = "simplechat:chat";
    private final SimpleChat plugin;

    public CrossServerManager(SimpleChat plugin) {
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this);
    }

    public void sendCrossServerMessage(String channelName, Player sender, Component message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(channelName);
        out.writeUTF(sender.getName());
        out.writeUTF(GsonComponentSerializer.gson().serialize(message));

        // 发送到所有在线玩家所在的服务器
        Player player = plugin.getServer().getOnlinePlayers().iterator().next();
        if (player != null) {
            player.sendPluginMessage(plugin, CHANNEL, out.toByteArray());
        }
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String channelName = in.readUTF();
        String senderName = in.readUTF();
        String serializedMessage = in.readUTF();

        Component messageComponent = GsonComponentSerializer.gson().deserialize(serializedMessage);

        // 获取频道并广播消息
        ChatChannel chatChannel = plugin.getChannelManager().getChannel(channelName);
        if (chatChannel != null && chatChannel.isCrossServer()) {
            plugin.getServer().broadcast(messageComponent);
        }
    }

    public void disable() {
        plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
        plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
    }
}

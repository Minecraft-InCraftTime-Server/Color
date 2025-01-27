package ict.minesunshineone.chat.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.channel.ChatChannel;
import ict.minesunshineone.chat.channel.impl.GlobalChannel;
import ict.minesunshineone.chat.channel.impl.NormalChannel;
import ict.minesunshineone.chat.channel.impl.PrivateChannel;
import ict.minesunshineone.chat.channel.impl.StaffChannel;

public class ChannelManager {

    private final SimpleChat plugin;
    private final Map<String, ChatChannel> channels = new HashMap<>();
    private final Map<UUID, String> playerChannels = new HashMap<>();

    public ChannelManager(SimpleChat plugin) {
        this.plugin = plugin;
        initializeChannels();
    }

    private void initializeChannels() {
        // 注册默认频道
        registerChannel(new NormalChannel(plugin));
        registerChannel(new StaffChannel(plugin));
        registerChannel(new PrivateChannel(plugin));
        registerChannel(new GlobalChannel(plugin));
    }

    public void registerChannel(ChatChannel channel) {
        channels.put(channel.getName().toLowerCase(), channel);
    }

    public ChatChannel getChannel(String name) {
        return channels.get(name.toLowerCase());
    }

    public ChatChannel getPlayerChannel(Player player) {
        String channelName = playerChannels.getOrDefault(player.getUniqueId(), "normal");
        return channels.get(channelName.toLowerCase());
    }

    public void setPlayerChannel(Player player, String channelName) {
        if (channels.containsKey(channelName.toLowerCase())) {
            playerChannels.put(player.getUniqueId(), channelName.toLowerCase());
        }
    }

    public void removePlayerChannel(Player player) {
        playerChannels.remove(player.getUniqueId());
    }

    public Map<String, ChatChannel> getChannels() {
        return new HashMap<>(channels);
    }
}

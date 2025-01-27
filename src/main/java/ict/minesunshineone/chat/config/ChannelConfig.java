package ict.minesunshineone.chat.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.utils.ChatFormatBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ChannelConfig {

    private final String name;
    private final Component displayName;
    private final Component description;
    private final String permission;
    private final boolean crossServer;
    private final ChatFormatBuilder formatBuilder;

    public ChannelConfig(String name, Component displayName, Component description, String permission, boolean crossServer, ChatFormatBuilder formatBuilder) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.permission = permission;
        this.crossServer = crossServer;
        this.formatBuilder = formatBuilder;
    }

    public static ChannelConfig load(SimpleChat plugin, String channelName) {
        File channelFile = new File(plugin.getDataFolder(), "channels/" + channelName + ".yml");
        if (!channelFile.exists()) {
            // 从资源文件复制默认配置
            try (InputStream in = plugin.getResource("channels/" + channelName + ".yml")) {
                if (in != null) {
                    channelFile.getParentFile().mkdirs();
                    Files.copy(in, channelFile.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().warning(String.format("无法创建频道配置文件: %s", channelName));
                return null;
            }
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(channelFile);

        String name = config.getString("name", channelName);
        Component displayName = LegacyComponentSerializer.legacyAmpersand().deserialize(
                config.getString("display_name", "&f" + channelName));
        Component description = LegacyComponentSerializer.legacyAmpersand().deserialize(
                config.getString("description", ""));
        String permission = config.getString("permission", "");
        boolean crossServer = config.getBoolean("cross_server", false);

        ConfigurationSection formatSection = config.getConfigurationSection("format");
        ChatFormatBuilder formatBuilder = new ChatFormatBuilder(formatSection);

        return new ChannelConfig(name, displayName, description, permission, crossServer, formatBuilder);
    }

    public String getName() {
        return name;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public Component getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isCrossServer() {
        return crossServer;
    }

    public Component formatMessage(Player player, Component message) {
        return formatBuilder.build(player, message);
    }
}

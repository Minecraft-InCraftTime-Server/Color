package ict.minesunshineone.chat.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import ict.minesunshineone.chat.SimpleChat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ChannelConfig {

    private final String name;
    private final Component displayName;
    private final Component description;
    private final String permission;
    private final String format;
    private final boolean crossServer;

    public ChannelConfig(String name, Component displayName, Component description, String permission, String format, boolean crossServer) {
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.permission = permission;
        this.format = format;
        this.crossServer = crossServer;
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
                plugin.getLogger().warning("无法创建频道配置文件: " + channelName);
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
        String format = config.getString("format", "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
        boolean crossServer = config.getBoolean("cross_server", false);

        return new ChannelConfig(name, displayName, description, permission, format, crossServer);
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

    public String getFormat() {
        return format;
    }

    public boolean isCrossServer() {
        return crossServer;
    }
}

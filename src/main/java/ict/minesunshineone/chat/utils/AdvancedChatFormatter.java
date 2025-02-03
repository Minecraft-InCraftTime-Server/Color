package ict.minesunshineone.chat.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class AdvancedChatFormatter {

    private final Plugin plugin;
    private final ConfigurationSection config;

    public AdvancedChatFormatter(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig().getConfigurationSection("chat.advanced-format");
    }

    public Component format(Player player, Component message) {
        Component result = Component.empty();

        // 添加世界前缀（如果配置存在）
        if (config != null) {
            ConfigurationSection worldSection = config.getConfigurationSection("prefix.world");
            if (worldSection != null) {
                String text = parsePlaceholders(worldSection.getString("text", ""), player);
                String hover = parsePlaceholders(worldSection.getString("hover", ""), player);
                String command = parsePlaceholders(worldSection.getString("command", ""), player);

                Component worldPrefix = LegacyComponentSerializer.legacyAmpersand().deserialize(text);
                if (!hover.isEmpty()) {
                    worldPrefix = worldPrefix.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(hover)
                    ));
                }
                if (!command.isEmpty()) {
                    worldPrefix = worldPrefix.clickEvent(ClickEvent.suggestCommand(command));
                }
                result = result.append(worldPrefix);
            }
        }

        // 添加玩家名字部分（使用基础格式，但不包含消息部分）
        String format = plugin.getConfig().getString("chat.format", "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
        String[] parts = format.split("%message%");
        String prefix = parts[0];

        // 处理玩家名字部分
        prefix = parsePlaceholders(prefix, player);
        Component nameComponent = (LegacyComponentSerializer.legacyAmpersand().deserialize(prefix));

        // 添加玩家名字的悬浮信息（如果配置存在）
        if (config != null) {
            ConfigurationSection playerSection = config.getConfigurationSection("player");
            if (playerSection != null) {
                String hover = parsePlaceholders(playerSection.getString("hover", ""), player);
                String command = parsePlaceholders(playerSection.getString("command", ""), player);

                if (!hover.isEmpty()) {
                    nameComponent = nameComponent.hoverEvent(HoverEvent.showText(
                            LegacyComponentSerializer.legacyAmpersand().deserialize(hover)
                    ));
                }
                if (!command.isEmpty()) {
                    nameComponent = nameComponent.clickEvent(ClickEvent.suggestCommand(command));
                }
            }
        }

        result = result.append(nameComponent);

        // 直接添加原始消息组件，保持其所有属性
        result = result.append(message);

        return result;
    }

    private String parsePlaceholders(String text, Player player) {
        if (text == null) {
            return "";
        }
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return PlaceholderAPI.setPlaceholders(player, text);
        }
        return text;
    }
}

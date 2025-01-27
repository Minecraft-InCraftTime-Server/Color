package ict.minesunshineone.chat.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import ict.minesunshineone.chat.config.FormatConfig;
import net.kyori.adventure.text.Component;

public class ChatFormatBuilder {

    private final ConfigurationSection formatSection;

    public ChatFormatBuilder(ConfigurationSection formatSection) {
        this.formatSection = formatSection;
    }

    public Component build(Player player, Component message) {
        Component result = Component.empty();

        // 处理前缀部分
        ConfigurationSection prefixSection = formatSection.getConfigurationSection("prefix");
        if (prefixSection != null) {
            for (String key : prefixSection.getKeys(false)) {
                ConfigurationSection section = prefixSection.getConfigurationSection(key);
                if (section != null) {
                    FormatConfig format = new FormatConfig(section);
                    result = result.append(format.build(player));
                }
            }
        }

        // 处理分隔符
        ConfigurationSection separatorSection = formatSection.getConfigurationSection("separator");
        if (separatorSection != null) {
            FormatConfig format = new FormatConfig(separatorSection);
            result = result.append(format.build(player));
        }

        // 处理消息部分
        ConfigurationSection messageSection = formatSection.getConfigurationSection("message");
        if (messageSection != null) {
            String messageFormat = messageSection.getString("text", "%message%");
            result = result.append(message);
        } else {
            result = result.append(message);
        }

        return result;
    }
}

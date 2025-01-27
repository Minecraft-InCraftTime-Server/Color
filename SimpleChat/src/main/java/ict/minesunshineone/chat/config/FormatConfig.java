package ict.minesunshineone.chat.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class FormatConfig {

    private final String text;
    private final String[] hover;
    private final String command;
    private final String suggest;
    private final String url;
    private final String copy;

    public FormatConfig(ConfigurationSection section) {
        if (section == null) {
            this.text = "";
            this.hover = new String[0];
            this.command = null;
            this.suggest = null;
            this.url = null;
            this.copy = null;
            return;
        }

        this.text = section.getString("text", "");
        this.hover = section.getStringList("hover").toArray(new String[0]);
        this.command = section.getString("command");
        this.suggest = section.getString("suggest");
        this.url = section.getString("url");
        this.copy = section.getString("copy");
    }

    public Component build(Player player) {
        // 处理文本
        String processedText = PlaceholderAPI.setPlaceholders(player, text);
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(processedText);

        // 处理悬浮文本
        if (hover != null && hover.length > 0) {
            StringBuilder hoverText = new StringBuilder();
            for (String line : hover) {
                if (!hoverText.isEmpty()) {
                    hoverText.append("\n");
                }
                hoverText.append(PlaceholderAPI.setPlaceholders(player, line));
            }
            component = component.hoverEvent(HoverEvent.showText(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(hoverText.toString())));
        }

        // 处理点击事件
        if (command != null) {
            String processedCommand = PlaceholderAPI.setPlaceholders(player, command);
            component = component.clickEvent(ClickEvent.runCommand(processedCommand));
        } else if (suggest != null) {
            String processedSuggest = PlaceholderAPI.setPlaceholders(player, suggest);
            component = component.clickEvent(ClickEvent.suggestCommand(processedSuggest));
        } else if (url != null) {
            String processedUrl = PlaceholderAPI.setPlaceholders(player, url);
            component = component.clickEvent(ClickEvent.openUrl(processedUrl));
        } else if (copy != null) {
            String processedCopy = PlaceholderAPI.setPlaceholders(player, copy);
            component = component.clickEvent(ClickEvent.copyToClipboard(processedCopy));
        }

        return component;
    }
}

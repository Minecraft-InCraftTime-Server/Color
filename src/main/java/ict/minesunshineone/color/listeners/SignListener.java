package ict.minesunshineone.color.listeners;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SignListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .hexCharacter('#')
            .build();

    private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.plainText();

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("colorcode.sign")) {
            return;
        }

        Sign sign = (Sign) event.getBlock().getState();

        for (int i = 0; i < 4; i++) {
            String newLine = event.getLine(i);
            Component oldLine = sign.line(i);
            String oldPlainText = PLAIN_SERIALIZER.serialize(oldLine);

            // 如果新行为空，直接设置空组件
            if (newLine == null || newLine.isEmpty()) {
                event.line(i, Component.empty());
                continue;
            }

            // 移除颜色代码后比较文本内容
            String strippedNewLine = newLine.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "");

            // 如果纯文本内容相同但原始文本不同（说明只改变了颜色代码），应用新的格式
            if (strippedNewLine.equals(oldPlainText) && !newLine.equals(oldPlainText)) {
                Component component = SERIALIZER.deserialize(newLine)
                        .decoration(TextDecoration.ITALIC, false);
                event.line(i, component);
                continue;
            }

            // 如果内容完全相同（包括颜色代码），保留原有格式
            if (newLine.equals(oldPlainText)) {
                event.line(i, oldLine);
                continue;
            }

            // 内容发生改变，应用新的格式
            Component component = SERIALIZER.deserialize(newLine)
                    .decoration(TextDecoration.ITALIC, false);
            event.line(i, component);
        }
    }
}

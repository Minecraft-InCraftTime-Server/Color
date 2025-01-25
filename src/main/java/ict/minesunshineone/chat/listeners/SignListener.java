package ict.minesunshineone.chat.listeners;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import ict.minesunshineone.chat.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;

public class SignListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("simplechat.sign.color")) {
            return;
        }

        Sign sign = (Sign) event.getBlock().getState();

        for (int i = 0; i < 4; i++) {
            Component newLine = event.line(i);
            Component oldLine = sign.line(i);
            String newLineStr = ComponentUtils.plainSerializer().serialize(newLine);
            String oldPlainText = ComponentUtils.plainSerializer().serialize(oldLine);

            // 如果新行为空，直接设置空组件
            if (newLine == null || newLine.equals(Component.empty())) {
                event.line(i, Component.empty());
                continue;
            }

            // 移除颜色代码后比较文本内容
            String strippedNewLine = newLineStr.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "");

            // 如果纯文本内容相同但原始文本不同（说明只改变了颜色代码），应用新的格式
            if (strippedNewLine.equals(oldPlainText) && !newLineStr.equals(oldPlainText)) {
                Component component = ComponentUtils.legacySerializer().deserialize(newLineStr);
                if (!newLineStr.contains("&o")) {
                    component = component.decoration(TextDecoration.ITALIC, false);
                }
                event.line(i, component);
                continue;
            }

            // 如果内容完全相同（包括颜色代码），保留原有格式
            if (newLineStr.equals(oldPlainText)) {
                event.line(i, oldLine);
                continue;
            }

            // 内容发生改变，应用新的格式
            Component component = ComponentUtils.legacySerializer().deserialize(newLineStr);
            if (!newLineStr.contains("&o")) {
                component = component.decoration(TextDecoration.ITALIC, false);
            }
            event.line(i, component);
        }
    }
}

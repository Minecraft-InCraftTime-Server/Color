package com.github.color.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SignListener implements Listener {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("colorcode.sign")) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            String line = event.getLine(i);
            if (line == null || line.isEmpty()) {
                continue;
            }

            Component component = SERIALIZER.deserialize(line)
                    .decoration(TextDecoration.ITALIC, false);
            event.line(i, component);
        }
    }
}

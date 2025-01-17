package com.github.color.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("colorcode.chat")) {
            return;
        }

        Component originalMessage = event.message();
        String plainMessage = originalMessage.toString();

        if (plainMessage.contains("[i]") || plainMessage.contains("[item]")) {
            Component displayItem = getComponent(player);

            Component chatMessage = originalMessage
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[i]").replacement(displayItem).build())
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[item]").replacement(displayItem).build());

            event.message(chatMessage);
        }
    }

    private static @NotNull
    Component getComponent(Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem.getType() != Material.AIR) {
            return handItem.displayName()
                    .decorate(TextDecoration.BOLD)
                    .hoverEvent(handItem.asHoverEvent());
        } else {
            return Component.text(player.getName() + "的手")
                    .decorate(TextDecoration.BOLD)
                    .decorate(TextDecoration.UNDERLINED)
                    .hoverEvent(Component.text("手上什么也没有"));
        }
    }
}

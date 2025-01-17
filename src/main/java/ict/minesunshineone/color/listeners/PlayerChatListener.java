package ict.minesunshineone.color.listeners;

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
            Component itemName = handItem.hasItemMeta() && handItem.getItemMeta().hasDisplayName()
                    ? handItem.getItemMeta().displayName()
                    : Component.translatable(handItem.getType().translationKey());

            String quantityText = handItem.getAmount() > 1 ? " x" + handItem.getAmount() : "";

            return Component.text("『")
                    .append(itemName
                            .decorate(TextDecoration.BOLD)
                            .hoverEvent(handItem.asHoverEvent()))
                    .append(Component.text(quantityText))
                    .append(Component.text("』"));
        } else {
            return Component.text("『")
                    .append(Component.text(player.getName() + "的手")
                            .decorate(TextDecoration.BOLD)
                            .decorate(TextDecoration.UNDERLINED)
                            .hoverEvent(Component.text("手上什么也没有")))
                    .append(Component.text("』"));
        }
    }
}

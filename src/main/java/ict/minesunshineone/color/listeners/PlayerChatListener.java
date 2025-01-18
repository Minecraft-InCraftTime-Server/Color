package ict.minesunshineone.color.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ict.minesunshineone.color.ColorCode;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PlayerChatListener implements org.bukkit.event.Listener {

    private ColorCode plugin;

    public PlayerChatListener(ColorCode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component originalMessage = event.message();

        // 检查是否包含物品展示标记
        String plainMessage = LegacyComponentSerializer.legacyAmpersand().serialize(originalMessage);
        if (plainMessage.contains("[i]") || plainMessage.contains("[item]")) {
            Component displayItem = getComponent(player);
            originalMessage = originalMessage
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[i]").replacement(displayItem).build())
                    .replaceText(TextReplacementConfig.builder().matchLiteral("[item]").replacement(displayItem).build());
        }

        // 构建最终消息
        String format = plugin.getConfig().getString("chat.format", "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
        format = PlaceholderAPI.setPlaceholders(player, format);
        format = format.replace("%player_name%", player.getName());

        // 将格式转换为组件，但保留 %message% 占位符
        Component formatComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(format);

        // 使用 replaceText 替换 %message% 为原始消息组件
        Component finalMessage = formatComponent.replaceText(TextReplacementConfig.builder()
                .matchLiteral("%message%")
                .replacement(originalMessage)
                .build());

        // 取消原始消息并广播新消息
        event.setCancelled(true);
        Bukkit.broadcast(finalMessage);
    }

    private static @NotNull
    Component getComponent(Player player) {
        ItemStack handItem = player.getInventory().getItemInMainHand();

        if (handItem.getType() != Material.AIR) {
            // 获取物品的显示名称（优先使用自定义名称，否则使用本地化名称）
            Component itemName;
            if (handItem.hasItemMeta() && handItem.getItemMeta().hasDisplayName()) {
                itemName = handItem.getItemMeta().displayName();
            } else {
                itemName = Component.translatable(handItem.getType().translationKey());
            }

            // 如果数量大于1，添加灰色数量显示
            if (handItem.getAmount() > 1) {
                itemName = itemName.append(Component.text(" X" + handItem.getAmount())
                        .color(TextColor.color(255, 255, 255)));
            }

            // 创建物品展示组件
            return Component.text("「")
                    .append(itemName)
                    .append(Component.text("」"))
                    .decorate(TextDecoration.BOLD)
                    .hoverEvent(HoverEvent.showItem(handItem.asHoverEvent().value()));
        } else {
            return Component.text("「" + player.getName() + "的手」")
                    .decorate(TextDecoration.BOLD)
                    .hoverEvent(HoverEvent.showText(Component.text("手上什么也没有").color(TextColor.color(255, 85, 85))));
        }
    }

}

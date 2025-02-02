package ict.minesunshineone.chat.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ict.minesunshineone.chat.SimpleChat;
import ict.minesunshineone.chat.managers.MuteManager;
import ict.minesunshineone.chat.utils.AdvancedChatFormatter;
import ict.minesunshineone.chat.utils.ColorUtils;
import ict.minesunshineone.chat.utils.ComponentUtils;
import ict.minesunshineone.chat.utils.TimeUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

public class PlayerChatListener implements org.bukkit.event.Listener {

    private final SimpleChat plugin;
    private final MuteManager muteManager;
    private final AdvancedChatFormatter chatFormatter;

    public PlayerChatListener(SimpleChat plugin, MuteManager muteManager) {
        this.plugin = plugin;
        this.muteManager = muteManager;
        this.chatFormatter = new AdvancedChatFormatter(plugin);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncChatEvent event) {
        Player player = event.getPlayer();

        // 检查玩家是否被禁言
        if (muteManager.isMuted(player)) {
            MuteManager.MuteInfo muteInfo = muteManager.getMuteInfo(player);

            Component message = Component.text("你已被禁言")
                    .color(TextColor.color(255, 170, 0))
                    .append(Component.newline())
                    .append(Component.text("原因: ").color(TextColor.color(255, 170, 0)))
                    .append(Component.text(muteInfo.getReason()).color(TextColor.color(255, 85, 85)))
                    .append(Component.newline())
                    .append(Component.text("剩余时间: ").color(TextColor.color(255, 170, 0)))
                    .append(Component.text(TimeUtils.formatDuration(muteInfo.getRemainingTime()))
                            .color(TextColor.color(255, 85, 85)));

            player.sendMessage(message);
            event.setCancelled(true);
            return;
        }

        // 获取原始消息
        Component originalMessage = event.message();
        String plainMessage = ComponentUtils.legacySerializer().serialize(originalMessage);

        // 处理颜色代码
        Component processedMessage = originalMessage;
        if (player.hasPermission("simplechat.chat.color")) {
            processedMessage = ColorUtils.formatText(plainMessage);
        }

        // 处理物品展示标记
        if (plainMessage.contains("[i]") || plainMessage.contains("[item]")) {
            Component displayItem = getHandItemComponent(player);
            processedMessage = processedMessage.replaceText(TextReplacementConfig.builder()
                    .match("\\[(i|item)\\]")
                    .replacement(displayItem)
                    .build());
        }

        // 使用高级格式化处理最终消息
        Component finalMessage = chatFormatter.format(player, processedMessage);

        // 取消原始消息并广播新消息
        event.setCancelled(true);
        Bukkit.broadcast(finalMessage);
    }

    private static @NotNull
    Component getHandItemComponent(Player player) {
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
                    .hoverEvent(HoverEvent.showItem(handItem.asHoverEvent().value()));
        } else {
            return Component.text("「" + player.getName() + "的手」")
                    .hoverEvent(HoverEvent.showText(Component.text("手上什么也没有").color(TextColor.color(255, 85, 85))));
        }
    }
}

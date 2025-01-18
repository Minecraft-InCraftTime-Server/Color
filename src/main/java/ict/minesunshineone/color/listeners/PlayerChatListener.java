package ict.minesunshineone.color.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import ict.minesunshineone.color.ColorCode;
import ict.minesunshineone.color.managers.MuteManager;
import ict.minesunshineone.color.utils.ColorUtils;
import ict.minesunshineone.color.utils.ComponentUtils;
import ict.minesunshineone.color.utils.TimeUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;

public class PlayerChatListener implements org.bukkit.event.Listener {

    private ColorCode plugin;
    private final MuteManager muteManager;

    public PlayerChatListener(ColorCode plugin, MuteManager muteManager) {
        this.plugin = plugin;
        this.muteManager = muteManager;
    }

    @EventHandler
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

        Component originalMessage = event.message();

        // 只序列化一次
        String plainMessage = ComponentUtils.legacySerializer().serialize(originalMessage);

        // 处理颜色代码
        if (player.hasPermission("colorcode.chat.color")) {
            originalMessage = ColorUtils.formatText(plainMessage);
        }

        // 处理物品展示标记
        if (plainMessage.contains("[i]") || plainMessage.contains("[item]")) {
            Component displayItem = getHandItemComponent(player);
            // 合并替换操作
            originalMessage = originalMessage.replaceText(TextReplacementConfig.builder()
                    .match("\\[(i|item)\\]")
                    .replacement(displayItem)
                    .build());
        }

        // 构建最终消息
        String format = plugin.getConfig().getString("chat.format",
                "%luckperms_prefix%%player_name%%luckperms_suffix% &a&l>>&r %message%");
        format = PlaceholderAPI.setPlaceholders(player, format);

        // 将格式转换为组件，但保留 %message% 占位符
        Component formatComponent = ComponentUtils.legacySerializer().deserialize(format);

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

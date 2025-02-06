package ict.minesunshineone.chat.listeners;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import ict.minesunshineone.chat.utils.ComponentUtils;
import ict.minesunshineone.chat.utils.SchedulerUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class PingListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = 5000; // 5秒冷却时间
    private static final long CLEANUP_DELAY = 12000L; // 10分钟清理一次
    private static final Pattern PLAYER_NAME_PATTERN = Pattern.compile("\\b%s\\b", Pattern.CASE_INSENSITIVE);
    private static final Component PLAYER_MENTION_PREFIX = Component.text("玩家 ");
    private static final Component PLAYER_MENTION_SUFFIX = Component.text(" 在聊天中提到了你！");

    public PingListener(Plugin plugin) {
        this.plugin = plugin;
        // 启动定期清理任务
        startCleanupTask();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cooldowns.remove(event.getPlayer().getUniqueId());
    }

    private void startCleanupTask() {
        SchedulerUtils.runAtFixedRate(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            cooldowns.entrySet().removeIf(entry
                    -> currentTime - entry.getValue() > COOLDOWN_TIME * 10); // 移除超过30秒的记录
        }, 20L, CLEANUP_DELAY);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Component originalMessage = event.message();
        String plainMessage = ComponentUtils.legacySerializer().serialize(originalMessage);

        // 检查冷却时间
        long currentTime = System.currentTimeMillis();
        Long lastPingTime = cooldowns.get(sender.getUniqueId());
        if (lastPingTime != null && currentTime - lastPingTime < COOLDOWN_TIME) {
            return; // 在冷却时间内，直接返回
        }

        boolean pinged = false;
        Component finalMessage = originalMessage;

        // 处理 @all 功能
        if (sender.hasPermission("simplechat.chat.mentionall") && plainMessage.contains("@all")) {
            pinged = true;
            Component allMentionComponent = Component.text("@all")
                    .color(TextColor.color(0, 255, 0)); // 绿色

            finalMessage = finalMessage.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("@all")
                    .replacement(allMentionComponent)
                    .build());

            // 给所有在线玩家发送提醒（除了发送者）
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 2.0f, 1.0f);
                Component actionBar = Component.text("管理员 ")
                        .append(Component.text(sender.getName()).color(NamedTextColor.YELLOW))
                        .append(Component.text(" 发送了全体消息！"));
                target.sendActionBar(actionBar);

            }
        }

        // 处理普通的 @ 功能
        for (Player target : plugin.getServer().getOnlinePlayers()) {
            String playerName = target.getName();
            String patternStr = String.format(PLAYER_NAME_PATTERN.pattern(), Pattern.quote(playerName));
            if (Pattern.compile(patternStr).matcher(plainMessage).find()) {
                pinged = true;
                Component pingComponent = Component.text("@" + playerName)
                        .color(TextColor.color(0, 255, 0));

                finalMessage = finalMessage.replaceText(TextReplacementConfig.builder()
                        .match("\\b" + Pattern.quote(playerName) + "\\b")
                        .replacement(pingComponent)
                        .build());

                if (!sender.equals(target)) {
                    target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 2.0f, 1.0f);
                    Component actionBar = PLAYER_MENTION_PREFIX
                            .append(Component.text(sender.getName()).color(NamedTextColor.YELLOW))
                            .append(PLAYER_MENTION_SUFFIX);
                    target.sendActionBar(actionBar);
                }
            }
        }

        if (pinged) {
            cooldowns.put(sender.getUniqueId(), currentTime);
        }
        event.message(finalMessage);
    }
}

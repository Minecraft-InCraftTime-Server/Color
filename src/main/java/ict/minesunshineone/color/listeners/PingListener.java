package ict.minesunshineone.color.listeners;

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

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class PingListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = 5000; // 5秒冷却时间
    private static final long CLEANUP_DELAY = 12000L; // 10分钟清理一次
    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors()
            .build();
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
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, (task) -> {
            long currentTime = System.currentTimeMillis();
            cooldowns.entrySet().removeIf(entry
                    -> currentTime - entry.getValue() > COOLDOWN_TIME * 10); // 移除超过30秒的记录
        }, 20L, CLEANUP_DELAY);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        Component originalMessage = event.message();
        String plainMessage = SERIALIZER.serialize(originalMessage);

        // 检查冷却时间
        long currentTime = System.currentTimeMillis();
        Long lastPingTime = cooldowns.get(sender.getUniqueId());
        if (lastPingTime != null && currentTime - lastPingTime < COOLDOWN_TIME) {
            return; // 在冷却时间内，直接返回
        }

        boolean pinged = false;
        Component finalMessage = originalMessage;

        for (Player target : plugin.getServer().getOnlinePlayers()) {
            String playerName = target.getName();
            String patternStr = String.format(PLAYER_NAME_PATTERN.pattern(), Pattern.quote(playerName));
            if (Pattern.compile(patternStr).matcher(plainMessage).find()) {
                pinged = true;
                Component pingComponent = Component.text("@" + playerName)
                        .color(TextColor.color(0, 255, 0))
                        .decorate(TextDecoration.BOLD);

                finalMessage = finalMessage.replaceText(TextReplacementConfig.builder()
                        .match("\\b" + Pattern.quote(playerName) + "\\b")
                        .replacement(pingComponent)
                        .build());

                if (!sender.equals(target)) {
                    target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 2.5f, 1.0f);
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

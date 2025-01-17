package ict.minesunshineone.color.listeners;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class PingListener implements Listener {

    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns = new ConcurrentHashMap<>();
    private static final long COOLDOWN_TIME = 3000; // 3秒冷却时间
    private static final long CLEANUP_DELAY = 72000L; // 1小时清理一次

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

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.message().toString();
        AtomicReference<Component> originalMessage = new AtomicReference<>(event.message());

        plugin.getServer().getGlobalRegionScheduler().execute(plugin, () -> {
            for (Player target : plugin.getServer().getOnlinePlayers()) {
                String playerName = target.getName();

                if (message.contains(playerName)) {
                    Component pingComponent = Component.text("@" + playerName)
                            .color(TextColor.color(255, 170, 0))
                            .decorate(TextDecoration.BOLD);

                    originalMessage.set(originalMessage.get().replaceText(TextReplacementConfig.builder()
                            .matchLiteral(playerName)
                            .replacement(pingComponent)
                            .build()));

                    if (!sender.equals(target) && target.getLocation() != null) {
                        plugin.getServer().getRegionScheduler().execute(plugin, target.getLocation(), () -> {
                            Component actionBar = Component.text("玩家 ")
                                    .append(Component.text(sender.getName()).color(NamedTextColor.YELLOW))
                                    .append(Component.text(" 在聊天中提到了你！"));
                            target.sendActionBar(actionBar);
                            target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_LAND, 2.5f, 1.0f);
                        });
                    }
                }
            }
            event.message(originalMessage.get());
        });
    }
}

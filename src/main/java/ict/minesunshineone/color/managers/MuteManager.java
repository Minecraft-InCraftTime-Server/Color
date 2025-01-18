package ict.minesunshineone.color.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class MuteManager {

    private final Plugin plugin;
    private final Map<UUID, MuteInfo> mutedPlayers = new HashMap<>();

    public MuteManager(Plugin plugin) {
        this.plugin = plugin;
        startCleanupTask();
    }

    public static class MuteInfo {

        private final long endTime;
        private final String reason;

        public MuteInfo(long duration, String reason) {
            this.endTime = System.currentTimeMillis() + duration;
            this.reason = reason;
        }

        public long getEndTime() {
            return endTime;
        }

        public String getReason() {
            return reason;
        }

        public long getRemainingTime() {
            return Math.max(0, endTime - System.currentTimeMillis());
        }
    }

    public void mutePlayer(Player player, long duration, String reason) {
        mutedPlayers.put(player.getUniqueId(), new MuteInfo(duration, reason));
    }

    public void unmutePlayer(Player player) {
        mutedPlayers.remove(player.getUniqueId());
    }

    public boolean isMuted(Player player) {
        MuteInfo info = mutedPlayers.get(player.getUniqueId());
        if (info == null) {
            return false;
        }

        if (info.getEndTime() <= System.currentTimeMillis()) {
            mutedPlayers.remove(player.getUniqueId());
            return false;
        }
        return true;
    }

    public MuteInfo getMuteInfo(Player player) {
        return mutedPlayers.get(player.getUniqueId());
    }

    private void startCleanupTask() {
        plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> {
            long currentTime = System.currentTimeMillis();
            mutedPlayers.entrySet().removeIf(entry -> entry.getValue().getEndTime() <= currentTime);
        }, 20L * 60, 20L * 60); // 每分钟清理一次
    }
}

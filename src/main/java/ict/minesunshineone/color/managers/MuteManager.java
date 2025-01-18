package ict.minesunshineone.color.managers;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MuteManager {

    private final Plugin plugin;
    private final Map<UUID, MuteInfo> mutedPlayers = new HashMap<>();
    private final File dataFile;

    public MuteManager(Plugin plugin) {
        this.plugin = plugin;
        this.dataFile = new File(plugin.getDataFolder(), "mute_data.json");
        loadMuteData();
        startCleanupTask();
    }

    private void loadMuteData() {
        if (!dataFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                UUID uuid = UUID.fromString(entry.getKey());
                JsonObject muteData = entry.getValue().getAsJsonObject();
                long endTime = muteData.get("endTime").getAsLong();
                String reason = muteData.get("reason").getAsString();

                // 只加载未过期的禁言
                if (endTime > System.currentTimeMillis()) {
                    mutedPlayers.put(uuid, new MuteInfo(endTime - System.currentTimeMillis(), reason));
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("加载禁言数据失败: %s", e.getMessage()));
        }
    }

    public void saveMuteData() {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<UUID, MuteInfo> entry : mutedPlayers.entrySet()) {
            JsonObject muteData = new JsonObject();
            muteData.addProperty("endTime", entry.getValue().getEndTime());
            muteData.addProperty("reason", entry.getValue().getReason());
            jsonObject.add(entry.getKey().toString(), muteData);
        }

        try (FileWriter writer = new FileWriter(dataFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, writer);
        } catch (IOException e) {
            plugin.getLogger().warning(String.format("保存禁言数据失败: %s", e.getMessage()));
        }
    }

    public static class MuteInfo {

        private final long endTime;
        private final String reason;
        private transient final AtomicBoolean isValid = new AtomicBoolean(true);

        public MuteInfo(long duration, String reason) {
            this.endTime = System.currentTimeMillis() + duration;
            this.reason = reason;
        }

        public void invalidate() {
            isValid.set(false);
        }

        public boolean isValid() {
            return isValid.get() && getEndTime() > System.currentTimeMillis();
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
        saveMuteData();
    }

    public void unmutePlayer(Player player) {
        mutedPlayers.remove(player.getUniqueId());
        saveMuteData();
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

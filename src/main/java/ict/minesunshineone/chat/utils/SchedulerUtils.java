package ict.minesunshineone.chat.utils;

import org.bukkit.plugin.Plugin;

public class SchedulerUtils {

    private static final boolean IS_FOLIA = checkIsFolia();

    private static boolean checkIsFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static void runAtFixedRate(Plugin plugin, Runnable task, long initialDelay, long period) {
        if (IS_FOLIA) {
            plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> task.run(), initialDelay, period);
        } else {
            plugin.getServer().getScheduler().runTaskTimer(plugin, task, initialDelay, period);
        }
    }

    public static void runTaskAsynchronously(Plugin plugin, Runnable task) {
        if (IS_FOLIA) {
            plugin.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
        }
    }
}

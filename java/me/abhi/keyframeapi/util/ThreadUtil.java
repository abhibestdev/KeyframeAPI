package me.abhi.keyframeapi.util;

import me.abhi.keyframeapi.KeyframeAPI;
import org.bukkit.Bukkit;

public class ThreadUtil {

    public static void runSync(Runnable runnable) {
        Bukkit.getScheduler().runTask(KeyframeAPI.getInstance(), runnable);
    }

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(KeyframeAPI.getInstance(), runnable);
    }
}

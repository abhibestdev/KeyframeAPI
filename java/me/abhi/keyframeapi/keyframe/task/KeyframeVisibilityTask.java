package me.abhi.keyframeapi.keyframe.task;

import lombok.RequiredArgsConstructor;
import me.abhi.keyframeapi.keyframe.KeyframeHandler;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class KeyframeVisibilityTask extends BukkitRunnable {

    private final KeyframeHandler keyframeHandler;

    public void run() {
        Bukkit.getOnlinePlayers().stream().forEach(a -> {

            if (keyframeHandler.isPlayingAnimation(a)) {
                Bukkit.getOnlinePlayers().stream().filter(b -> keyframeHandler.isPlayingAnimation(b) && a != b).forEach(b -> {
                    if (a.canSee(b)) a.hidePlayer(b);
                    if (b.canSee(a)) b.hidePlayer(a);
                });
            } else {
                Bukkit.getOnlinePlayers().stream().filter(b -> !a.canSee(b) && a != b && !keyframeHandler.isPlayingAnimation(b)).forEach(b -> {
                    a.showPlayer(b);
                    b.showPlayer(a);
                });
            }
        });
    }
}

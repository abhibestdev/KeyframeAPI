package me.abhi.keyframeapi.keyframe;

import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.keyframe.event.KeyframeCancelEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeEndEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeStartEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeTickEvent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class PlayerAnimator {

    private final Player player;
    private final KeyframeAnimation animation;
    private final double duration;
    private GameMode originalGamemode;
    private BukkitRunnable runnable;

    public PlayerAnimator(Player player, KeyframeAnimation animation) {
        this.player = player;
        this.animation = animation;
        this.duration = animation.getKeyframes().get(animation.getKeyframes().size() - 1).getTime();
        this.originalGamemode = player.getGameMode();
    }

    public void play() {
        List<Keyframe> frames = animation.getKeyframes();
        if (frames.isEmpty()) return;

        Keyframe firstFrame = frames.get(0);
        player.teleport(firstFrame.getLocation());
        player.setGameMode(GameMode.SPECTATOR);

        new KeyframeStartEvent(player, animation, this).call();


        runnable = new BukkitRunnable(){

            double elapsed = 0;
            final double tickRate = 1.0 / 20.0; // 1 tick = 1/20th second

            @Override
            public void run() {
                if (elapsed >= duration) {
                    cancel();
                    onEnd();
                    new KeyframeEndEvent(player, animation, PlayerAnimator.this).call();
                    return;
                }

                Location interpolated = interpolate(elapsed);
                if (interpolated != null) {
                    player.teleport(interpolated);
                }

                elapsed += tickRate;
                new KeyframeTickEvent(player, animation, PlayerAnimator.this).call();
            }
        };
        runnable.runTaskTimer(KeyframeAPI.getInstance(), 0L, 1L);
    }

    public void stop() {
        runnable.cancel();
        onEnd();
        new KeyframeCancelEvent(player, animation, this).call();
    }

    private void onEnd() {
        if (originalGamemode != GameMode.SPECTATOR) {
            player.setGameMode(originalGamemode);
        }
    }

    private Location interpolate(double time) {
        List<Keyframe> frames = animation.getKeyframes();
        Keyframe previous = null, next = null;

        for (Keyframe frame : frames) {
            if (frame.getTime() > time) {
                next = frame;
                break;
            }
            previous = frame;
        }

        if (previous == null || next == null) {
            return null;
        }

        double interval = next.getTime() - previous.getTime();
        double progress = (time - previous.getTime()) / interval;

        Location from = previous.getLocation();
        Location to = next.getLocation();

        return interpolateLocation(from, to, progress);
    }

    private Location interpolateLocation(Location from, Location to, double t) {
        double x = from.getX() + (to.getX() - from.getX()) * t;
        double y = from.getY() + (to.getY() - from.getY()) * t;
        double z = from.getZ() + (to.getZ() - from.getZ()) * t;

        float yaw = interpolateYaw(from.getYaw(), to.getYaw(), t);
        float pitch = from.getPitch() + (to.getPitch() - from.getPitch()) * (float) t;

        return new Location(from.getWorld(), x, y, z, yaw, pitch);
    }

    private float interpolateYaw(float fromYaw, float toYaw, double t) {
        float delta = wrapDegrees(toYaw - fromYaw);
        return fromYaw + delta * (float) t;
    }

    private float wrapDegrees(float degrees) {
        degrees = degrees % 360.0f;
        if (degrees >= 180.0f) {
            degrees -= 360.0f;
        }
        if (degrees < -180.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }

}

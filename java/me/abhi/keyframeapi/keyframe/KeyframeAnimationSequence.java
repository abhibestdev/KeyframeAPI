package me.abhi.keyframeapi.keyframe;

import lombok.Getter;
import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.keyframe.event.KeyframeEndEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeSequenceEndEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeSequenceStartEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeSequenceSwitchEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class KeyframeAnimationSequence implements Listener {

    private final String name;
    private final Player player;
    private final Queue<KeyframeAnimation> animations = new LinkedList<>();
    private GameMode originalGamemode;
    private boolean active = false;
    private int played = 0;

    public KeyframeAnimationSequence(String name, Player player, KeyframeAnimation... keyframeAnimations) {
        this.name = name;
        this.player = player;
        animations.addAll(List.of(keyframeAnimations));
        this.originalGamemode = player.getGameMode();
        Bukkit.getPluginManager().registerEvents(this, KeyframeAPI.getInstance());
    }

    public KeyframeAnimationSequence add(KeyframeAnimation animation) {
        animations.add(animation);
        return this;
    }

    public void play() {
        if (animations.isEmpty() || active) return;
        active = true;

        player.setGameMode(GameMode.SPECTATOR);
        preloadAllChunks(() -> Bukkit.getScheduler().runTask(KeyframeAPI.getInstance(), this::playNext));
    }

    private void preloadAllChunks(Runnable onComplete) {
        Set<String> seenChunks = new HashSet<>();
        List<CompletableFuture<Chunk>> futures = new ArrayList<>();

        for (KeyframeAnimation animation : animations) {
            for (Keyframe keyframe : animation.getKeyframes()) {
                Location loc = keyframe.getLocation();
                World world = loc.getWorld();
                int chunkX = loc.getBlockX() >> 4;
                int chunkZ = loc.getBlockZ() >> 4;
                String key = world.getName() + ":" + chunkX + ":" + chunkZ;

                if (seenChunks.add(key)) {
                    futures.add(world.getChunkAtAsync(chunkX, chunkZ));
                }
            }
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenRun(onComplete);
    }

    private void playNext() {
        if (animations.isEmpty()) {
            finish();
            return;
        }

        KeyframeAnimation animation = animations.poll();
        Location firstLoc = animation.getKeyframes().get(0).getLocation();

        Bukkit.getScheduler().runTask(KeyframeAPI.getInstance(), () -> {
            player.teleport(firstLoc);
            Bukkit.getScheduler().runTaskLater(KeyframeAPI.getInstance(), () ->
                    KeyframeAPI.playKeyframeAnimation(player, animation), 1L);
        });
        if (played == 0) {
            new KeyframeSequenceStartEvent(player, this).call();
        } else {
            new KeyframeSequenceSwitchEvent(player, this).call();
        }
        played++;
    }

    @EventHandler
    public void onKeyframeEnd(KeyframeEndEvent event) {
        if (!event.getPlayer().equals(player)) return;
        Bukkit.getScheduler().runTaskLater(KeyframeAPI.getInstance(), this::playNext, 1L);
    }

    private void finish() {
        active = false;
        HandlerList.unregisterAll(this);

        new KeyframeSequenceEndEvent(player, this).call();
        player.setGameMode(originalGamemode);
    }
}

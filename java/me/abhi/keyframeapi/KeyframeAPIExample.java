package me.abhi.keyframeapi;

import me.abhi.keyframeapi.keyframe.KeyframeAnimationSequence;
import me.abhi.keyframeapi.keyframe.event.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class KeyframeAPIExample implements Listener {

    public KeyframeAPIExample() {
        Bukkit.getPluginManager().registerEvents(this, KeyframeAPI.getInstance());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!KeyframeAPI.keyframeAnimationExists("join1") || !KeyframeAPI.keyframeAnimationExists("join2")) return;

        KeyframeAnimationSequence keyframeAnimationSequence = new KeyframeAnimationSequence(
                "join",
                player,
                KeyframeAPI.getKeyframeAnimation("join1"),
                KeyframeAPI.getKeyframeAnimation("join2"));

        keyframeAnimationSequence.play();
    }

    @EventHandler
    public void onKeyframeStart(KeyframeStartEvent event) {
        Player player = event.getPlayer();

        player.sendMessage(ChatColor.GREEN + "Keyframe Animation Started");
    }

    @EventHandler
    public void onKeyframeEnd(KeyframeEndEvent event) {
        Player player = event.getPlayer();

        player.sendMessage(ChatColor.GREEN + "Keyframe Animation Ended");
    }

    @EventHandler
    public void onKeyframeCancel(KeyframeCancelEvent event) {
        Player player = event.getPlayer();

        player.sendMessage(ChatColor.GREEN + "Keyframe Animation Cancelled");
    }

    @EventHandler
    public void onSequenceStart(KeyframeSequenceStartEvent event) {
        Player player = event.getPlayer();

        player.sendTitle(ChatColor.GOLD + "Welcome to the first scene!", null, 10, 40, 20);

    }

    @EventHandler
    public void onSequenceSwitch(KeyframeSequenceSwitchEvent event) {
        Player player = event.getPlayer();

        player.sendTitle(ChatColor.GOLD + "Welcome to the next scene!", null, 10, 40, 20);
    }

    @EventHandler
    public void onSequenceEnd(KeyframeSequenceEndEvent event) {
        Player player = event.getPlayer();

        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.sendMessage(ChatColor.GOLD + "Have fun!");
    }
}

package me.abhi.keyframeapi.keyframe.command;

import lombok.RequiredArgsConstructor;
import me.abhi.keyframeapi.command.framework.Command;
import me.abhi.keyframeapi.command.framework.Param;
import me.abhi.keyframeapi.keyframe.KeyframeAnimation;
import me.abhi.keyframeapi.keyframe.KeyframeHandler;
import me.abhi.keyframeapi.keyframe.PlayerAnimator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@RequiredArgsConstructor
public class KeyframeCommand {

    private final KeyframeHandler keyframeHandler;

    @Command(name = "keyframe", permission = "keyframeapi.command")
    public void keyframe(CommandSender sender) {
        sender.sendMessage(new String[]{
                ChatColor.RED + "Keyframe Help: ",
                ChatColor.RED + "/keyframe create <name>",
                ChatColor.RED + "/keyframe start <name>",
                ChatColor.RED + "/keyframe play <name>",
                ChatColor.RED + "/keyframe stop",
                ChatColor.RED + "/keyframe delete <name>",
                ChatColor.RED + "/keyframe list"
        });
    }

    @Command(name = "keyframe.create", permission = "keyframeapi.command")
    public void keyframeCreate(CommandSender sender, @Param(name = "animationName") String animationName) {

        if (keyframeHandler.getByName(animationName) != null) {
            sender.sendMessage(ChatColor.RED + "There is already an animation with this name.");
            return;
        }
        KeyframeAnimation animation = new KeyframeAnimation(animationName);
        animation.save();

        keyframeHandler.addKeyframeAnimation(animation);

        sender.sendMessage(ChatColor.GREEN + "Keyframe animation with name \"" + animationName + "\" has been created.");
        return;
    }

    @Command(name = "keyframe.start", inGameOnly = true, permission = "keyframeapi.command")
    public void startKeyframe(CommandSender sender, @Param(name = "animationName") String animationName) {

        Player player = (Player) sender;
        if (keyframeHandler.isPlayerRecording(player)) {
            player.sendMessage(ChatColor.RED + "You are already recording a keyframe.");
            return;
        }
        if (keyframeHandler.getByName(animationName) == null) {
            player.sendMessage(ChatColor.RED + "There is no animation with this name.");
            return;
        }

        keyframeHandler.getRecordingAnimation().put(player.getUniqueId(), animationName);
        keyframeHandler.getRecordingKeyframes().put(player.getUniqueId(), new ArrayList<>());

        if (!player.getInventory().contains(keyframeHandler.getKeyframeItem())) {
            player.getInventory().addItem(keyframeHandler.getKeyframeItem());
            player.updateInventory();
        }

        player.sendMessage(ChatColor.GREEN + "Started recording keyframe. Use the Keyframe tool to add keyframes.");
        return;
    }

    @Command(name = "keyframe.stop", inGameOnly = true, permission = "keyframeapi.command")
    public void stopKeyframe(CommandSender sender) {
        Player player = (Player) sender;

        if (!keyframeHandler.isPlayerRecording(player)) {
            player.sendMessage(ChatColor.RED + "You are not recording!");
            return;
        }
        keyframeHandler.endPlayerRecording(player);
        player.sendMessage(ChatColor.GREEN + "You have ended the recording!");
        return;
    }

    @Command(name = "keyframe.play", inGameOnly = true, permission = "keyframeapi.command")
    public void playKeyframe(CommandSender sender, @Param(name = "animationName") String animationName) {
        Player player = (Player) sender;

        if (keyframeHandler.getByName(animationName) == null) {
            player.sendMessage(ChatColor.RED + "There is no animation with this name.");
            return;
        }

        KeyframeAnimation keyframeAnimation = keyframeHandler.getByName(animationName);
        PlayerAnimator animator = new PlayerAnimator(player, keyframeAnimation);
        animator.play();
        return;
    }

    @Command(name = "keyframe.delete", permission = "keyframeapi.command")
    public void deleteKeyframe(CommandSender sender, @Param(name = "animationName") String animationName) {

        if (keyframeHandler.getByName(animationName) == null) {
            sender.sendMessage(ChatColor.RED + "There is no animation with this name.");
            return;
        }

        KeyframeAnimation keyframeAnimation = keyframeHandler.getByName(animationName);
        keyframeAnimation.delete();

        keyframeHandler.removeKeyframeAnimation(keyframeAnimation);

        sender.sendMessage(ChatColor.GREEN + "Deleted keyframe animation with name \"" + animationName + "\".");
        return;
    }

    @Command(name = "keyframe.list", permission = "keyframeapi.command")
    public void listKeyframes(CommandSender sender) {

        if (keyframeHandler.getKeyframeAnimationList().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "There are no animation recordings.");
            return;
        }

        sender.sendMessage(ChatColor.DARK_PURPLE + "Keyframe list:");
        keyframeHandler.getKeyframeAnimationList().stream().forEach(anim ->
            sender.sendMessage(ChatColor.GRAY + " * " + anim.getName())
        );
        return;
    }
}

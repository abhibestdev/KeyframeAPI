package me.abhi.keyframeapi;

import lombok.Getter;
import me.abhi.keyframeapi.command.KeyframeCommandHandler;
import me.abhi.keyframeapi.keyframe.KeyframeAnimation;
import me.abhi.keyframeapi.keyframe.KeyframeHandler;
import me.abhi.keyframeapi.keyframe.PlayerAnimator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class KeyframeAPI extends JavaPlugin {

    @Getter
    private static KeyframeAPI instance;

    @Getter
    private KeyframeCommandHandler keyframeCommandHandler;

    @Getter
    private static KeyframeHandler keyframeHandler;

    @Override
    public void onEnable() {
        instance = this;

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        registerHandlers();

       // new KeyframeAPIExample();
    }

    private void registerHandlers() {
        keyframeCommandHandler = new KeyframeCommandHandler();
        keyframeHandler = new KeyframeHandler();
    }

    public static boolean keyframeAnimationExists(String name) {
        return keyframeHandler.getByName(name) != null;
    }

    public static void deleteKeyframeAnimation(KeyframeAnimation keyframeAnimation) {
        keyframeAnimation.delete();
        keyframeHandler.removeKeyframeAnimation(keyframeAnimation);
    }

    public static KeyframeAnimation createKeyframeAnimation(String name) {
        return new KeyframeAnimation(name);
    }

    public static KeyframeAnimation getKeyframeAnimation(String name) {
        return keyframeHandler.getByName(name);
    }

    public static void playKeyframeAnimation(Player player, KeyframeAnimation keyframeAnimation) {
        PlayerAnimator playerAnimator = new PlayerAnimator(player, keyframeAnimation);
        playerAnimator.play();
    }

    public static List<KeyframeAnimation> getKeyframeAnimations() {
        return keyframeHandler.getKeyframeAnimationList();
    }

    public static boolean isPlayingAnimation(Player player) {
        return keyframeHandler.isPlayingAnimation(player);
    }

    public static boolean isRecordingAnimation(Player player) {
        return keyframeHandler.getRecordingAnimation().containsKey(player.getUniqueId());
    }

}
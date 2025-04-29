package me.abhi.keyframeapi.keyframe.event;

import lombok.Getter;
import me.abhi.keyframeapi.event.PlayerEvent;
import me.abhi.keyframeapi.keyframe.KeyframeAnimation;
import me.abhi.keyframeapi.keyframe.PlayerAnimator;
import org.bukkit.entity.Player;

@Getter
public class KeyframeStartEvent extends PlayerEvent {

    private KeyframeAnimation keyframeAnimation;
    private PlayerAnimator playerAnimator;

    public KeyframeStartEvent(Player player, KeyframeAnimation keyframeAnimation, PlayerAnimator playerAnimator) {
        super(player);

        this.keyframeAnimation = keyframeAnimation;
        this.playerAnimator = playerAnimator;
    }
}

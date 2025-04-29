package me.abhi.keyframeapi.keyframe.event;

import lombok.Getter;
import me.abhi.keyframeapi.event.PlayerEvent;
import me.abhi.keyframeapi.keyframe.KeyframeAnimationSequence;
import org.bukkit.entity.Player;

@Getter
public class KeyframeSequenceSwitchEvent extends PlayerEvent {

    private KeyframeAnimationSequence keyframeAnimationSequence;

    public KeyframeSequenceSwitchEvent(Player player, KeyframeAnimationSequence keyframeAnimationSequence) {
        super(player);

        this.keyframeAnimationSequence = keyframeAnimationSequence;
    }
}

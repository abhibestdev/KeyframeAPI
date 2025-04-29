package me.abhi.keyframeapi.keyframe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@Getter
@RequiredArgsConstructor
public class Keyframe {

    private final double time;
    private final Location location;
}

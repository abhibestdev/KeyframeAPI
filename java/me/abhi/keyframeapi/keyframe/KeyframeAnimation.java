package me.abhi.keyframeapi.keyframe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.util.LocationUtil;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class KeyframeAnimation {

    private final String name;
    private List<Keyframe> keyframes = new ArrayList<>();

    public void addKeyframe(Keyframe keyframe) {
        keyframes.add(keyframe);
        keyframes.sort((a, b) -> Double.compare(a.getTime(), b.getTime()));
    }

    public void save() {
        File folder = new File(KeyframeAPI.getInstance().getDataFolder(), "animations");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, name + ".yml");

        YamlConfiguration config = new YamlConfiguration();

        int index = 0;
        for (Keyframe keyframe : keyframes) {
            String path = "keyframes." + index;
            config.set(path + ".time", keyframe.getTime());
            config.set(path + ".location", LocationUtil.getStringFromLocation(keyframe.getLocation()));
            index++;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void delete() {
        File folder = new File(KeyframeAPI.getInstance().getDataFolder(), "animations");
        File file = new File(folder, name + ".yml");

        if (file.exists()) {
            file.delete();
        }
    }
}

package me.abhi.keyframeapi.command.parameter;

import com.google.common.collect.Maps;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import me.abhi.keyframeapi.command.framework.ParameterType;

import java.util.Map;

public class GameModeParameterType implements ParameterType<GameMode> {

    private static Map<String, GameMode> gameModeMap = Maps.newHashMap();

    @Override
    public GameMode transform(CommandSender sender, String source) {
        if (gameModeMap.containsKey(source.toLowerCase())) {
            return gameModeMap.get(source);
        }
        return null;
    }

    static {
        gameModeMap.put("0", GameMode.SURVIVAL);
        gameModeMap.put("s", GameMode.SURVIVAL);
        gameModeMap.put("survival", GameMode.SURVIVAL);
        gameModeMap.put("1", GameMode.CREATIVE);
        gameModeMap.put("c", GameMode.CREATIVE);
        gameModeMap.put("creative", GameMode.CREATIVE);
        gameModeMap.put("2", GameMode.ADVENTURE);
        gameModeMap.put("a", GameMode.ADVENTURE);
        gameModeMap.put("adventure", GameMode.ADVENTURE);
        gameModeMap.put("3", GameMode.SPECTATOR);
        gameModeMap.put("sp", GameMode.SPECTATOR);
        gameModeMap.put("spectator", GameMode.SPECTATOR);
    }
}

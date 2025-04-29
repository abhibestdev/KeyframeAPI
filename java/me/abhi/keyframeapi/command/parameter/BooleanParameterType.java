package me.abhi.keyframeapi.command.parameter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import me.abhi.keyframeapi.command.framework.ParameterType;

import java.util.HashMap;
import java.util.Map;

public class BooleanParameterType implements ParameterType<Boolean> {

    private static Map<String, Boolean> booleanMap = new HashMap<>();

    @Override
    public Boolean transform(CommandSender sender, String source) {
        if (!booleanMap.containsKey(source.toLowerCase())) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid boolean.");
            return null;
        }
        return booleanMap.get(source.toLowerCase());
    }

    static  {
        booleanMap.put("true", true);
        booleanMap.put("on", true);
        booleanMap.put("yes", true);
        booleanMap.put("false", false);
        booleanMap.put("off", false);
        booleanMap.put("no", false);
    }
}

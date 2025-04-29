package me.abhi.keyframeapi.command.parameter;

import org.bukkit.command.CommandSender;
import me.abhi.keyframeapi.command.framework.ParameterType;

public class StringParameterType implements ParameterType<String> {

    @Override
    public String transform(CommandSender sender, String source) {
        return source;
    }
}

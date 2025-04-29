package me.abhi.keyframeapi.command.framework;

import org.bukkit.command.CommandSender;

public interface ParameterType<T> {

    T transform(CommandSender sender, String source);
}

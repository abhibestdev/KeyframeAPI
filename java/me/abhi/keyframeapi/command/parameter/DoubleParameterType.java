package me.abhi.keyframeapi.command.parameter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import me.abhi.keyframeapi.command.framework.ParameterType;

public class DoubleParameterType implements ParameterType<Double> {

    @Override
    public Double transform(CommandSender sender, String source) {
        if (source.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return null;
        }
        try {
            double parsed = Double.parseDouble(source);
            if (Double.isNaN(parsed) || !Double.isFinite(parsed)) {
                sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
                return null;
            }
            return parsed;
        } catch (NumberFormatException ex) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return null;
        }
    }
}

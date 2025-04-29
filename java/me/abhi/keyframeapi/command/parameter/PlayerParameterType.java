package me.abhi.keyframeapi.command.parameter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.abhi.keyframeapi.command.framework.ParameterType;

public class PlayerParameterType implements ParameterType<Player> {

    @Override
    public Player transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equals("self") || source.equals(""))) {
            return (Player) sender;
        }
        Player player = Bukkit.getPlayer(source);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + source + "\" found.");
            return null;
        }
        return player;
    }
}

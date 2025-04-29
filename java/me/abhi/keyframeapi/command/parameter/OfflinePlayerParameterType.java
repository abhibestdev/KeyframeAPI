package me.abhi.keyframeapi.command.parameter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.abhi.keyframeapi.command.framework.ParameterType;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    @Override
    public OfflinePlayer transform(CommandSender sender, String source) {
        if (sender instanceof Player && (source.equals("self") || source.equals(""))) {
            return (OfflinePlayer) sender;
        }
        Player player = Bukkit.getPlayer(source);
        if (player != null) {
            return player;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(source);
        if (!offlinePlayer.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "No player with the name \"" + offlinePlayer.getName() + "\" has ever logged in.");
            return null;
        }
        return offlinePlayer;
    }
}

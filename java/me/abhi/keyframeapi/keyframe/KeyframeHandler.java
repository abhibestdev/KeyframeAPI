package me.abhi.keyframeapi.keyframe;

import lombok.Getter;
import me.abhi.keyframeapi.KeyframeAPI;
import me.abhi.keyframeapi.keyframe.command.KeyframeCommand;
import me.abhi.keyframeapi.keyframe.task.KeyframeVisibilityTask;
import me.abhi.keyframeapi.util.ItemBuilder;
import me.abhi.keyframeapi.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

@Getter
public class KeyframeHandler {

    private List<KeyframeAnimation> keyframeAnimationList = new ArrayList<>();
    private Map<UUID, Long> recordingStartTime = new HashMap<>();
    private Map<UUID, List<Keyframe>> recordingKeyframes = new HashMap<>();
    private Map<UUID, String> recordingAnimation = new HashMap<>();
    private List<UUID> playingAnimation = new ArrayList<>();

    private ItemStack keyframeItem = new ItemBuilder(Material.GOLDEN_HOE)
            .setName(ChatColor.GOLD + "Keyframe Tool")
            .setLore(ChatColor.DARK_PURPLE + "Keyframe Help:",
                    ChatColor.GRAY + "Right Click to add Keyframe",
                    ChatColor.GRAY + "Shift + Left Click to end recording").toItemStack();

    public KeyframeHandler() {
        loadKeyframeAnimations();
        registerCommands();
        registerListeners();
        registerTasks();
    }

    private void loadKeyframeAnimations() {
        File folder = new File(KeyframeAPI.getInstance().getDataFolder(), "animations");
        if (!folder.exists()) {
            folder.mkdirs();
            return;
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

            String animationName = file.getName().replace(".yml", "");
            KeyframeAnimation animation = new KeyframeAnimation(animationName);

            if (config.contains("keyframes")) {
                for (String key : config.getConfigurationSection("keyframes").getKeys(false)) {
                    double time = config.getDouble("keyframes." + key + ".time");
                    String locString = config.getString("keyframes." + key + ".location");

                    if (locString != null && !locString.isEmpty()) {
                        animation.addKeyframe(new Keyframe(time, LocationUtil.getLocationFromString(locString)));
                    }
                }
            }
            addKeyframeAnimation(animation);
        }
    }

    private void registerCommands() {
        KeyframeAPI.getInstance().getKeyframeCommandHandler().registerCommand(new KeyframeCommand(this));
    }

    private void registerTasks() {
        new KeyframeVisibilityTask(this).runTaskTimer(KeyframeAPI.getInstance(), 0L, 1L);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new KeyframeListener(this), KeyframeAPI.getInstance());
    }

    public void startPlayerRecording(Player player) {
        recordingStartTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    public void endPlayerRecording(Player player) {
        recordingStartTime.remove(player.getUniqueId());
    }

    public boolean isPlayerRecording(Player player) {
        return recordingStartTime.containsKey(player.getUniqueId());
    }

    public long getRecordingStartTime(Player player) {
        return recordingStartTime.get(player.getUniqueId());
    }

    public List<Keyframe> getRecordingKeyframes(Player player) {
        return recordingKeyframes.get(player.getUniqueId());
    }

    public void addKeyframeAnimation(KeyframeAnimation keyframeAnimation) {
        keyframeAnimationList.add(keyframeAnimation);
    }

    public void removeKeyframeAnimation(KeyframeAnimation keyframeAnimation) {
        keyframeAnimationList.remove(keyframeAnimation);
    }

    public boolean isPlayingAnimation(Player player) {
        return playingAnimation.contains(player.getUniqueId());
    }

    public void addPlayingAnimation(Player player) {
        playingAnimation.add(player.getUniqueId());
    }

    public void removePlayingAnimation(Player player) {
        playingAnimation.remove(player.getUniqueId());
    }

    public KeyframeAnimation getByName(String name) {
        return keyframeAnimationList.stream().filter(x -> x.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}

package me.abhi.keyframeapi.keyframe;

import lombok.RequiredArgsConstructor;
import me.abhi.keyframeapi.keyframe.event.KeyframeCancelEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeEndEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeStartEvent;
import me.abhi.keyframeapi.keyframe.event.KeyframeTickEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
public class KeyframeListener implements Listener {

    private final KeyframeHandler keyframeHandler;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (keyframeHandler.isPlayingAnimation(player)) event.setCancelled(true);

        if (!player.getItemInHand().equals(keyframeHandler.getKeyframeItem()) || !keyframeHandler.getRecordingAnimation().containsKey(player.getUniqueId())) return;

        event.setCancelled(true);

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (keyframeHandler.getRecordingAnimation().containsKey(player.getUniqueId()) && !keyframeHandler.isPlayerRecording(player)) {
                keyframeHandler.startPlayerRecording(player);
            }
            long startTime = keyframeHandler.getRecordingStartTime(player);
            double elapsedSeconds = (double) (System.currentTimeMillis() - startTime) / 1000L;
            Location location = player.getLocation().clone();

            keyframeHandler.getRecordingKeyframes(player).add(new Keyframe(elapsedSeconds, location));
            player.sendMessage(ChatColor.GREEN + "Added keyframe at " + String.format("%.2f", elapsedSeconds) + " seconds.");
            return;
        }
        if (player.isSneaking() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
            keyframeHandler.endPlayerRecording(player);

            String animationName = keyframeHandler.getRecordingAnimation().get(player.getUniqueId());
            KeyframeAnimation keyframeAnimation = keyframeHandler.getByName(animationName);

            if (keyframeAnimation == null) {
                player.sendMessage(ChatColor.RED + "Something went wrong... no animation was found.");
            } else {
                keyframeAnimation.getKeyframes().clear();
                keyframeHandler.getRecordingKeyframes(player).forEach(keyframeAnimation::addKeyframe);
                keyframeAnimation.save();

                player.sendMessage(ChatColor.GREEN + "You have ended the recording! Saved animation with name \"" + animationName + "\".");
            }

            keyframeHandler.endPlayerRecording(player);
            keyframeHandler.getRecordingAnimation().remove(player.getUniqueId());

            player.getInventory().remove(keyframeHandler.getKeyframeItem());
            player.updateInventory();
            return;
        }
    }

    @EventHandler
    public void onKeyframeStart(KeyframeStartEvent event) {
        Player player = event.getPlayer();
        PlayerAnimator playerAnimator = event.getPlayerAnimator();

        if (player == null || !player.isOnline()) {
            playerAnimator.stop();
        } else {
            keyframeHandler.addPlayingAnimation(player);
        }
    }

    @EventHandler
    public void onKeyframeTick(KeyframeTickEvent event) {
        Player player = event.getPlayer();
        PlayerAnimator playerAnimator = event.getPlayerAnimator();

        if (player == null || !player.isOnline()) {
            playerAnimator.stop();
        }
    }

    @EventHandler
    public void onKeyframeEnd(KeyframeEndEvent event) {
        Player player = event.getPlayer();
        keyframeHandler.removePlayingAnimation(player);
    }

    @EventHandler
    public void onKeyframeCancel(KeyframeCancelEvent event) {
        Player player = event.getPlayer();
        keyframeHandler.removePlayingAnimation(player);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (keyframeHandler.isPlayingAnimation(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (keyframeHandler.isPlayingAnimation(player)) {
            event.setCancelled(true);
        }
    }
}

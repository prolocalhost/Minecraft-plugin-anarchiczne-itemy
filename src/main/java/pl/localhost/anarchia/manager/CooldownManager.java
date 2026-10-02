package pl.localhost.anarchia.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final Main plugin;
    private final Map<UUID, Map<AnarchiaItemType, Long>> cooldowns = new HashMap<>();

    public CooldownManager(Main plugin) {
        this.plugin = plugin;
    }

    public boolean isOnCooldown(Player player, AnarchiaItemType type) {
        Map<AnarchiaItemType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return false;

        Long expireTime = playerCooldowns.get(type);
        if (expireTime == null) return false;

        return System.currentTimeMillis() < expireTime;
    }

    public double getRemainingSeconds(Player player, AnarchiaItemType type) {
        Map<AnarchiaItemType, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) return 0.0;

        Long expireTime = playerCooldowns.get(type);
        if (expireTime == null) return 0.0;

        long diff = expireTime - System.currentTimeMillis();
        if (diff <= 0) return 0.0;

        return Math.round((diff / 1000.0) * 10.0) / 10.0;
    }

    public void setCooldown(Player player, AnarchiaItemType type, int seconds) {
        if (seconds <= 0) return;
        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>())
                .put(type, System.currentTimeMillis() + (seconds * 1000L));
    }

    public void sendCooldownNotice(Player player, AnarchiaItemType type) {
        double rem = getRemainingSeconds(player, type);
        String msg = plugin.getConfigManager().getMessage("cooldown-actionbar")
                .replace("%time%", String.valueOf(rem));

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.8f, 0.6f);
    }
}

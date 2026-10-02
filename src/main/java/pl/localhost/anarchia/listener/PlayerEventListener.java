package pl.localhost.anarchia.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.*;

public class PlayerEventListener implements Listener {

    private final Main plugin;
    private final Map<UUID, List<ItemStack>> keptItems = new HashMap<>();

    public PlayerEventListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Iterator<ItemStack> iter = event.getDrops().iterator();
        List<ItemStack> saved = new ArrayList<>();

        while (iter.hasNext()) {
            ItemStack drop = iter.next();
            if (plugin.getItemManager().isKeepOnDeath(drop)) {
                saved.add(drop.clone());
                iter.remove();
            }
        }

        if (!saved.isEmpty()) {
            keptItems.put(victim.getUniqueId(), saved);
            victim.sendMessage(plugin.getConfigManager().color("&5&lAnarchiczny Kilof &e☆∞ &7zostal zachowany w Twoim ekwipunku!"));
        }

        Player killer = victim.getKiller();
        if (killer != null && !killer.equals(victim)) {
            ItemStack hand = killer.getInventory().getItemInMainHand();
            if (plugin.getItemManager().getType(hand) == AnarchiaItemType.EXCALIBUR) {
                int currentKills = plugin.getItemManager().getExcaliburKills(hand);
                int newKills = currentKills + 1;
                plugin.getItemManager().updateExcaliburLore(hand, newKills);
                killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.5f);
                killer.sendMessage(plugin.getConfigManager().color("&b&lExcalibur &8» &7Zabójstwo zaliczone! Licznik: &b" + newKills));
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> saved = keptItems.remove(player.getUniqueId());
        if (saved != null) {
            for (ItemStack is : saved) {
                player.getInventory().addItem(is);
            }
        }
    }
}

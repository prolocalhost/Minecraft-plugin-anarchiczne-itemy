package pl.localhost.anarchia.task;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

public class ArmorCheckTask extends BukkitRunnable {

    private final Main plugin;
    private int tickCounter = 0;

    public ArmorCheckTask(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        tickCounter++;
        for (Player player : Bukkit.getOnlinePlayers()) {
            ItemStack helmet = player.getInventory().getHelmet();
            if (helmet == null) continue;

            AnarchiaItemType type = plugin.getItemManager().getType(helmet);
            if (type == AnarchiaItemType.KORONA_ANARCHII) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 40, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false));

                if (tickCounter % 2 == 0) {
                    Location head = player.getEyeLocation().add(0, 0.45, 0);
                    player.getWorld().spawnParticle(Particle.CRIT, head, 3, 0.15, 0.05, 0.15, 0.01);
                }
            }
        }
    }
}

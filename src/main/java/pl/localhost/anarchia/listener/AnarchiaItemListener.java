package pl.localhost.anarchia.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.*;

public class AnarchiaItemListener implements Listener {

    private final Main plugin;
    private final Random random = new Random();

    public AnarchiaItemListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        AnarchiaItemType type = plugin.getItemManager().getType(item);
        if (type == null) return;

        Action action = event.getAction();
        boolean isRightClick = (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);

        if (!isRightClick) return;

        if (type == AnarchiaItemType.ANTY_COBWEB) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            int radius = plugin.getConfigManager().getInt("items.anty-cobweb.radius", 5);
            Location loc = player.getLocation();
            int cleared = 0;

            for (int x = -radius; x <= radius; x++) {
                for (int y = -2; y <= 3; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block b = loc.clone().add(x, y, z).getBlock();
                        if (b.getType() == Material.COBWEB) {
                            b.setType(Material.AIR);
                            b.getWorld().spawnParticle(Particle.SWEEP_ATTACK, b.getLocation().add(0.5, 0.5, 0.5), 1);
                            cleared++;
                        }
                    }
                }
            }

            if (cleared > 0) {
                player.sendMessage(plugin.getConfigManager().getMessage("anty-cobweb-success")
                        .replace("%count%", String.valueOf(cleared)));
                player.playSound(loc, Sound.ENTITY_SHEEP_SHEAR, 1.0f, 1.2f);
                player.playSound(loc, Sound.BLOCK_WOOL_BREAK, 1.0f, 1.0f);
                plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("anty-cobweb-none"));
            }
            return;
        }

        if (type == AnarchiaItemType.BOMBARDA_MAXIMA) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            Snowball projectile = player.launchProjectile(Snowball.class);
            projectile.setMetadata("anarchia_bombarda", new FixedMetadataValue(plugin, true));
            projectile.setVelocity(player.getLocation().getDirection().multiply(1.8));

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1.0f, 0.6f);
            player.getWorld().spawnParticle(Particle.FLAME, player.getLocation().add(0, 1.2, 0), 10, 0.2, 0.2, 0.2, 0.05);
            return;
        }

        if (type == AnarchiaItemType.TRAPBOX) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            Snowball projectile = player.launchProjectile(Snowball.class);
            projectile.setMetadata("anarchia_trapbox", new FixedMetadataValue(plugin, true));
            projectile.setVelocity(player.getLocation().getDirection().multiply(1.5));

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_SNOWBALL_THROW, 1.0f, 0.8f);
            return;
        }

        if (type == AnarchiaItemType.BOSKI_TOPOR) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            int dur = plugin.getConfigManager().getInt("items.boski-topor.protection-duration", 3);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, dur * 20, 3, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, dur * 20, 0, false, false));

            int pushRadius = plugin.getConfigManager().getInt("items.boski-topor.push-radius", 6);
            for (Entity e : player.getNearbyEntities(pushRadius, pushRadius, pushRadius)) {
                if (e instanceof LivingEntity && !e.equals(player)) {
                    Vector diff = e.getLocation().toVector().subtract(player.getLocation().toVector());
                    if (diff.lengthSquared() > 0) {
                        Vector vel = diff.normalize().multiply(1.8).setY(0.7);
                        e.setVelocity(vel);
                    }
                    e.getWorld().spawnParticle(Particle.CRIT, e.getLocation(), 15, 0.3, 0.5, 0.3, 0.1);
                }
            }

            player.getWorld().strikeLightningEffect(player.getLocation());
            player.sendMessage(plugin.getConfigManager().getMessage("axe-shield-activated"));
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1.0f, 1.0f);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));
            return;
        }

        if (type == AnarchiaItemType.EXCALIBUR) {
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            LivingEntity target = getTargetEntity(player, 14);
            if (target != null) {
                target.getWorld().strikeLightningEffect(target.getLocation());
                target.damage(6.0, player);
                player.sendMessage(plugin.getConfigManager().getMessage("excalibur-strike"));
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.8f, 1.2f);
                plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));
            } else {
                player.sendMessage(plugin.getConfigManager().color("&cNie znaleziono celu w zasiegu 14 kratek!"));
            }
            return;
        }

        if (type == AnarchiaItemType.KREW_WAMPIRA) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            double heal = plugin.getConfigManager().getDouble("items.krew-wampira.heal-amount", 12.0);
            double maxHp = player.getMaxHealth();
            player.setHealth(Math.min(maxHp, player.getHealth() + heal));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));

            player.getWorld().spawnParticle(Particle.HEART, player.getLocation().add(0, 1.5, 0), 8, 0.4, 0.4, 0.4);
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_DRINK, 1.0f, 1.1f);
            player.sendMessage(plugin.getConfigManager().getMessage("vampire-heal"));
            return;
        }

        if (type == AnarchiaItemType.KOSTKA_RUBIKA) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            Snowball projectile = player.launchProjectile(Snowball.class);
            projectile.setMetadata("anarchia_rubik", new FixedMetadataValue(plugin, true));
            projectile.setVelocity(player.getLocation().getDirection().multiply(1.6));

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDER_PEARL_THROW, 1.0f, 1.4f);
            return;
        }

        if (type == AnarchiaItemType.NISZCZYCIEL_BEDROCKA) {
            event.setCancelled(true);
            Block clicked = event.getClickedBlock();
            if (clicked == null || clicked.getType() != Material.BEDROCK) {
                player.sendMessage(plugin.getConfigManager().getMessage("bedrock-not-bedrock"));
                return;
            }

            if (clicked.getY() <= -63) {
                player.sendMessage(plugin.getConfigManager().getMessage("bedrock-void-deny"));
                return;
            }

            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            player.sendMessage(plugin.getConfigManager().getMessage("bedrock-destroying"));
            Location bLoc = clicked.getLocation();
            bLoc.getWorld().playSound(bLoc, Sound.ENTITY_TNT_PRIMED, 1.0f, 1.0f);

            int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
                bLoc.getWorld().spawnParticle(Particle.SMOKE_LARGE, bLoc.clone().add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2, 0.02);
                bLoc.getWorld().spawnParticle(Particle.LAVA, bLoc.clone().add(0.5, 0.5, 0.5), 2);
            }, 0L, 10L);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Bukkit.getScheduler().cancelTask(taskId);
                if (bLoc.getBlock().getType() == Material.BEDROCK) {
                    bLoc.getBlock().setType(Material.AIR);
                    bLoc.getWorld().playSound(bLoc, Sound.BLOCK_ANVIL_DESTROY, 1.0f, 0.8f);
                    bLoc.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, bLoc.clone().add(0.5, 0.5, 0.5), 1);
                    if (player.isOnline()) {
                        player.sendMessage(plugin.getConfigManager().getMessage("bedrock-destroyed"));
                    }
                }
            }, 60L);
            return;
        }

        if (type == AnarchiaItemType.LIZAK_ANARCHII) {
            event.setCancelled(true);
            if (plugin.getCooldownManager().isOnCooldown(player, type)) {
                plugin.getCooldownManager().sendCooldownNotice(player, type);
                return;
            }

            consumeItem(player, item);
            plugin.getCooldownManager().setCooldown(player, type, plugin.getConfigManager().getItemCooldown(type));

            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 600, 1));

            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1.0f, 1.2f);
            player.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 1.2, 0), 10, 0.4, 0.4, 0.4);
            player.sendMessage(plugin.getConfigManager().getMessage("lollipop-eaten"));
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Entity entity = event.getEntity();

        if (entity.hasMetadata("anarchia_bombarda")) {
            Location loc = entity.getLocation();
            loc.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, loc, 1);
            loc.getWorld().spawnParticle(Particle.FLAME, loc, 30, 0.5, 0.5, 0.5, 0.1);
            loc.getWorld().playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.5f, 0.8f);

            int radius = plugin.getConfigManager().getInt("items.bombarda-maxima.break-radius", 3);
            boolean breakObsidian = plugin.getConfigManager().getBoolean("items.bombarda-maxima.break-obsidian", true);

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block b = loc.clone().add(x, y, z).getBlock();
                        Material mat = b.getType();
                        if (mat != Material.BEDROCK && mat != Material.AIR) {
                            if (mat == Material.OBSIDIAN && !breakObsidian) continue;
                            b.breakNaturally();
                        }
                    }
                }
            }

            double power = plugin.getConfigManager().getDouble("items.bombarda-maxima.explosion-power", 4.5);
            for (Entity e : loc.getWorld().getNearbyEntities(loc, radius * 1.5, radius * 1.5, radius * 1.5)) {
                if (e instanceof LivingEntity) {
                    ((LivingEntity) e).damage(power * 2.0);
                    Vector v = e.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(1.5).setY(0.6);
                    e.setVelocity(v);
                }
            }
            return;
        }

        if (entity.hasMetadata("anarchia_trapbox")) {
            Location loc = entity.getLocation();
            List<Block> placedWebs = new ArrayList<>();

            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 2; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block b = loc.clone().add(x, y, z).getBlock();
                        if (b.getType() == Material.AIR) {
                            b.setType(Material.COBWEB);
                            placedWebs.add(b);
                        }
                    }
                }
            }

            loc.getWorld().playSound(loc, Sound.ENTITY_SPIDER_AMBIENT, 1.2f, 0.8f);

            int dur = plugin.getConfigManager().getInt("items.trapbox.trap-duration", 15);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Block b : placedWebs) {
                    if (b.getType() == Material.COBWEB) {
                        b.setType(Material.AIR);
                    }
                }
            }, dur * 20L);
            return;
        }

        if (entity.hasMetadata("anarchia_rubik")) {
            Entity hit = event.getHitEntity();
            if (hit instanceof Player) {
                Player victim = (Player) hit;
                shuffleHotbar(victim);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                victim.sendMessage(plugin.getConfigManager().getMessage("rubik-victim"));
                victim.playSound(victim.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 1.0f, 1.0f);
                victim.getWorld().spawnParticle(Particle.SPELL_WITCH, victim.getLocation().add(0, 1, 0), 20, 0.4, 0.4, 0.4);

                if (entity instanceof Snowball && ((Snowball) entity).getShooter() instanceof Player) {
                    Player shooter = (Player) ((Snowball) entity).getShooter();
                    shooter.sendMessage(plugin.getConfigManager().getMessage("rubik-used").replace("%player%", victim.getName()));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        Player damager = (Player) event.getDamager();
        ItemStack hand = damager.getInventory().getItemInMainHand();

        AnarchiaItemType type = plugin.getItemManager().getType(hand);

        if (type == AnarchiaItemType.EXCALIBUR) {
            int kills = plugin.getItemManager().getExcaliburKills(hand);
            double bonusPer5 = plugin.getConfigManager().getDouble("items.excalibur.bonus-damage-per-5-kills", 1.0);
            double maxBonus = plugin.getConfigManager().getDouble("items.excalibur.max-bonus-damage", 8.0);
            double totalBonus = Math.min(maxBonus, (kills / 5) * bonusPer5);

            event.setDamage(event.getDamage() + totalBonus);
            event.getEntity().getWorld().spawnParticle(Particle.CRIT_MAGIC, event.getEntity().getLocation().add(0, 1, 0), 10, 0.2, 0.2, 0.2);
            return;
        }

        ItemStack offhand = damager.getInventory().getItemInOffHand();
        boolean hasVampire = (type == AnarchiaItemType.KREW_WAMPIRA) || (plugin.getItemManager().getType(offhand) == AnarchiaItemType.KREW_WAMPIRA);
        if (hasVampire) {
            int chance = plugin.getConfigManager().getInt("items.krew-wampira.lifesteal-chance", 15);
            if (random.nextInt(100) < chance) {
                double newHp = Math.min(damager.getMaxHealth(), damager.getHealth() + 2.0);
                damager.setHealth(newHp);
                damager.getWorld().spawnParticle(Particle.HEART, damager.getLocation().add(0, 1.5, 0), 3, 0.2, 0.2, 0.2);
                damager.playSound(damager.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 1.8f);
            }
        }

        if (type == AnarchiaItemType.KOSTKA_RUBIKA && event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            if (!plugin.getCooldownManager().isOnCooldown(damager, type)) {
                shuffleHotbar(victim);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                victim.sendMessage(plugin.getConfigManager().getMessage("rubik-victim"));
                damager.sendMessage(plugin.getConfigManager().getMessage("rubik-used").replace("%player%", victim.getName()));
                plugin.getCooldownManager().setCooldown(damager, type, plugin.getConfigManager().getItemCooldown(type));
            }
        }
    }

    private void shuffleHotbar(Player player) {
        List<ItemStack> hotbarItems = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            hotbarItems.add(player.getInventory().getItem(i));
        }
        Collections.shuffle(hotbarItems);
        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, hotbarItems.get(i));
        }
    }

    private void consumeItem(Player player, ItemStack item) {
        if (player.getGameMode() == org.bukkit.GameMode.CREATIVE) return;
        item.setAmount(item.getAmount() - 1);
    }

    private LivingEntity getTargetEntity(Player player, int range) {
        Location eye = player.getEyeLocation();
        Vector dir = eye.getDirection().normalize();

        LivingEntity closest = null;
        double closestDist = Double.MAX_VALUE;

        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e instanceof LivingEntity && !e.equals(player)) {
                Vector toTarget = e.getLocation().add(0, 1, 0).toVector().subtract(eye.toVector());
                double dot = toTarget.normalize().dot(dir);
                if (dot > 0.85) {
                    double d = eye.distance(e.getLocation());
                    if (d < closestDist) {
                        closestDist = d;
                        closest = (LivingEntity) e;
                    }
                }
            }
        }
        return closest;
    }
}

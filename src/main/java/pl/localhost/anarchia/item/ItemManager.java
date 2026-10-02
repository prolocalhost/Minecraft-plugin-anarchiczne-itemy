package pl.localhost.anarchia.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.localhost.anarchia.Main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemManager {

    private final Main plugin;
    private final NamespacedKey itemTypeKey;
    private final NamespacedKey excaliburKillsKey;
    private final NamespacedKey keepOnDeathKey;

    public ItemManager(Main plugin) {
        this.plugin = plugin;
        this.itemTypeKey = new NamespacedKey(plugin, "anarchia_type");
        this.excaliburKillsKey = new NamespacedKey(plugin, "anarchia_kills");
        this.keepOnDeathKey = new NamespacedKey(plugin, "anarchia_keep_on_death");
    }

    public ItemStack createItem(AnarchiaItemType type, int amount) {
        Material mat = plugin.getConfigManager().getItemMaterial(type);
        ItemStack item = new ItemStack(mat, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(itemTypeKey, PersistentDataType.STRING, type.name());

        meta.setDisplayName(plugin.getConfigManager().getItemName(type));

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("cooldown", String.valueOf(plugin.getConfigManager().getItemCooldown(type)));

        if (type == AnarchiaItemType.ANTY_COBWEB) {
            placeholders.put("radius", String.valueOf(plugin.getConfigManager().getInt("items.anty-cobweb.radius", 5)));
        } else if (type == AnarchiaItemType.TRAPBOX) {
            placeholders.put("duration", String.valueOf(plugin.getConfigManager().getInt("items.trapbox.trap-duration", 15)));
        } else if (type == AnarchiaItemType.BOSKI_TOPOR) {
            placeholders.put("duration", String.valueOf(plugin.getConfigManager().getInt("items.boski-topor.protection-duration", 3)));
            meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
            meta.addEnchant(Enchantment.DURABILITY, 5, true);
        } else if (type == AnarchiaItemType.EXCALIBUR) {
            pdc.set(excaliburKillsKey, PersistentDataType.INTEGER, 0);
            placeholders.put("kills", "0");
            meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
            meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        } else if (type == AnarchiaItemType.KORONA_ANARCHII) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 5, true);
            meta.addEnchant(Enchantment.DURABILITY, 5, true);
            meta.addEnchant(Enchantment.MENDING, 1, true);
        } else if (type == AnarchiaItemType.KILOF_ANARCHII) {
            pdc.set(keepOnDeathKey, PersistentDataType.BYTE, (byte) 1);
            meta.addEnchant(Enchantment.DIG_SPEED, 7, true);
            meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 4, true);
            meta.addEnchant(Enchantment.DURABILITY, 10, true);
        }

        if (!meta.hasEnchants()) {
            meta.addEnchant(Enchantment.LUCK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        List<String> lore = plugin.getConfigManager().getItemLore(type, placeholders);
        meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }

    public AnarchiaItemType getType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String typeName = pdc.get(itemTypeKey, PersistentDataType.STRING);
        if (typeName == null) return null;

        try {
            return AnarchiaItemType.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isAnarchiaItem(ItemStack item) {
        return getType(item) != null;
    }

    public boolean isKeepOnDeath(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        Byte val = meta.getPersistentDataContainer().get(keepOnDeathKey, PersistentDataType.BYTE);
        return val != null && val == (byte) 1;
    }

    public int getExcaliburKills(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return 0;

        Integer kills = meta.getPersistentDataContainer().get(excaliburKillsKey, PersistentDataType.INTEGER);
        return kills != null ? kills : 0;
    }

    public void updateExcaliburLore(ItemStack item, int newKills) {
        if (item == null || !item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        meta.getPersistentDataContainer().set(excaliburKillsKey, PersistentDataType.INTEGER, newKills);

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("cooldown", String.valueOf(plugin.getConfigManager().getItemCooldown(AnarchiaItemType.EXCALIBUR)));
        placeholders.put("kills", String.valueOf(newKills));

        List<String> lore = plugin.getConfigManager().getItemLore(AnarchiaItemType.EXCALIBUR, placeholders);
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void registerRecipes() {
        if (!plugin.getConfigManager().getBoolean("recipes.enabled", true)) return;

        registerShapedRecipe("recipe_anty_cobweb", createItem(AnarchiaItemType.ANTY_COBWEB, 1),
                new String[]{" I ", "ISI", " D "},
                Map.of('I', Material.IRON_INGOT, 'S', Material.SHEARS, 'D', Material.DIAMOND));

        registerShapedRecipe("recipe_bombarda_maxima", createItem(AnarchiaItemType.BOMBARDA_MAXIMA, 1),
                new String[]{"BTB", "TFT", "BTB"},
                Map.of('B', Material.BLAZE_POWDER, 'T', Material.TNT, 'F', Material.FIRE_CHARGE));

        registerShapedRecipe("recipe_trapbox", createItem(AnarchiaItemType.TRAPBOX, 1),
                new String[]{"WWW", "WPW", "WWW"},
                Map.of('W', Material.COBWEB, 'P', Material.ENDER_PEARL));

        registerShapedRecipe("recipe_krew_wampira", createItem(AnarchiaItemType.KREW_WAMPIRA, 1),
                new String[]{"TNT", "NRN", "TNT"},
                Map.of('T', Material.GHAST_TEAR, 'N', Material.NETHER_WART, 'R', Material.RED_DYE));

        registerShapedRecipe("recipe_kostka_rubika", createItem(AnarchiaItemType.KOSTKA_RUBIKA, 1),
                new String[]{"ARA", "RCR", "ARA"},
                Map.of('A', Material.AMETHYST_SHARD, 'R', Material.REDSTONE_BLOCK, 'C', Material.CONDUIT));

        registerShapedRecipe("recipe_niszczyciel_bedrocka", createItem(AnarchiaItemType.NISZCZYCIEL_BEDROCKA, 1),
                new String[]{"ONO", "NTN", "ONO"},
                Map.of('O', Material.OBSIDIAN, 'N', Material.NETHERITE_SCRAP, 'T', Material.TNT));

        registerShapedRecipe("recipe_lizak_anarchii", createItem(AnarchiaItemType.LIZAK_ANARCHII, 1),
                new String[]{" S ", "ACA", " S "},
                Map.of('S', Material.SUGAR, 'A', Material.GOLDEN_APPLE, 'C', Material.COOKIE));
    }

    private void registerShapedRecipe(String key, ItemStack result, String[] shape, Map<Character, Material> ingredients) {
        NamespacedKey rKey = new NamespacedKey(plugin, key);
        try {
            Bukkit.removeRecipe(rKey);
        } catch (Exception ignored) {}

        ShapedRecipe recipe = new ShapedRecipe(rKey, result);
        recipe.shape(shape);
        for (Map.Entry<Character, Material> entry : ingredients.entrySet()) {
            recipe.setIngredient(entry.getKey(), entry.getValue());
        }
        try {
            Bukkit.addRecipe(recipe);
        } catch (Exception e) {
            plugin.getLogger().warning("Nie udalo sie zarejestrowac receptury: " + key);
        }
    }

    public NamespacedKey getItemTypeKey() {
        return itemTypeKey;
    }
}

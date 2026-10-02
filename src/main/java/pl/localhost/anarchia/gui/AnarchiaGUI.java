package pl.localhost.anarchia.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.ArrayList;
import java.util.List;

public class AnarchiaGUI implements Listener {

    private final Main plugin;
    private static final String GUI_IDENTIFIER = "§0§8";

    public AnarchiaGUI(Main plugin) {
        this.plugin = plugin;
    }

    public void openGUI(Player player) {
        String title = plugin.getConfigManager().color("&8» &6&lPrzedmioty Anarchia.GG") + GUI_IDENTIFIER;
        Inventory inv = Bukkit.createInventory(null, 54, title);

        ItemStack border = createGuiItem(Material.BLACK_STAINED_GLASS_PANE, " ");
        ItemStack accent = createGuiItem(Material.ORANGE_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, border);
        }

        int[] accents = {0, 8, 9, 17, 36, 44, 45, 53};
        for (int slot : accents) {
            inv.setItem(slot, accent);
        }

        ItemStack info = new ItemStack(Material.NETHER_STAR);
        ItemMeta infoMeta = info.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(plugin.getConfigManager().color("&6&lSystem Przedmiotów Anarchia.GG"));
            List<String> lore = new ArrayList<>();
            lore.add(plugin.getConfigManager().color("&7Oto zestaw unikalnych broni, pancerzy i gadżetów"));
            lore.add(plugin.getConfigManager().color("&7używanych w walkach i raidach na serwerze Anarchia."));
            lore.add("");
            lore.add(plugin.getConfigManager().color(" &8» &eDostępne przedmioty: &a11 unikalnych"));
            lore.add(plugin.getConfigManager().color(" &8» &eReceptury craftingu: &aWłączone"));
            if (player.hasPermission("anarchia.admin")) {
                lore.add("");
                lore.add(plugin.getConfigManager().color("&a[ADMIN] Kliknij przedmiot, aby go otrzymać!"));
            }
            infoMeta.setLore(lore);
            info.setItemMeta(infoMeta);
        }
        inv.setItem(4, info);

        AnarchiaItemType[] types = AnarchiaItemType.values();
        int[] itemSlots = {19, 20, 21, 22, 23, 24, 25, 29, 30, 31, 32};

        for (int i = 0; i < types.length && i < itemSlots.length; i++) {
            ItemStack item = plugin.getItemManager().createItem(types[i], 1);
            inv.setItem(itemSlots[i], item);
        }

        ItemStack close = createGuiItem(Material.BARRIER, "&c&lZamknij Menu");
        inv.setItem(49, close);

        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private ItemStack createGuiItem(Material mat, String name) {
        ItemStack is = new ItemStack(mat);
        ItemMeta meta = is.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.getConfigManager().color(name));
            is.setItemMeta(meta);
        }
        return is;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().endsWith(GUI_IDENTIFIER)) return;

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack current = event.getCurrentItem();
        if (current == null || current.getType() == Material.AIR) return;

        if (event.getRawSlot() == 49) {
            player.closeInventory();
            return;
        }

        AnarchiaItemType type = plugin.getItemManager().getType(current);
        if (type != null) {
            if (player.hasPermission("anarchia.admin")) {
                ItemStack given = plugin.getItemManager().createItem(type, 1);
                player.getInventory().addItem(given);
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7f, 1.2f);
                player.sendMessage(plugin.getConfigManager().getMessage("item-given")
                        .replace("%item%", plugin.getConfigManager().getItemName(type))
                        .replace("%player%", player.getName()));
            } else {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
            }
        }
    }
}

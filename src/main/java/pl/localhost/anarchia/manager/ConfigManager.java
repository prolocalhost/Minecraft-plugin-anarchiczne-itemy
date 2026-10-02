package pl.localhost.anarchia.manager;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;

    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        this.reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String getPrefix() {
        return color(config.getString("prefix", "&8[&6&lAnarchia&8] &r"));
    }

    public String getMessage(String key) {
        String msg = config.getString("messages." + key);
        if (msg == null) return color("&cWiadomość nieznaleziona: " + key);
        return color(getPrefix() + msg);
    }

    public String getItemName(AnarchiaItemType type) {
        String path = "items." + type.getConfigKey() + ".name";
        return color(config.getString(path, type.getDefaultName()));
    }

    public Material getItemMaterial(AnarchiaItemType type) {
        String path = "items." + type.getConfigKey() + ".material";
        String matName = config.getString(path);
        if (matName != null) {
            try {
                return Material.valueOf(matName.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return type.getDefaultMaterial();
    }

    public int getItemCooldown(AnarchiaItemType type) {
        String path = "items." + type.getConfigKey() + ".cooldown";
        return config.getInt(path, type.getDefaultCooldown());
    }

    public List<String> getItemLore(AnarchiaItemType type, Map<String, String> placeholders) {
        String path = "items." + type.getConfigKey() + ".lore";
        List<String> rawLore = config.getStringList(path);
        List<String> formatted = new ArrayList<>();

        for (String line : rawLore) {
            String processed = line;
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    processed = processed.replace("%" + entry.getKey() + "%", entry.getValue());
                }
            }
            formatted.add(color(processed));
        }
        return formatted;
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
}

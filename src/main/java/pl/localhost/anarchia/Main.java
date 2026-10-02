package pl.localhost.anarchia;

import org.bukkit.plugin.java.JavaPlugin;
import pl.localhost.anarchia.command.AnarchiaCommand;
import pl.localhost.anarchia.gui.AnarchiaGUI;
import pl.localhost.anarchia.item.ItemManager;
import pl.localhost.anarchia.listener.AnarchiaItemListener;
import pl.localhost.anarchia.listener.PlayerEventListener;
import pl.localhost.anarchia.manager.ConfigManager;
import pl.localhost.anarchia.manager.CooldownManager;
import pl.localhost.anarchia.task.ArmorCheckTask;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private CooldownManager cooldownManager;
    private ItemManager itemManager;
    private AnarchiaGUI anarchiaGUI;
    private ArmorCheckTask armorCheckTask;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.cooldownManager = new CooldownManager(this);
        this.itemManager = new ItemManager(this);
        this.anarchiaGUI = new AnarchiaGUI(this);

        AnarchiaCommand anarchiaCmd = new AnarchiaCommand(this);
        if (getCommand("anarchia") != null) {
            getCommand("anarchia").setExecutor(anarchiaCmd);
            getCommand("anarchia").setTabCompleter(anarchiaCmd);
        }

        getServer().getPluginManager().registerEvents(new AnarchiaItemListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getServer().getPluginManager().registerEvents(anarchiaGUI, this);

        this.itemManager.registerRecipes();

        this.armorCheckTask = new ArmorCheckTask(this);
        this.armorCheckTask.runTaskTimer(this, 20L, 20L);

        getLogger().info("localhost-anarchia zostal pomyslnie wlaczony!");
    }

    @Override
    public void onDisable() {
        if (this.armorCheckTask != null) {
            this.armorCheckTask.cancel();
        }
        getLogger().info("localhost-anarchia zostal wylaczony!");
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public AnarchiaGUI getAnarchiaGUI() {
        return anarchiaGUI;
    }
}

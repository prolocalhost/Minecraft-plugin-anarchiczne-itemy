package pl.localhost.anarchia.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import pl.localhost.anarchia.Main;
import pl.localhost.anarchia.item.AnarchiaItemType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnarchiaCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;

    public AnarchiaCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("gui") || args[0].equalsIgnoreCase("menu")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-only"));
                return true;
            }
            plugin.getAnarchiaGUI().openGUI((Player) sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("lista") || sub.equals("list")) {
            sender.sendMessage(plugin.getConfigManager().color("&8---------- &6&lPrzedmioty Anarchia.GG &8----------"));
            for (AnarchiaItemType type : AnarchiaItemType.values()) {
                String name = plugin.getConfigManager().getItemName(type);
                sender.sendMessage(plugin.getConfigManager().color(" &8» &e" + type.getConfigKey() + " &7- " + name));
            }
            sender.sendMessage(plugin.getConfigManager().color("&8------------------------------------------------"));
            return true;
        }

        if (sub.equals("reload") || sub.equals("przeladuj")) {
            if (!sender.hasPermission("anarchia.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }
            plugin.getConfigManager().reload();
            plugin.getItemManager().registerRecipes();
            sender.sendMessage(plugin.getConfigManager().getMessage("reload-success"));
            return true;
        }

        if (sub.equals("daj") || sub.equals("give")) {
            if (!sender.hasPermission("anarchia.admin")) {
                sender.sendMessage(plugin.getConfigManager().getMessage("no-permission"));
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(plugin.getConfigManager().color("&7Użycie: &e/anarchia daj <gracz> <przedmiot> [ilość]"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(plugin.getConfigManager().getMessage("player-not-found"));
                return true;
            }

            AnarchiaItemType itemType = AnarchiaItemType.fromKey(args[2]);
            if (itemType == null) {
                sender.sendMessage(plugin.getConfigManager().getMessage("item-not-found").replace("%id%", args[2]));
                return true;
            }

            int amount = 1;
            if (args.length >= 4) {
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignored) {}
            }

            ItemStack item = plugin.getItemManager().createItem(itemType, amount);
            target.getInventory().addItem(item);

            String itemName = plugin.getConfigManager().getItemName(itemType);
            sender.sendMessage(plugin.getConfigManager().getMessage("item-given")
                    .replace("%item%", itemName + " &7(x" + amount + ")")
                    .replace("%player%", target.getName()));

            target.sendMessage(plugin.getConfigManager().getMessage("item-received")
                    .replace("%item%", itemName + " &7(x" + amount + ")"));
            return true;
        }

        sender.sendMessage(plugin.getConfigManager().color("&7Nieznana podkomenda! Wpisz &e/anarchia &7lub &e/anarchia lista&7."));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("gui");
            list.add("lista");
            if (sender.hasPermission("anarchia.admin")) {
                list.add("daj");
                list.add("reload");
            }
            return filter(list, args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("daj")) {
            return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("daj")) {
            List<String> keys = Arrays.stream(AnarchiaItemType.values())
                    .map(AnarchiaItemType::getConfigKey)
                    .collect(Collectors.toList());
            return filter(keys, args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("daj")) {
            return filter(Arrays.asList("1", "16", "32", "64"), args[3]);
        }

        return list;
    }

    private List<String> filter(List<String> list, String query) {
        String lower = query.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}

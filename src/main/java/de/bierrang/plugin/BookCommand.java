package de.bierrang.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BookCommand implements CommandExecutor {
    private final BierRuestung plugin;
    public BookCommand(BierRuestung plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("bierarmor.admin")) {
            sender.sendMessage(ChatColor.RED + "Keine Rechte.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Nutze: /beaconbook <name>");
            sender.sendMessage(ChatColor.GRAY + "Verfügbare: vampire, giant, tank, telekinese, blitz, gift, erfolg, gier, schmelzen, explosiv");
            return true;
        }

        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        String id = args[0].toLowerCase();
        
        plugin.getArmorManager().applySpecialEnchant(book, id, 1);
        
        org.bukkit.inventory.meta.ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Spezial-Buch: " + id);
        book.setItemMeta(meta);

        if (sender instanceof Player p) p.getInventory().addItem(book);
        sender.sendMessage(ChatColor.GREEN + "Buch '" + id + "' erhalten!");
        return true;
    }
}

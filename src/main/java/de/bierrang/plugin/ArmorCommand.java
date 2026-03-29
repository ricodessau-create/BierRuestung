package de.bierrang.plugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorCommand implements CommandExecutor {
    private final BierRuestung plugin;
    public ArmorCommand(BierRuestung plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("bierarmor.admin")) { sender.sendMessage(ChatColor.RED + "Keine Rechte."); return true; }

        // Gibt das ganze Set
        p.getInventory().addItem(plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_HELMET));
        p.getInventory().addItem(plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_CHESTPLATE));
        p.getInventory().addItem(plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_LEGGINGS));
        p.getInventory().addItem(plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_BOOTS));
        p.sendMessage(ChatColor.GREEN + "Du hast die Beacon Rüstung erhalten!");
        return true;
    }
}

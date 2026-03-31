package de.bierrang.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ArmorGUI implements Listener {

    private final BierRuestung plugin;
    private final String title = "§6Beacon Rüstung";

    public ArmorGUI(BierRuestung plugin) {
        this.plugin = plugin;
    }

    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, title);

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        org.bukkit.inventory.meta.ItemMeta glassMeta = glass.getItemMeta();
        glassMeta.setDisplayName(" ");
        glass.setItemMeta(glassMeta);
        for (int i = 0; i < 27; i++) inv.setItem(i, glass);

        ItemStack helmet = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_HELMET);
        ItemStack chest = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_CHESTPLATE);
        ItemStack legs = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_LEGGINGS);
        ItemStack boots = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_BOOTS);

        inv.setItem(10, helmet);
        inv.setItem(12, chest);
        inv.setItem(14, legs);
        inv.setItem(16, boots);

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals(title)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.hasPermission("bierarmor.admin")) return;

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR || clicked.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        p.getInventory().addItem(clicked.clone());
        p.sendMessage(ChatColor.GREEN + "Du hast " + clicked.getItemMeta().getDisplayName() + " erhalten!");
        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
    }
}

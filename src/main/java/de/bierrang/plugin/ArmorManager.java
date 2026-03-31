package de.bierrang.plugin;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class ArmorManager {

    private final BierRuestung plugin;

    public ArmorManager(BierRuestung plugin) {
        this.plugin = plugin;
    }

    public ItemStack createBeaconArmor(Material baseMat) {
        ItemStack item = new ItemStack(baseMat);
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§6Beacon " + getFriendlyName(baseMat));
        meta.setUnbreakable(true);
        
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "beacon_armor"), PersistentDataType.BYTE, (byte) 1);

        addAttribute(meta, Attribute.GENERIC_ARMOR, getArmorValue(baseMat, 25), EquipmentSlotGroup.ARMOR);
        addAttribute(meta, Attribute.GENERIC_ARMOR_TOUGHNESS, getArmorValue(baseMat, 15), EquipmentSlotGroup.ARMOR);
        addAttribute(meta, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.2, EquipmentSlotGroup.ARMOR);
        addAttribute(meta, Attribute.GENERIC_MOVEMENT_SPEED, 0.05, EquipmentSlotGroup.ARMOR);

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack applySpecialEnchant(ItemStack item, String id, int level) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        
        java.util.List<String> lore = meta.hasLore() ? meta.getLore() : new java.util.ArrayList<>();
        String display = switch (id.toLowerCase()) {
            case "giant" -> "§d§lRiese " + toRoman(level);
            case "tank" -> "§d§lPanzer " + toRoman(level);
            case "vampire" -> "§d§lVampir " + toRoman(level);
            case "telekinese" -> "§d§lTelekinese " + toRoman(level);
            case "blitz" -> "§d§lBlitz " + toRoman(level);
            case "gift" -> "§d§lGift " + toRoman(level);
            case "erfolg" -> "§d§lErfolg " + toRoman(level);
            case "gier" -> "§d§lGier " + toRoman(level);
            case "schmelzen" -> "§d§lSchmelzen " + toRoman(level);
            case "explosiv" -> "§d§lExplosiv " + toRoman(level);
            default -> "§d§l" + id + " " + toRoman(level);
        };
        lore.add(display);
        meta.setLore(lore);
        
        meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "custom_enchant_" + id), PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
        return item;
    }

    private void addAttribute(ItemMeta meta, Attribute attr, double amount, EquipmentSlotGroup slot) {
        meta.addAttributeModifier(attr, new AttributeModifier(UUID.randomUUID(), "bier.armor", amount, AttributeModifier.Operation.ADD_NUMBER, slot));
    }

    private double getArmorValue(Material type, int total) {
        return switch (type) {
            case DIAMOND_HELMET -> Math.round(total * 0.15);
            case DIAMOND_CHESTPLATE -> Math.round(total * 0.40);
            case DIAMOND_LEGGINGS -> Math.round(total * 0.30);
            case DIAMOND_BOOTS -> Math.round(total * 0.15);
            default -> 1;
        };
    }

    private String getFriendlyName(Material type) {
        return type.name().replace("DIAMOND_", "").replace("_", " ");
    }

    private String toRoman(int n) {
        return switch (n) {
            case 1 -> "I"; case 2 -> "II"; case 3 -> "III"; case 4 -> "IV"; case 5 -> "V";
            default -> String.valueOf(n);
        };
    }
    }

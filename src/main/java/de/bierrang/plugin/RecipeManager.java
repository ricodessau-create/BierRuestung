package de.bierrang.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeManager {

    public static void registerRecipes(BierRuestung plugin) {
        
        // Helm
        ItemStack helmet = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_HELMET);
        NamespacedKey keyH = new NamespacedKey(plugin, "beacon_helmet");
        ShapedRecipe recipeH = new ShapedRecipe(keyH, helmet);
        recipeH.shape("BBB", "B B", "   ");
        recipeH.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(recipeH);

        // Brustpanzer
        ItemStack chest = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_CHESTPLATE);
        NamespacedKey keyC = new NamespacedKey(plugin, "beacon_chest");
        ShapedRecipe recipeC = new ShapedRecipe(keyC, chest);
        recipeC.shape("B B", "BBB", "BBB");
        recipeC.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(recipeC);

        // Hose
        ItemStack legs = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_LEGGINGS);
        NamespacedKey keyL = new NamespacedKey(plugin, "beacon_legs");
        ShapedRecipe recipeL = new ShapedRecipe(keyL, legs);
        recipeL.shape("BBB", "B B", "B B");
        recipeL.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(recipeL);

        // Stiefel
        ItemStack boots = plugin.getArmorManager().createBeaconArmor(Material.DIAMOND_BOOTS);
        NamespacedKey keyB = new NamespacedKey(plugin, "beacon_boots");
        ShapedRecipe recipeB = new ShapedRecipe(keyB, boots);
        recipeB.shape("   ", "B B", "B B");
        recipeB.setIngredient('B', Material.BEACON);
        Bukkit.addRecipe(recipeB);
    }
}

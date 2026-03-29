package de.bierrang.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class BierRuestung extends JavaPlugin {

    private static BierRuestung instance;
    private ArmorManager armorManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        armorManager = new ArmorManager(this);

        getCommand("bierarmor").setExecutor(new ArmorCommand(this));
        getCommand("beaconbook").setExecutor(new BookCommand(this));
        
        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);

        // Rezepte registrieren
        RecipeManager.registerRecipes(this);

        getLogger().info("BierRuestung geladen!");
    }

    public static BierRuestung getInstance() { return instance; }
    public ArmorManager getArmorManager() { return armorManager; }
}

package de.bierrang.plugin;

import org.bukkit.plugin.java.JavaPlugin;

public class BierRuestung extends JavaPlugin {

    private static BierRuestung instance;
    private ArmorManager armorManager;
    private ArmorGUI armorGUI;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        armorManager = new ArmorManager(this);
        armorGUI = new ArmorGUI(this);

        getCommand("armorGUI").setExecutor(new ArmorGUICommand(this));
        getCommand("beaconbook").setExecutor(new BookCommand(this));
        
        getServer().getPluginManager().registerEvents(new ArmorListener(this), this);
        getServer().getPluginManager().registerEvents(armorGUI, this);

        RecipeManager.registerRecipes(this);

        getLogger().info("BierRuestung v1.1.0 geladen!");
    }

    public static BierRuestung getInstance() { return instance; }
    public ArmorManager getArmorManager() { return armorManager; }
    public ArmorGUI getArmorGUI() { return armorGUI; }
}

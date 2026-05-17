package de.bierrang.plugin;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class ArmorListener implements Listener {

    private final BierRuestung plugin;
    private final Map<UUID, String> activeModes = new HashMap<>();

    public ArmorListener(BierRuestung plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.ANVIL) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;
        AnvilInventory anvil = (AnvilInventory) e.getInventory();
        ItemStack result = anvil.getItem(2);

        if (result != null && isBeaconItem(result)) {
            if (!p.hasPermission("bierarmor.admin")) {
                e.setCancelled(true);
                p.sendMessage(ChatColor.RED + "Nur Admins dürfen Beacon Items verzaubern!");
                p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_BREAK, 1, 1);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        if (!(e.getEntity() instanceof LivingEntity target)) return;

        ItemStack mainHand = p.getInventory().getItemInMainHand();
        List<ItemStack> itemsToCheck = new ArrayList<>(Arrays.asList(p.getInventory().getArmorContents()));
        itemsToCheck.add(mainHand);

        for (ItemStack item : itemsToCheck) {
            if (item == null || item.getType().isAir()) continue;

            if (hasEnchant(item, "vampire")) {
                double heal = e.getDamage() * 0.25;
                double max = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                p.setHealth(Math.min(p.getHealth() + heal, max));
                p.spawnParticle(Particle.HEART, p.getLocation().add(0, 1, 0), 3);
            }

            if (hasEnchant(item, "giant")) {
                e.setDamage(e.getDamage() * 1.25);
            }

            if (hasEnchant(item, "blitz")) {
                if (new Random().nextDouble() < 0.15) {
                    target.getWorld().strikeLightningEffect(target.getLocation());
                    target.damage(5.0);
                }
            }

            if (hasEnchant(item, "gift")) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1, false, true, true));
            }

            if (hasEnchant(item, "gier")) {
                if (new Random().nextDouble() < 0.10) {
                    double amount = 10.0 + (new Random().nextDouble() * 20.0);
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + p.getName() + " " + (int)amount);
                    p.sendMessage(ChatColor.GOLD + "+" + (int)amount + " Coins (Gier)");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        Block block = e.getBlock();
        
        ItemStack handItem = p.getInventory().getItemInMainHand();
        List<ItemStack> itemsToCheck = new ArrayList<>(Arrays.asList(p.getInventory().getArmorContents()));
        itemsToCheck.add(handItem);

        boolean hasTelekinese = false;
        boolean hasSchmelzen = false;
        boolean hasExplosiv = false;

        for(ItemStack item : itemsToCheck) {
            if (item == null) continue;
            if (hasEnchant(item, "telekinese")) hasTelekinese = true;
            if (hasEnchant(item, "schmelzen")) hasSchmelzen = true;
            if (hasEnchant(item, "explosiv")) hasExplosiv = true;
        }

        if (!hasTelekinese && !hasSchmelzen && !hasExplosiv) return;
        if (block.getState() instanceof Container) return;

        if (hasExplosiv) {
            int radius = 1;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        if (x == 0 && y == 0 && z == 0) continue;
                        
                        Location loc = block.getLocation().clone().add(x, y, z);
                        Block relative = loc.getBlock();
                        
                        if (relative.getType() != Material.AIR && relative.getType().getHardness() >= 0) {
                            if (hasSchmelzen) {
                                dropSmelted(p, relative);
                            } else if (hasTelekinese) {
                                dropToInv(p, relative);
                            } else {
                                relative.breakNaturally(p.getInventory().getItemInMainHand());
                            }
                            relative.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        if (hasSchmelzen) {
            dropSmelted(p, block);
            e.setDropItems(false);
            e.setExpToDrop(0);
        } else if (hasTelekinese) {
            dropToInv(p, block);
            e.setDropItems(false);
            p.giveExp(e.getExpToDrop());
            e.setExpToDrop(0);
        }
    }

    private void dropSmelted(Player p, Block block) {
        Material type = block.getType();
        ItemStack result = getSmeltedResult(type);
        
        if (result != null) {
            HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(result);
            for (ItemStack drop : leftover.values()) block.getWorld().dropItemNaturally(block.getLocation(), drop);
            p.giveExp(2); 
        } else {
            dropToInv(p, block);
        }
        block.setType(Material.AIR);
    }

    private void dropToInv(Player p, Block block) {
        Collection<ItemStack> drops = block.getDrops(p.getInventory().getItemInMainHand());
        if (!drops.isEmpty()) {
            HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(drops.toArray(new ItemStack[0]));
            for (ItemStack drop : leftover.values()) block.getWorld().dropItemNaturally(block.getLocation(), drop);
        }
        block.setType(Material.AIR);
    }

    private ItemStack getSmeltedResult(Material ore) {
        if (ore == Material.IRON_ORE || ore == Material.DEEPSLATE_IRON_ORE) return new ItemStack(Material.IRON_INGOT);
        if (ore == Material.GOLD_ORE || ore == Material.DEEPSLATE_GOLD_ORE) return new ItemStack(Material.GOLD_INGOT);
        if (ore == Material.COPPER_ORE || ore == Material.DEEPSLATE_COPPER_ORE) return new ItemStack(Material.COPPER_INGOT);
        if (ore == Material.NETHER_GOLD_ORE) return new ItemStack(Material.GOLD_INGOT);
        if (ore == Material.ANCIENT_DEBRIS) return new ItemStack(Material.NETHERITE_SCRAP);
        return null;
    }

    @EventHandler
    public void onXP(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        for (ItemStack item : p.getInventory().getArmorContents()) {
            if (item != null && hasEnchant(item, "erfolg")) {
                e.setAmount((int)(e.getAmount() * 1.5));
                return;
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        String currentMode = activeModes.getOrDefault(p.getUniqueId(), "none");
        
        // NEUE LOGIK: Prüft ob ALLE 4 Teile den Enchant haben
        boolean hasGiant = hasFullSetEnchant(p, "giant");
        boolean hasTank = hasFullSetEnchant(p, "tank");
        
        String newMode = "none";
        if (hasGiant) newMode = "giant";
        if (hasTank) newMode = "tank";

        if (!currentMode.equals(newMode)) {
            resetAttributes(p);
            
            if (newMode.equals("giant")) applyGiantStats(p);
            else if (newMode.equals("tank")) applyTankStats(p);
            
            activeModes.put(p.getUniqueId(), newMode);
        }
    }

    private void applyGiantStats(Player p) {
        AttributeInstance scale = p.getAttribute(Attribute.GENERIC_SCALE);
        if (scale != null) {
            scale.addModifier(new AttributeModifier(UUID.randomUUID(), "bier.giant", 0.4, AttributeModifier.Operation.ADD_SCALAR));
        }
        AttributeInstance health = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (health != null) {
            health.addModifier(new AttributeModifier(UUID.randomUUID(), "bier.giant_hp", 10, AttributeModifier.Operation.ADD_NUMBER));
        }
        p.sendMessage(ChatColor.DARK_GREEN + "Set-Bonus aktiviert: Riese");
    }

    private void applyTankStats(Player p) {
        AttributeInstance speed = p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (speed != null) {
            speed.addModifier(new AttributeModifier(UUID.randomUUID(), "bier.tank_slow", -0.3, AttributeModifier.Operation.ADD_SCALAR));
        }
        AttributeInstance armor = p.getAttribute(Attribute.GENERIC_ARMOR);
        if (armor != null) {
            armor.addModifier(new AttributeModifier(UUID.randomUUID(), "bier.tank_armor", 10, AttributeModifier.Operation.ADD_NUMBER));
        }
        p.sendMessage(ChatColor.DARK_BLUE + "Set-Bonus aktiviert: Panzer");
    }

    private void resetAttributes(Player p) {
        AttributeInstance scale = p.getAttribute(Attribute.GENERIC_SCALE);
        if (scale != null) scale.setBaseValue(1.0);
    }

    private boolean isBeaconItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "beacon_armor"), PersistentDataType.BYTE);
    }

    private boolean hasEnchant(ItemStack item, String id) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "custom_enchant_" + id), PersistentDataType.INTEGER);
    }

    // NEUE METHODE: Prüft ob alle 4 Teile den Enchant haben
    private boolean hasFullSetEnchant(Player p, String id) {
        for (ItemStack item : p.getInventory().getArmorContents()) {
            if (!hasEnchant(item, id)) return false;
        }
        return true;
    }
}
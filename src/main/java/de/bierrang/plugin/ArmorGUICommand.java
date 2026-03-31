package de.bierrang.plugin;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArmorGUICommand implements CommandExecutor {

    private final BierRuestung plugin;

    public ArmorGUICommand(BierRuestung plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (!p.hasPermission("bierarmor.admin")) {
            sender.sendMessage(ChatColor.RED + "Keine Rechte.");
            return true;
        }

        plugin.getArmorGUI().open(p);
        return true;
    }
}

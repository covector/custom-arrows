package dev.covector.customarrows.item;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.entity.Player;

import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.ParamTester;

public class GiveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(args.length) {
            case 1:
                if (args[0].equals("list")) {
                    sender.sendMessage(ChatColor.GOLD + "Custom Arrows:");
                    for (int i = 0; i < ArrowRegistry.getArrowTypeCount(); i++) {
                        sender.sendMessage(ChatColor.GRAY + " - " + ChatColor.GOLD + ArrowRegistry.getArrowType(i).getName() + ChatColor.GRAY + " (ID " + i + ")");
                    }
                    return true;
                }
                break;
            case 2:
                if (args[0].equals("arrow")) {
                    int id;
                    try {
                        id = Integer.parseInt(args[1]);
                        if (id < 0 || id >= ArrowRegistry.getArrowTypeCount()) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid ID.");
                        return false;
                    }
                    ItemStack item = ItemManager.createCustomArrow(id);
                    ItemMeta itemMeta = item.getItemMeta();
                    // itemMeta.setDisplayName(ChatColor.GOLD + "Custom Arrow" + ChatColor.GRAY + " (ID " + id + ")");
                    itemMeta.setDisplayName(ChatColor.GOLD + ArrowRegistry.getArrowType(id).getName());
                    item.setItemMeta(itemMeta);
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GREEN + "Successfully gave you a custom arrow.");
                    }
                    return true;
                }
                if (args[0].equals("bow")) {
                    int slot;
                    try {
                        slot = Integer.parseInt(args[1]);
                        if (slot < 0) {
                            throw new NumberFormatException();
                        }
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid Slot Number.");
                        return false;
                    }
                    ItemStack item = ItemManager.createCustomBow(slot);
                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setUnbreakable(true);
                    itemMeta.setDisplayName(ChatColor.GOLD + "Custom Bow" + ChatColor.GRAY + " (Slot " + slot + ")");
                    item.setItemMeta(itemMeta);
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        player.getInventory().addItem(item);
                        player.sendMessage(ChatColor.GREEN + "Successfully gave you a custom bow.");
                    }
                    return true;
                }
                if (args[0].equals("bind")) {
                    if (sender instanceof Player) {
                        int slot;
                        try {
                            slot = Integer.parseInt(args[1]);
                            if (slot < 0) {
                                throw new NumberFormatException();
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Invalid Slot Number.");
                            return false;
                        }
                        Player player = (Player) sender;
                        ItemStack main = player.getInventory().getItemInMainHand();
                        ItemStack newBow = ItemManager.modifyCustomBowSlot(main, slot);
                        player.getInventory().setItemInMainHand(newBow);
                        player.sendMessage(ChatColor.GREEN + "Successfully bind to bow.");
                    }
                    return true;
                }
                break;
            case 3:
                if (args[0].equals("test")) {
                    ParamTester.set(args[1], Integer.parseInt(args[2]));
                    return true;
                }
                break;
        }

        return false;
    }
}
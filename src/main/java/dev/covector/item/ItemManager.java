package dev.covector.customarrows.item;

import org.bukkit.Bukkit;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;

public class ItemManager {
    // private static Material psuedoArrow = Material.EMERALD;
    private static Material psuedoArrow = Material.ECHO_SHARD;
    private static String customBowKey = "ca-bow";
    private static String customArrowKey = "ca-arrow";

    public static ItemStack createCustomBow(int slot) {
        ItemStack item = new ItemStack(Material.CROSSBOW);
        return modifyCustomBowSlot(item, slot);
    }

    public static ItemStack modifyCustomBowSlot(ItemStack item, int slot) {
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        tag.putInt(customBowKey, slot);
        stack.setTag(tag);

        return CraftItemStack.asBukkitCopy(stack);
    }

    public static ItemStack createCustomArrow(int id) {
        ItemStack item = new ItemStack(psuedoArrow);

        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        tag.putInt(customArrowKey, id);
        tag.putInt("CustomModelData", id);
        stack.setTag(tag);

        return CraftItemStack.asBukkitCopy(stack);
    }

    public static boolean isCustomBow(ItemStack item) {
        if (item == null || item.getType() != Material.CROSSBOW) return false;
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        return tag.contains(customBowKey);
    }

    public static int getCustomBowSlot(ItemStack item) {
        if (item == null || item.getType() != Material.CROSSBOW) return 0;
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        return tag.contains(customBowKey) ? tag.getInt(customBowKey) : 0;
    }

    public static boolean isCustomArrow(ItemStack item) {
        if (item == null || item.getType() != psuedoArrow) return false;
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        return tag.contains(customArrowKey);
    }

    public static int getCustomArrowId(ItemStack item) {
        if (item == null || item.getType() != psuedoArrow) return -1;
        net.minecraft.world.item.ItemStack stack = CraftItemStack.asNMSCopy(item);
        CompoundTag tag = stack.hasTag() ? stack.getTag() : new CompoundTag();
        return tag.contains(customArrowKey) ? tag.getInt(customArrowKey) : -1;
    }

    public static int getSlotFromInventory(PlayerInventory inv) {
        int main = getCustomBowSlot(inv.getItemInMainHand());
        if (main != 0) return main;
        int off = getCustomBowSlot(inv.getItemInOffHand());
        if (off != 0) return off;
        for (int i = 0; i < 35; i++) {
            ItemStack item = inv.getItem(i);
            int slotId = getCustomBowSlot(item);
            if (slotId != 0) return slotId;
        }
        return 0;
    }

    public static int[] getArrowsFromInventory(PlayerInventory inv, int slot) {
        if (slot == 0) return new int[0];
        ArrayList<Integer> arrows = new ArrayList<Integer>();
        int main = getCustomArrowId(inv.getItemInMainHand());
        if (main != -1 && arrows.size() < slot) arrows.add(main);
        int off = getCustomArrowId(inv.getItemInOffHand());
        if (off != -1 && arrows.size() < slot) arrows.add(off);
        int mainind = inv.getHeldItemSlot();
        for (int i = 0; i < 35; i++) {
            if (i == mainind) continue;
            ItemStack item = inv.getItem(i);
            int slotId = getCustomArrowId(item);
            if (slotId != -1 && arrows.size() < slot) arrows.add(slotId);
            if (arrows.size() == slot) break;
        }
        int[] result = new int[arrows.size()];
        for (int i = 0; i < arrows.size(); i++) {
            result[i] = arrows.get(i);
        }
        return result;
    }
}
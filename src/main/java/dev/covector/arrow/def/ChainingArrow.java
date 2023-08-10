package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.ArrayList;

import dev.covector.customarrows.CustomArrowsPlugin;

public class ChainingArrow extends CustomArrow {
    private static Color color = Color.fromRGB(245, 152, 66);
    private static String name = "Chaining Arrow";
    private int delay = 12;

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        arrow.remove();
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (!(entity instanceof LivingEntity) || entity instanceof Player) {
            return;
        }
        new BukkitRunnable() {
            public void run() {
                ItemStack crossBow = shooter.getInventory().getItemInMainHand();
                crossBow = crossBow.getType() == Material.CROSSBOW ? crossBow : shooter.getInventory().getItemInOffHand();
                if (crossBow.getType() == Material.CROSSBOW) {
                    CrossbowMeta bowMeta = (CrossbowMeta)crossBow.getItemMeta();
                    if (!bowMeta.hasChargedProjectiles()) {
                        bowMeta.setChargedProjectiles(Arrays.asList(new ItemStack(Material.ARROW, 1)));
                        crossBow.setItemMeta(bowMeta);
                    }
                }
            }
        }.runTaskLater(CustomArrowsPlugin.plugin, delay);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return -1;
    }

    
    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Charge your crossbow after hit");
        lore.add(ChatColor.GRAY + "Has 0.6s delay");
        return lore;
    }
}
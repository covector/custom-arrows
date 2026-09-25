package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.scheduler.BukkitRunnable;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.CustomArrow;

public class ChainingArrow extends CustomArrow {
    private static Color color = Color.fromRGB(245, 152, 66);
    private static String name = "Chaining Arrow";
    private int delay = 10;
    private HashSet<String> inUsePlayers = new HashSet<String>();

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        Entity entity = event.entity;
        LivingEntity shooter = event.shooter;

        if (!(entity instanceof LivingEntity) || entity instanceof Player) {
            return;
        }
        if (!(shooter instanceof Player)) {
            return;
        }
        Player player = (Player) shooter;
        if (inUsePlayers.contains(player.getUniqueId().toString())) { return; }
        inUsePlayers.add(player.getUniqueId().toString());
        new BukkitRunnable() {
            public void run() {
                ItemStack crossBow = player.getInventory().getItemInMainHand();
                crossBow = crossBow.getType() == Material.CROSSBOW ? crossBow : player.getInventory().getItemInOffHand();
                if (crossBow.getType() == Material.CROSSBOW) {
                    CrossbowMeta bowMeta = (CrossbowMeta)crossBow.getItemMeta();
                    if (!bowMeta.hasChargedProjectiles()) {
                        bowMeta.setChargedProjectiles(Arrays.asList(new ItemStack(Material.ARROW, 1)));
                        crossBow.setItemMeta(bowMeta);
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1);
                }
                inUsePlayers.remove(player.getUniqueId().toString());
            }
        }.runTaskLater(CustomArrowsPlugin.plugin, delay);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        return -1;
    }

    public boolean removeOnHitGround() {
        return true;
    }
    
    public String getName() {
        return name;
    }

    public boolean allowTrigger() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Charge your crossbow after hit");
        lore.add(ChatColor.GRAY + "Has " + String.valueOf(delay/20D) + "s delay");
        return lore;
    }
}
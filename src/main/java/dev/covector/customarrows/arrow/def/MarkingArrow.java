package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.CustomArrow;

public class MarkingArrow extends CustomArrow implements AutoCloseable, Listener {
    private static Color color = Color.fromRGB(255, 226, 79);
    private static String name = "Marking Arrow";
    private HashSet<String> marked = new HashSet<String>();
    private double damageMultiplier = 1.5D;
    private int duration;

    public MarkingArrow(int duration) {
        this.duration = duration;
        Bukkit.getPluginManager().registerEvents(this, CustomArrowsPlugin.plugin);
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        arrow.remove();
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;

        if (livingEntity instanceof Player) {
            return;
        }

        if (marked.contains(livingEntity.getUniqueId().toString())) {
            return;
        }
        
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));
        
        marked.add(livingEntity.getUniqueId().toString());
        new BukkitRunnable() {
            public void run() {
                marked.remove(livingEntity.getUniqueId().toString());
            }
        }.runTaskLater(CustomArrowsPlugin.plugin, duration * 20);
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
        lore.add(ChatColor.WHITE + "Mark enemy with glowing effect");
        lore.add(ChatColor.GRAY + "Marked enemy will take " + String.valueOf(damageMultiplier) + "x damage");
        return lore;
    }

    // private boolean cleanIfCan(LivingEntity entity) {
    //     return (entity.isDead() ||
    //         entity.getHealth() <= 0 || 
    //         !entity.hasPotionEffect(PotionEffectType.GLOWING));
    // }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        modifyDamage(event);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        modifyDamage(event);
    }

    private void modifyDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!(marked.contains(entity.getUniqueId().toString()))) {
            return;
        }

        event.setDamage(damageMultiplier * event.getDamage());
    }


    @Override
    public void close() {
        EntityDamageByEntityEvent.getHandlerList().unregister(this);
    }
}
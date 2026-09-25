package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.CustomArrow;

public class DamageOverTimeArrow extends CustomArrow {
    private static Color color = Color.BLACK;
    private static String name = "Wither Arrow";
    private HashMap<String, BukkitTask> runningMap = new HashMap<String, BukkitTask>();
    private int duration;
    private int period;
    private int damage;

    public DamageOverTimeArrow(int duration, int period, int damage) {
        this.duration = duration;
        this.period = period;
        this.damage = damage;
    }

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        Arrow arrow = event.arrow;
        Entity entity = event.entity;
        LivingEntity shooter = event.shooter;

        // cannot mark players
        if (!(entity instanceof LivingEntity) || entity instanceof Player) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;

        // give wither effect
        // livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, duration * 20, amplifier));

        // cancel if already running
        String uuid = livingEntity.getUniqueId().toString();
        if (runningMap.containsKey(uuid)) {
            runningMap.get(uuid).cancel();
            runningMap.remove(uuid);
        }

        // create new damage over time runnable
        BukkitTask task = new BukkitRunnable() {
            long ticksPassed = 0;

            public void run() {
                // on end
                if (ticksPassed > duration * 20) {
                    cancel();
                    runningMap.remove(uuid);
                    return;
                }

                // set piercedEntities
                // note that this arrow is shared across all pierced entities
                // so need to make sure it is reset before running the rest
                ArrowHelper.resetPiercedEntities(arrow);
                ArrowHelper.addPiercedEntities(arrow, livingEntity.getUniqueId());

                // get modified damage
                double baseDamage = damage;
                DamageEvent damageEvent = new DamageEvent(shooter, arrow, livingEntity, baseDamage);
                double finalDamage = ArrowHelper.calculateDamage(damageEvent, id);
                
                // set damaged by player
                if (shooter instanceof Player) {
                    livingEntity.damage(finalDamage, shooter);
                } else {
                    livingEntity.damage(finalDamage);
                }

                // call hit entity event
                EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, livingEntity, true);
                ArrowHelper.triggerOnHitEntity(entityHitEvent, id);

                // increase ticksPassed
                ticksPassed += period;
            }
        }.runTaskTimer(CustomArrowsPlugin.plugin, period, period);
        runningMap.put(uuid, task);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        return -1;
    }
    
    public String getName() {
        return name;
    }

    public boolean removeOnHitGround() {
        return true;
    }

    public boolean allowTrigger() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Give wither to target");
        lore.add(ChatColor.GRAY + String.valueOf(damage) + " damage every " + String.valueOf(period) + " ticks for " + String.valueOf(duration) + "s");
        lore.add(ChatColor.GRAY + "will trigger onHitEntities of other arrows");
        return lore;
    }
}
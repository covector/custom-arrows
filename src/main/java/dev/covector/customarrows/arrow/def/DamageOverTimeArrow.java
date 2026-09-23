package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

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
    private static String name = "Damage Over Time Arrow";
    private HashMap<String, BukkitTask> runningMap = new HashMap<String, BukkitTask>();
    private int duration;
    private int amplifier;

    public DamageOverTimeArrow(int duration, int amplifier) {
        this.duration = duration;
        this.amplifier = amplifier;
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
        long period = 20 / (amplifier + 1);
        BukkitTask task = new BukkitRunnable() {
            long runTime = 0;

            public void run() {
                // on end
                if (runTime > duration * 20) {
                    cancel();
                    runningMap.remove(uuid);
                }

                // set piercedEntities
                // note that this arrow is shared across all pierced entities
                // so need to make sure it is reset before running the rest
                ArrowHelper.resetPiercedEntities(arrow);
                ArrowHelper.addPiercedEntities(arrow, livingEntity.getUniqueId());

                // get modified damage
                double damage = 1;
                DamageEvent damageEvent = new DamageEvent(shooter, arrow, livingEntity, damage);
                damage = ArrowHelper.calculateDamage(damageEvent, id);
                
                // set damaged by player
                if (shooter instanceof Player) {
                    livingEntity.damage(damage, shooter);
                } else {
                    livingEntity.damage(damage);
                }

                // call hit entity event
                EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, livingEntity, true);
                ArrowHelper.triggerOnHitEntity(entityHitEvent, id);

                // increase runTime
                runTime += period;
            }
        }.runTaskTimer(CustomArrowsPlugin.plugin, 0, period);
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

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Give wither to target");
        lore.add(ChatColor.GRAY + "wither " + String.valueOf(amplifier+1) + " for " + String.valueOf(duration) + "s");
        return lore;
    }
}
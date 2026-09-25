package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.ArrowRegistry;
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

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        Entity entity = event.entity;

        // check if is living etity
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;

        // cannot mark players
        if (livingEntity instanceof Player) {
            return;
        }

        // cannot remark marked
        if (marked.contains(livingEntity.getUniqueId().toString())) {
            return;
        }
        
        // give glow effect
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration * 20, 0));
       
        // schedule unmark
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

    public double ModifyDamage(DamageEvent event) {
        return -1;
    }

    public boolean removeOnHitGround() {
        return true;
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

    // damage multiplier on marked
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        // check if damaged by arrow
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getDamager() instanceof Arrow) {
                return;
            }
        }

        // check if living entity is damaged
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();

        // check if marked
        if (!(marked.contains(entity.getUniqueId().toString()))) {
            return;
        }

        // damage multiplier 
        event.setDamage(damageMultiplier * event.getDamage());
    }

    // sure hit for marked
    @EventHandler (priority = EventPriority.HIGHEST)
    public void onArrowHit(ProjectileHitEvent event) {
        // check if is arrow
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        // check if shot by living entity
        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof LivingEntity)) {
            return;
        }
        LivingEntity shooter = (LivingEntity) arrow.getShooter();

        // if arrow only hit ground, transfer the hit to all marked entities
        int piercedEntities = ArrowHelper.getPiercedEntityIDs(arrow).length;
        if (piercedEntities == 0) {
            if (ArrowHelper.isCustomArrow(arrow)) {
                // loop through all custom arrows
                int[] ids = arrow.getPersistentDataContainer().get(CustomArrowsPlugin.plugin.arrowTypesKey, PersistentDataType.INTEGER_ARRAY);
                
                int markedCount = marked.size();
                int piercedCount = 0;
                for (String uuid: marked) {
                    piercedCount++;
                    Entity entity = Bukkit.getEntity(UUID.fromString(uuid));
                    if (!(entity instanceof LivingEntity) || entity instanceof Player) {
                        continue;
                    }
                    LivingEntity livingEntity = (LivingEntity) entity;

                    // add pierced entities 
                    ArrowHelper.addPiercedEntities(arrow, livingEntity.getUniqueId());

                    // get modified damage
                    double damage = arrow.getDamage() * 6.5D;
                    DamageEvent damageEvent = new DamageEvent(shooter, arrow, livingEntity, damage);
                    damage = ArrowHelper.calculateDamage(damageEvent, id);
                
                    // set damaged by player
                    if (shooter instanceof Player) {
                        livingEntity.damage(damage, shooter);
                    } else {
                        livingEntity.damage(damage);
                    }

                    // trigger onHitEntity
                    boolean arrowStopped = piercedCount == markedCount;
                    CustomArrow.EntityHitEvent entityHitEvent = new CustomArrow.EntityHitEvent(shooter, arrow, livingEntity, arrowStopped);
                    for (int id : ids) {
                        ArrowRegistry.getArrowType(id).onHitEntity(entityHitEvent);
                    }
                }
            }
        }
    }

    public boolean allowTrigger() {
        return true;
    }

    @Override
    public void close() {
        // EntityDamageByEntityEvent.getHandlerList().unregister(this);
        EntityDamageEvent.getHandlerList().unregister(this);
    }
}
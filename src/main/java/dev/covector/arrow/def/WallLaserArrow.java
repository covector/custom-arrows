package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Collection;
import java.util.jar.Attributes.Name;
import java.util.ArrayList;

import dev.covector.customarrows.CustomArrowsPlugin;

public class WallLaserArrow extends CustomArrow {
    private static Color color = Color.fromRGB(252, 25, 59);
    private static String name = "Wall Laser Arrow";
    private NamespacedKey key;
    private NamespacedKey deactivateKey;
    private int hitLimit = 5;
    private int maxBounce = 3;
    private int bounceTickDelay = 20;
    private NamespacedKey maxBounceKey;
    // private double damage = 2;

    public WallLaserArrow() {
        this.key = new NamespacedKey(CustomArrowsPlugin.plugin, "arrow-types");
        this.deactivateKey = new NamespacedKey(CustomArrowsPlugin.plugin, "deactivated-walllaser");
        this.maxBounceKey = new NamespacedKey(CustomArrowsPlugin.plugin, "max-bounce");
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        // bounce limit
        if (arrow.getPersistentDataContainer().has(maxBounceKey, PersistentDataType.INTEGER)) {
            int bounceLeft = arrow.getPersistentDataContainer().get(maxBounceKey, PersistentDataType.INTEGER);
            if (bounceLeft < 0) {
                arrow.remove();
                return;
            }
            arrow.getPersistentDataContainer().set(maxBounceKey, PersistentDataType.INTEGER, bounceLeft - 1);
        } else {
            arrow.getPersistentDataContainer().set(maxBounceKey, PersistentDataType.INTEGER, maxBounce);
        }
        
        if (arrow.getPersistentDataContainer().has(deactivateKey, PersistentDataType.BYTE)) {
            arrow.remove();
            return;
        }
        int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
        double damage = arrow.getDamage() * 25;
        Location hitEnd = null;

        // raycast entities from blockface
        Entity[] hitEntities = new Entity[hitLimit];
        for (int i = 0; i < hitLimit; i++) {
            RayTraceResult entityray = shooter.getWorld().rayTraceEntities(location, blockFace.getDirection(), 50, 0.5,
                e -> (e instanceof LivingEntity &&
                    !(e instanceof ArmorStand ||
                    e instanceof Player ||
                    isInArray(hitEntities, e) ||
                    e.getUniqueId().toString().equals(arrow.getUniqueId().toString())
                    ))
            );
            if (entityray == null) {
                break;
            }
            Entity entity = entityray.getHitEntity();
            hitEntities[i] = entity;
            hitEnd = entityray.getHitPosition().toLocation(shooter.getWorld());

            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.damage(damage, shooter);
            
            // call onHitEntity
            for (int id : ids) {
                if (ArrowRegistry.getArrowType(id) == this) { continue; }
                ArrowRegistry.getArrowType(id).onHitEntity(shooter, arrow, entity);
            }
        }
        for (Entity entity : hitEntities) {
            if (entity == null) {
                break;
            }
            for (int id : ids) {
                if (ArrowRegistry.getArrowType(id) instanceof PierceAwareArrow) {
                    PierceAwareArrow pierceAwareArrow = (PierceAwareArrow) ArrowRegistry.getArrowType(id);
                    pierceAwareArrow.onHitGround(shooter, arrow, location, blockFace);
                }
            }
        }

        // if no entity hit, raycast blocks from blockface
        if (hitEntities[0] == null) {
            RayTraceResult blockray = shooter.getWorld().rayTraceBlocks(location, blockFace.getDirection(), 50, FluidCollisionMode.NEVER, true);
            if (blockray != null) {
                Location hitLocation = blockray.getHitBlock().getLocation().add(0.5, 0.5, 0.5).add(blockray.getHitBlockFace().getDirection().multiply(.5));
                hitEnd = hitLocation;
                for (int id : ids) {
                    // if (ArrowRegistry.getArrowType(id) == this) { continue; }  // DO NOT COMMENT THIS OUT NO MATTER WHAT
                    if (ArrowRegistry.getArrowType(id) != this) {
                        ArrowRegistry.getArrowType(id).onHitGround(shooter, arrow, hitLocation, blockray.getHitBlockFace());
                    } else {
                        new BukkitRunnable() {
                            public void run() {
                                ArrowRegistry.getArrowType(id).onHitGround(shooter, arrow, hitLocation, blockray.getHitBlockFace());
                            }
                        }.runTaskLater(CustomArrowsPlugin.plugin, bounceTickDelay);
                    }
                }
            } else {
                arrow.remove();
            }
        } else {
            arrow.remove();
        }

        // spawn particles
        if (hitEnd != null) {
            double distance = location.distance(hitEnd);
            double step = 2 / distance;
            for (double i = 0; i <= 1; i += step) {
                Location loc = lerp3D(i, location, hitEnd);
                // loc.getWorld().spawnParticle(Particle.REDSTONE, loc, 1, new Particle.DustOptions(Color.FUCHSIA, 1));
                loc.getWorld().spawnParticle(org.bukkit.Particle.SONIC_BOOM, loc, 1);
                location.getWorld().playSound(location, Sound.ENTITY_WARDEN_SONIC_BOOM, 0.2F, 1F);
            }

            
        }

        // arrow.remove();
    }

    private boolean isInArray(Entity[] entities, Entity entity) {
        String uuid = entity.getUniqueId().toString();
        for (Entity e : entities) {
            if (e == null) {
                return false;
            }
            if (uuid.equals(e.getUniqueId().toString())) {
                return true;
            }
        }
        return false;
    }

    private Location lerp3D(double amount, Location loc1, Location loc2)
    {
        return new Location(loc1.getWorld(),
            lerp(amount, loc1.getX(), loc2.getX()),
            lerp(amount, loc1.getY(), loc2.getY()),
            lerp(amount, loc1.getZ(), loc2.getZ()))
        .setDirection(loc2.toVector().subtract(loc1.toVector()));
    }

    private double lerp(double amount, double start, double end)
    {
        return (start + amount * (end - start));
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (entity instanceof LivingEntity) {
            arrow.getPersistentDataContainer().set(deactivateKey, PersistentDataType.BYTE, (byte) 1);
        }
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
        lore.add(ChatColor.WHITE + "Shoots a laser from the surface of block hit");
        lore.add(ChatColor.GRAY + "will trigger on hit entities of other arrows");
        lore.add(ChatColor.GRAY + "will trigger on hit ground of other arrows if no entity hit");
        return lore;
    }
}
package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.CustomArrow;

public class WallLaserArrow extends CustomArrow {
    private static Color color = Color.fromRGB(252, 25, 59);
    private static String name = "Wall Laser Arrow";
    private NamespacedKey deactivateKey;
    private int hitLimit = 5;
    private int maxBounce = 3;
    private int bounceTickDelay = 20;
    private NamespacedKey maxBounceKey;
    // private double damage = 2;

    public WallLaserArrow() {
        this.deactivateKey = new NamespacedKey(CustomArrowsPlugin.plugin, "deactivated-walllaser");
        this.maxBounceKey = new NamespacedKey(CustomArrowsPlugin.plugin, "max-bounce");
    }

    public void onHitGround(GroundHitEvent event) {
        Location location = event.location;
        Arrow arrow = event.arrow;
        LivingEntity shooter = event.shooter;
        BlockFace blockFace = event.blockFace;

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
        double damage = arrow.getDamage() * 30;
        Location hitEnd = null;

        // raycast entities from blockface
        List<Entity> hitEntities = new ArrayList<Entity>();
        for (int i = 0; i < hitLimit; i++) {
            RayTraceResult entityray = shooter.getWorld().rayTraceEntities(location, blockFace.getDirection(), 50, 0.75,
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
            hitEntities.add(entity);
            hitEnd = entityray.getHitPosition().toLocation(shooter.getWorld());
        }

        // hit every entities
        for (int i = 0; i < hitEntities.size(); i++) {
            LivingEntity livingEntity = (LivingEntity) hitEntities.get(0);
            
            if (shooter instanceof Player) {
                livingEntity.damage(damage, shooter);
            } else {
                livingEntity.damage(damage);
            }
            
            // add into piercedEntities
            ArrowHelper.addPiercedEntities(arrow, livingEntity.getUniqueId());

            // call hit entity event
            boolean arrowStopped = i == hitEntities.size();
            EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, livingEntity, arrowStopped);
            ArrowHelper.triggerOnHitEntity(entityHitEvent, id);

        }

        // if no entity hit, raycast blocks from blockface
        if (hitEntities.size() == 0) {
            RayTraceResult blockray = shooter.getWorld().rayTraceBlocks(location, blockFace.getDirection(), 50, FluidCollisionMode.NEVER, true);
            if (blockray != null) {
                Location hitLocation = blockray.getHitBlock().getLocation().add(0.5, 0.5, 0.5).add(blockray.getHitBlockFace().getDirection().multiply(.5));
                hitEnd = hitLocation;
                int[] ids = ArrowHelper.getCustomArrowIDs(arrow);
                GroundHitEvent groundHitEvent = new GroundHitEvent(shooter, arrow, hitLocation, blockray.getHitBlockFace(), new UUID[0]);
                for (int id : ids) {
                    // if (ArrowRegistry.getArrowType(id) == this) { continue; }  // DO NOT COMMENT THIS OUT NO MATTER WHAT
                    if (ArrowRegistry.getArrowType(id) != this) {
                        ArrowRegistry.getArrowType(id).onHitGround(groundHitEvent);
                    } else {
                        new BukkitRunnable() {
                            public void run() {
                                ArrowRegistry.getArrowType(id).onHitGround(groundHitEvent);
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

    private boolean isInArray(List<Entity> entities, Entity entity) {
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

    public void onHitEntity(EntityHitEvent event) {
        Entity entity = event.entity;
        Arrow arrow = event.arrow;
        if (entity instanceof LivingEntity) {
            arrow.getPersistentDataContainer().set(deactivateKey, PersistentDataType.BYTE, (byte) 1);
        }
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

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Shoots a laser from the surface of block hit");
        lore.add(ChatColor.GRAY + "will trigger on hit entities of other arrows");
        lore.add(ChatColor.GRAY + "will trigger on hit ground of other arrows if no entity hit");
        return lore;
    }
}
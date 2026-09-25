package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.CustomArrow;

public class DamageNearestArrow extends CustomArrow {
    private static Color color = Color.fromRGB(227, 227, 227);
    private static String name = "Damage Nearest Arrow";
    private double radius = 3.5;

    public void onHitGround(GroundHitEvent event) {
        Location location = event.location;
        Arrow arrow = event.arrow;
        LivingEntity shooter = event.shooter;

        // get hit entities
        UUID[] uuids = ArrowHelper.getPiercedEntityIDs(arrow);
        HashSet<String> hitEntities = new HashSet<String>();
        for (UUID uuid : uuids) {
            hitEntities.add(uuid.toString());
        }

        // sort nearest entities
        List<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius).stream()
            .filter(entity -> 
                !hitEntities.contains(entity.getUniqueId().toString()) &&
                entity instanceof LivingEntity &&
                !(entity instanceof Player) &&
                !(entity instanceof ArmorStand)
            ).collect(Collectors.toList()); // filter out hit entities
        EntityDistance[] entityDistances = new EntityDistance[entities.size()];
        int j = 0;
        for (Entity entity : entities) {
            entityDistances[j] = new EntityDistance(entity, location);
            j++;
        }
        Arrays.sort(entityDistances, (a, b) -> {
            return Double.compare(a.getDistance(), b.getDistance());
        });

        // can pierce (pierce level + 1) mobs
        int canPierce = arrow.getPierceLevel() + 1;
        for (int i = 0; i < entityDistances.length && canPierce > 0; i++) {
            Entity entity = entityDistances[i].entity;

            LivingEntity livingEntity = (LivingEntity) entity;
            
            // add into piercedEntities
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

            // call hit entity event
            boolean arrowStopped = i >= entityDistances.length - 1 || canPierce <= 1;
            EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, livingEntity, arrowStopped);
            ArrowHelper.triggerOnHitEntity(entityHitEvent, id);

            canPierce--;
        }

        // hardcode trigger swap
        if (entityDistances.length == 0 && ArrowHelper.hasArrowID(event.arrow, 0)) {
            EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, null, true);
            ArrowRegistry.getArrowType(0).onHitEntity(entityHitEvent);
        }
    }

    class EntityDistance {
        Entity entity;
        double distance;

        public EntityDistance(Entity entity, Location location) {
            this.entity = entity;
            this.distance = entity.getLocation().distanceSquared(location);
        }

        public double getDistance() {
            return distance;
        }
    }

    public void onHitEntity(EntityHitEvent event) {
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
        lore.add(ChatColor.WHITE + "Damage nearest entity within " + String.valueOf(radius) + " radius");
        lore.add(ChatColor.WHITE + "when hit ground");
        lore.add(ChatColor.GRAY + "Compatible with piercing level");
        lore.add(ChatColor.GRAY + "Will trigger onHitEntities of other arrows");
        return lore;
    }
}
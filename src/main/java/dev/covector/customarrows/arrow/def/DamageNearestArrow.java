package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
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

        // sort nearest entities
        Collection<Entity> entities = location.getWorld().getNearbyEntities(location, radius, radius, radius);
        EntityDistance[] entityDistances = new EntityDistance[entities.size()];
        int j = 0;
        for (Entity entity : entities) {
            entityDistances[j] = new EntityDistance(entity, arrow);
            j++;
        }
        Arrays.sort(entityDistances, (a, b) -> {
            return Double.compare(a.getDistance(), b.getDistance());
        });

        // convert remaining piercing into damage nearest
        int canPierce = arrow.getPierceLevel() + 1;
        for (int i = 0; i < entityDistances.length && canPierce > 0; i++) {
            Entity entity = entityDistances[i].entity;
            if (!(entity instanceof LivingEntity) || entity instanceof Player) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

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

            // add into piercedEntities
            ArrowHelper.addPiercedEntities(arrow, livingEntity.getUniqueId());

            // call hit entity event
            boolean arrowStopped = i >= entityDistances.length || canPierce <= 1;
            EntityHitEvent entityHitEvent = new EntityHitEvent(shooter, arrow, livingEntity, arrowStopped);
            ArrowHelper.triggerOnHitEntity(entityHitEvent, id);

            canPierce--;
        }

        arrow.remove();
    }

    class EntityDistance {
        Entity entity;
        double distance;

        public EntityDistance(Entity entity, Arrow arrow) {
            this.entity = entity;
            this.distance = entity.getLocation().distanceSquared(arrow.getLocation());
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

    
    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Damage nearest entity within " + String.valueOf(radius) + " radius");
        lore.add(ChatColor.GRAY + "when hit ground");
        lore.add(ChatColor.GRAY + "compatible with piercing level");
        lore.add(ChatColor.GRAY + "will trigger on hit entities of other arrows");
        return lore;
    }
}
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
import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.CustomArrow;

public class DamageNearestArrow extends CustomArrow {
    private static Color color = Color.fromRGB(227, 227, 227);
    private static String name = "Damage Nearest Arrow";
    private NamespacedKey key;
    private double radius = 3.5;

    public DamageNearestArrow() {
        this.key = new NamespacedKey(CustomArrowsPlugin.plugin, "arrow-types");
    }

    public void onHitGround(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace) {
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

        int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
        int canPiece = arrow.getPierceLevel() + 1;
        for (int i = 0; i < entityDistances.length && canPiece > 0; i++) {
            Entity entity = entityDistances[i].entity;
            if (!(entity instanceof LivingEntity) || entity instanceof Player) {
                continue;
            }

            LivingEntity livingEntity = (LivingEntity) entity;

            // get modified damage
            double damage = arrow.getDamage() * 6.5D;
            for (int id : ids) {
                if (ArrowRegistry.getArrowType(id) == this) { continue; }
                double modDamage = ArrowRegistry.getArrowType(id).ModifyDamage(shooter, arrow, livingEntity, damage);
                if (modDamage != -1) {
                    damage = modDamage;
                    break;
                }
            }
            
            if (shooter instanceof Player) {
                livingEntity.damage(damage, shooter);
            } else {
                livingEntity.damage(damage);
            }

            // call hit entity event
            for (int id : ids) {
                if (ArrowRegistry.getArrowType(id) == this) { continue; }
                ArrowRegistry.getArrowType(id).onHitEntity(shooter, arrow, entity);
            }
            canPiece--;
        }
        for (int id : ids) {
            if (ArrowRegistry.getArrowType(id) instanceof PierceAwareArrow) {
                PierceAwareArrow pierceAwareArrow = (PierceAwareArrow) ArrowRegistry.getArrowType(id);
                pierceAwareArrow.onHitGround(shooter, arrow, location, blockFace);
            }
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

    public void onHitEntity(LivingEntity shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(LivingEntity shooter, Arrow arrow, LivingEntity entity, double damage) {
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
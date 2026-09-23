package dev.covector.customarrows.arrow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;

public class ArrowHelper {
    public static void setCustomArrowIDs(Arrow arrow, int[] arrowIDs) {
        arrow.getPersistentDataContainer().set(CustomArrowsPlugin.plugin.arrowTypesKey, PersistentDataType.INTEGER_ARRAY, arrowIDs);
    }

    public static boolean isCustomArrow(Arrow arrow) {
        return arrow.getPersistentDataContainer().has(CustomArrowsPlugin.plugin.arrowTypesKey, PersistentDataType.INTEGER_ARRAY);
    }

    public static int[] getCustomArrowIDs(Arrow arrow) {
        return arrow.getPersistentDataContainer().get(CustomArrowsPlugin.plugin.arrowTypesKey, PersistentDataType.INTEGER_ARRAY);
    }

    public static void triggerOnHitEntity(CustomArrow.EntityHitEvent event, int exceptID) {
        int[] ids = getCustomArrowIDs(event.arrow);
        for (int id : ids) {
            if (id == exceptID) { continue; }
            ArrowRegistry.getArrowType(id).onHitEntity(event);
        }
    }

    public static void triggerOnHitGround(CustomArrow.GroundHitEvent event, int exceptID) {
        int[] ids = getCustomArrowIDs(event.arrow);
        for (int id : ids) {
            if (id == exceptID) { continue; }
            ArrowRegistry.getArrowType(id).onHitGround(event);
        }
    }

    public static double calculateDamage(CustomArrow.DamageEvent event, int exceptID) {
        int[] ids = getCustomArrowIDs(event.arrow);
        for (int id : ids) {
            if (id == exceptID) { continue; }
                double modDamage = ArrowRegistry.getArrowType(id).ModifyDamage(event);
                if (modDamage != -1) {
                    return modDamage;
            }
        }
        return event.damage;
    }

    public static UUID[] getPiercedEntityIDs(Arrow arrow) {
        if (!arrow.getPersistentDataContainer().has(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE)) {
            return new UUID[0];
        }
        return arrow.getPersistentDataContainer().get(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE);
    }

    public static List<Entity> getPiercedEntities(Arrow arrow) {
        List<Entity> entities = new ArrayList<>();
    
        UUID[] uuids = getPiercedEntityIDs(arrow);
        for (UUID uuid : uuids) {
            if (uuid == null) continue;
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entities.add(entity);
            }
        }
    
        return entities;
    }

    public static void addPiercedEntities(Arrow arrow, UUID entity) {
        // init
        if (!arrow.getPersistentDataContainer().has(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE)) {
            arrow.getPersistentDataContainer().set(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE, new UUID[0]);
        }

        // get old
        UUID[] uuids = getPiercedEntityIDs(arrow);

        // append new
        UUID[] newUuids = Arrays.copyOf(uuids, uuids.length + 1);
        newUuids[newUuids.length - 1] = entity;
        arrow.getPersistentDataContainer().set(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE, newUuids);
    }

    public static void resetPiercedEntities(Arrow arrow) {
        arrow.getPersistentDataContainer().set(CustomArrowsPlugin.plugin.piercedEntitiesKey, UUIDArrayDataType.INSTANCE, new UUID[0]);
    }

    public static boolean needsRemove(Arrow arrow) {
        int[] ids = getCustomArrowIDs(arrow);
        for (int id : ids) {
            if (!ArrowRegistry.getArrowType(id).removeOnHitGround()) {
                // don't remove if any of the arrow doesn't want
                return false;
            }
        }
        return true;
    }
}

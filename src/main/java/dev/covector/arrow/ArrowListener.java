package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.entity.AbstractArrow.PickupStatus;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.item.ItemManager;

public class ArrowListener implements Listener {
    private NamespacedKey key;

    public ArrowListener(NamespacedKey key) {
        this.key = key;
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) arrow.getShooter();

        int[] arrowIds = ItemManager.getArrowsFromInventory(player.getInventory(), ItemManager.getSlotFromInventory(player.getInventory()));
        if (arrowIds.length == 0) {
            return;
        }
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        arrow.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, arrowIds);
        arrow.setColor(ArrowRegistry.getArrowType(arrowIds[0]).getColor());
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        if (arrow.getPersistentDataContainer().has(key, PersistentDataType.INTEGER_ARRAY)) {
            int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
            Player player = (Player) arrow.getShooter();
            for (int id : ids) {
                // Bukkit.broadcastMessage("Arrow has id " + id);
                if (event.getHitEntity() != null) {
                    ArrowRegistry.getArrowType(id).onHitEntity(player, arrow, event.getHitEntity());
                } else if (event.getHitBlock() != null) {
                    ArrowRegistry.getArrowType(id).onHitGround(player, arrow, event.getHitBlock().getLocation().add(0.5, 0.5, 0.5).add(event.getHitBlockFace().getDirection().multiply(.5)), event.getHitBlockFace());
                    // ArrowRegistry.getArrowType(id).onHitGround(player, arrow, arrow.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        if(event.getCause() != DamageCause.PROJECTILE){
            return;
        }

        if(!(event.getDamager() instanceof Arrow)){
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();
        if(!(arrow.getShooter() instanceof Player)){
            return;
        }

        Player player = (Player) arrow.getShooter();
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        LivingEntity entity = (LivingEntity) event.getEntity();
        if (arrow.getPersistentDataContainer().has(key, PersistentDataType.INTEGER_ARRAY)) {
            int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
            for (int id : ids) {
                double damage = ArrowRegistry.getArrowType(id).ModifyDamage(player, arrow, entity, event.getDamage());
                if (damage < 0) {
                    continue;
                }
                event.setDamage(damage);
                break;
            }
        }
    }
}
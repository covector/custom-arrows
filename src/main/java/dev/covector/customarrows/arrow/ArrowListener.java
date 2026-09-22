package dev.covector.customarrows.arrow;

import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.item.ItemManager;

public class ArrowListener implements Listener {
    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        // check if is arrow
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        // check if shot by player
        Arrow arrow = (Arrow) event.getEntity();
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }

        // get shooter
        Player player = (Player) arrow.getShooter();

        // get custom arrows
        int[] arrowIds = ItemManager.getArrowsFromInventory(player.getInventory(), ItemManager.getSlotFromInventory(player.getInventory()));
        if (arrowIds.length == 0) {
            return;
        }

        // set unpickupable
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        // set color
        arrow.setColor(ArrowRegistry.getArrowType(arrowIds[0]).getColor());
        // store custom arrow ids into arrow entity
        ArrowHelper.setCustomArrowIDs(arrow, arrowIds);
    }

    @EventHandler
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

        if (ArrowHelper.isCustomArrow(arrow)) {
            // loop through all custom arrows
            int[] ids = arrow.getPersistentDataContainer().get(CustomArrowsPlugin.plugin.arrowTypesKey, PersistentDataType.INTEGER_ARRAY);
            
            if (event.getHitEntity() != null) {
                // add pierced entities 
                Entity entity = event.getHitEntity();
                ArrowHelper.addPiercedEntities(arrow, entity.getUniqueId());

                 // trigger onHitEntity
                boolean arrowStopped = arrow.getPierceLevel() == 0;
                CustomArrow.EntityHitEvent entityHitEvent = new CustomArrow.EntityHitEvent(shooter, arrow, entity, arrowStopped);
                for (int id : ids) {
                    ArrowRegistry.getArrowType(id).onHitEntity(entityHitEvent);
                }
            } else if (event.getHitBlock() != null) {
                // trigger onHitGround
                Location blockCenter = event.getHitBlock().getLocation().add(0.5, 0.5, 0.5);
                BlockFace blockFace = event.getHitBlockFace();
                UUID[] piercedEntities = ArrowHelper.getPiercedEntityIDs(arrow);
                CustomArrow.GroundHitEvent groundHitEvent = new CustomArrow.GroundHitEvent(shooter, arrow, blockCenter, blockFace, piercedEntities);
                for (int id : ids) {
                    ArrowRegistry.getArrowType(id).onHitGround(groundHitEvent);
                }
            }
        }
    }

    @EventHandler
    public void onArrowDamage(EntityDamageByEntityEvent event) {
        // caused by projectiles
        if(event.getCause() != DamageCause.PROJECTILE){
            return;
        }

        // caused by arrow
        if(!(event.getDamager() instanceof Arrow)){
            return;
        }

        // living entity is shot
        Arrow arrow = (Arrow) event.getDamager();
        if(!(arrow.getShooter() instanceof LivingEntity)){
            return;
        }

        // shot by living entity
        LivingEntity shooter = (LivingEntity) arrow.getShooter();
        if (!(event.getEntity() instanceof LivingEntity)) {
            return;
        }

        // loop through to find first custom arrow that 
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (ArrowHelper.isCustomArrow(arrow)) {
            int[] ids = ArrowHelper.getCustomArrowIDs(arrow);
            for (int id : ids) {
                // craft damage event
                CustomArrow.DamageEvent damageEvent = new CustomArrow.DamageEvent(shooter, arrow, entity, event.getDamage());
                double damage = ArrowRegistry.getArrowType(id).ModifyDamage(damageEvent);
                // ignore if damage is -1
                if (damage < 0) {
                    continue;
                }
                // else set arrow final damage
                event.setDamage(damage);
                break;
            }
        }
    }

    private HashSet<String> leftClickToggled = new HashSet<String>();
    public boolean toggleLeftClick(Player player, boolean toggle) {
        if (toggle) {
            leftClickToggled.add(player.getUniqueId().toString());
        } else {
            leftClickToggled.remove(player.getUniqueId().toString());
        }
        return toggle;
    }
    public boolean toggleLeftClick(Player player) {
        return toggleLeftClick(player, !isLeftClickToggled(player));
    }

    private boolean isLeftClickToggled(Player player) {
        return leftClickToggled.contains(player.getUniqueId().toString());
    }

    @EventHandler
    public void onArrowLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // if ((event.getAction() != Action.RIGHT_CLICK_AIR) || !player.isSneaking()) {
        //     return;
        // }

        if ((event.getAction() != Action.LEFT_CLICK_AIR)) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!isLeftClickToggled(player)) {
            return;
        }

        ItemStack itemMain = player.getInventory().getItemInMainHand();

        if (!ItemManager.isCustomArrow(itemMain)) {
            return;
        }

        PlayerInventory inv = player.getInventory();
        int prevInd = inv.getHeldItemSlot();
        for (int i = prevInd - 1; i >= 0; i--) {
            if (ItemManager.isCustomArrow(inv.getItem(i))) {
                player.getInventory().setItem(prevInd, player.getInventory().getItem(i));
                prevInd = i;
            }
            
        }
        if (prevInd != inv.getHeldItemSlot()) {
            player.getInventory().setItem(prevInd, itemMain);
        }
    }
}
package dev.covector.customarrows.arrow;

import java.util.HashSet;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
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
        for (int i = inv.getHeldItemSlot(); i > 0; i--) {
            player.getInventory().setItem(i, player.getInventory().getItem(i-1));
        }
        player.getInventory().setItem(0, itemMain);

    }
}
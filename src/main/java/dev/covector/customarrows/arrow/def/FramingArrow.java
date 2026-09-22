package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.CustomArrow;

public class FramingArrow extends CustomArrow implements Listener {
    private static Color color = Color.fromRGB(58, 202, 207);
    private static String name = "Framing Arrow";
    private int hitLimit = 8;
    private NamespacedKey maxDepthKey;
    private NamespacedKey playerInitArrowKey;

    public FramingArrow() {
        this.maxDepthKey = new NamespacedKey(CustomArrowsPlugin.plugin, "max-depth");
        this.playerInitArrowKey = new NamespacedKey(CustomArrowsPlugin.plugin, "player-init-arrow");
    }

    public void onHitGround(GroundHitEvent event) {
        event.arrow.remove();
    }

    public void onHitEntity(EntityHitEvent event) {
        Entity entity = event.entity;
        LivingEntity shooter = event.shooter;
        Arrow arrow = event.arrow;

        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;

        // set target
        if (!(shooter instanceof Player) && (entity instanceof Mob)) {
            ((Mob) entity).setTarget(shooter);
        }

        // hit limit
        int hitLeft = hitLimit;
        if (arrow.getPersistentDataContainer().has(maxDepthKey, PersistentDataType.INTEGER)) {
            hitLeft = arrow.getPersistentDataContainer().get(maxDepthKey, PersistentDataType.INTEGER);
            if (hitLeft < 1) {
                return;
            }
        }

        // copy arrow attributes
        Arrow newArrow = livingEntity.launchProjectile(Arrow.class, livingEntity.getLocation().getDirection());
        newArrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        newArrow.setDamage(arrow.getDamage());
        newArrow.setKnockbackStrength(arrow.getKnockbackStrength());
        newArrow.setFireTicks(arrow.getFireTicks());
        newArrow.setCritical(arrow.isCritical());
        newArrow.setPierceLevel(arrow.getPierceLevel());
        newArrow.getPersistentDataContainer().set(maxDepthKey, PersistentDataType.INTEGER, hitLeft - 1);
        newArrow.getPersistentDataContainer().set(playerInitArrowKey, PersistentDataType.BYTE, (byte) 0);

        // inject abilities
        int[] ids = ArrowHelper.getCustomArrowIDs(arrow);
        ArrowHelper.setCustomArrowIDs(newArrow, ids);
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
        lore.add(ChatColor.WHITE + "Shoot an arrow out from hit entity");
        lore.add(ChatColor.GRAY + "at the direction they are facing");
        return lore;
    }
}
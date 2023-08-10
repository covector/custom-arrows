package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.BlockFace;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

import dev.covector.customarrows.CustomArrowsPlugin;

public class FramingArrow extends CustomArrow implements Listener {
    private static Color color = Color.fromRGB(58, 202, 207);
    private static String name = "Framing Arrow";
    private NamespacedKey key;

    public FramingArrow() {
        this.key = new NamespacedKey(CustomArrowsPlugin.plugin, "arrow-types");
        // Bukkit.getPluginManager().registerEvents(this, CustomArrowsPlugin.plugin);
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        onHitGround(arrow);
    }

    public void onHitGround(Arrow arrow) {
        arrow.remove();
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        onHitEntity(arrow, entity);
    }

    public void onHitEntity(Arrow arrow, Entity entity) {
        // if (!(entity instanceof LivingEntity) || entity instanceof Player) {
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        LivingEntity livingEntity = (LivingEntity) entity;
        Arrow newArrow = livingEntity.launchProjectile(Arrow.class, livingEntity.getLocation().getDirection());

        // inject abilities
        // int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
        // newArrow.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, ids);
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
        lore.add(ChatColor.WHITE + "Shoot an arrow out from hit entity");
        lore.add(ChatColor.GRAY + "at the direction they are facing");
        return lore;
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW) {
            return;
        }

        Arrow arrow = (Arrow) event.getEntity();
        if (arrow.getShooter() instanceof Player) {
            return;
        }

        if (arrow.getPersistentDataContainer().has(key, PersistentDataType.INTEGER_ARRAY)) {
            int[] ids = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER_ARRAY);
            for (int id : ids) {
                if (ArrowRegistry.getArrowType(id) instanceof FramingArrow) {
                    FramingArrow framingArrow = (FramingArrow) ArrowRegistry.getArrowType(id);
                    if (event.getHitEntity() != null) {
                        framingArrow.onHitEntity(arrow, event.getHitEntity());
                    } else if (event.getHitBlock() != null) {
                        framingArrow.onHitGround(arrow);
                    }
                }
            }
        }
    }
}
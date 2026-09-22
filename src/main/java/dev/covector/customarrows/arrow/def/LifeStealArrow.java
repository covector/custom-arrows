package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.CustomArrow;

public class LifeStealArrow extends CustomArrow {
    private static Color color = Color.fromRGB(240, 55, 77);
    private static String name = "Life Steal Arrow";

    public void onHitGround(GroundHitEvent event) {
        event.arrow.remove();
    }

    public void onHitEntity(EntityHitEvent event) {
        LivingEntity shooter = event.shooter;
        Arrow arrow = event.arrow;
        int entityPierced = ArrowHelper.getPiercedEntityIDs(arrow).length;
        shooter.setHealth(Math.min(shooter.getHealth() + Math.pow(2, entityPierced - 1), shooter.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()));
        shooter.getWorld().spawnParticle(org.bukkit.Particle.HEART, shooter.getLocation(), 1);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
    }

    public void onAfterHitAll(LivingEntity shooter, Arrow arrow, Entity[] entities) {
        if (entities.length > 1) {
            double healAmount = entities.length * entities.length  * entities.length / 2D - entities.length;
            shooter.setHealth(Math.min(shooter.getHealth() + healAmount, shooter.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()));
            shooter.getWorld().spawnParticle(org.bukkit.Particle.HEART, shooter.getLocation(), entities.length);
            shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, entities.length);
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
        lore.add(ChatColor.WHITE + "Heal 1 health for first pierced");
        lore.add(ChatColor.WHITE + "Increase exponentially after every piercing");
        lore.add(ChatColor.WHITE + "(i.e. 2nd pierced: heal 2, 3rd pierced: heal 4, ...)");
        return lore;
    }
}
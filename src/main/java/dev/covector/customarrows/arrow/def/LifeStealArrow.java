package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LifeStealArrow extends PierceAwareArrow {
    private static Color color = Color.fromRGB(240, 55, 77);
    private static String name = "Life Steal Arrow";

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) { 
        shooter.setHealth(Math.min(shooter.getHealth() + 1, shooter.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()));
        super.onHitEntity(shooter, arrow, entity);
        shooter.getWorld().spawnParticle(org.bukkit.Particle.HEART, shooter.getLocation(), 1);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
    }

    public void onAfterHitAll(Player shooter, Arrow arrow, Entity[] entities) {
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

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return -1;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Heal self for 1 when hit 1 enemy");
        lore.add(ChatColor.WHITE + "Heal self for (hit entity count)^3/2 otherwise");
        return lore;
    }
}
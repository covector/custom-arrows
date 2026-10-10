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
    private double maxAbsorption = 20D;

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        LivingEntity shooter = event.shooter;
        if (!shooter.isValid() || shooter.isDead()) { return; }
        Arrow arrow = event.arrow;
        int entityPierced = ArrowHelper.getPiercedEntityIDs(arrow).length;
        heal(shooter, Math.pow(2, entityPierced - 1));
        shooter.getWorld().spawnParticle(org.bukkit.Particle.HEART, shooter.getLocation(), 1);
        shooter.getWorld().playSound(shooter.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
    }

    protected void heal(LivingEntity shooter, double healAmount) {
        double maxHealth = shooter.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue();
        double actualHealAmount = Math.min(healAmount, maxHealth - shooter.getHealth());

        // heal
        shooter.setHealth(Math.min(shooter.getHealth() + actualHealAmount, maxHealth));

        // set absorption for the rest
        double addAbsorptionAmount = healAmount - actualHealAmount;
        if (addAbsorptionAmount > 0) {
            shooter.setAbsorptionAmount(Math.min(shooter.getAbsorptionAmount() + addAbsorptionAmount, maxAbsorption));
        }
    }
        

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        return -1;
    }

    public boolean removeOnHitGround() {
        return true;
    }

    public String getName() {
        return name;
    }

    public boolean allowTrigger() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Heal 1 health for first pierced");
        lore.add(ChatColor.WHITE + "Increase exponentially after every piercing");
        lore.add(ChatColor.GRAY + "(i.e. 2nd pierced: heal 2, 3rd pierced: heal 4, ...)");
        lore.add(ChatColor.GRAY + "Can stack as absorption up to " + String.valueOf(maxAbsorption));
        return lore;
    }
}
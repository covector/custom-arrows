package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.BlockFace;

public class PercentDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private static String name = "Percent Damage Arrow";
    private double percent;

    public PercentDamageArrow(double percent) {
        this.percent = percent;
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        arrow.remove();
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return Math.min(percent * entity.getHealth(), damage * 2.5);
    }
    
    public String getName() {
        return name;
    }
}
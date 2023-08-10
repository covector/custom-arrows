package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.BlockFace;
import org.bukkit.ChatColor;

import java.util.ArrayList;

public class TrueDamageArrow extends CustomArrow {
    private static Color color = Color.fromRGB(74, 10, 5);
    private String name = "True Damage Arrow";
    private double damage = 12;

    public void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace) {
        arrow.remove();
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        if (entity instanceof Player) {
            return 0;
        }
        if (entity.getHealth() - damage <= 0) {
            // entity.setHealth(0);
            return 999;
        }
        entity.setHealth(entity.getHealth() - damage);
        return 0;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Deals " + String.valueOf(damage) + " true damage");
        return lore;
    }
}
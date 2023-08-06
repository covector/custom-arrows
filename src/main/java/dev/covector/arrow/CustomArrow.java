package dev.covector.customarrows.arrow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.block.BlockFace;

public abstract class CustomArrow {
    public abstract void onHitGround(Player shooter, Arrow arrow, Location location, BlockFace blockFace);
    public abstract void onHitEntity(Player shooter, Arrow arrow, Entity entity);
    public abstract double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage);
    public abstract Color getColor();
    public abstract String getName();
}
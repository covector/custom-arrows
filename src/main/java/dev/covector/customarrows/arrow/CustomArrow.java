package dev.covector.customarrows.arrow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public abstract class CustomArrow {
    public abstract void onHitGround(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace);
    public abstract void onHitEntity(LivingEntity shooter, Arrow arrow, Entity entity);
    public abstract double ModifyDamage(LivingEntity shooter, Arrow arrow, LivingEntity entity, double damage);
    public abstract Color getColor();
    public abstract String getName();
    public abstract ArrayList<String> getLore();
}
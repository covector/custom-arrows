package dev.covector.customarrows.arrow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public abstract class CustomArrow {
    public int id = -1;
    public abstract void onHitGround(GroundHitEvent event);
    public abstract void onHitEntity(EntityHitEvent event);
    public abstract double ModifyDamage(DamageEvent event);
    public abstract Color getColor();
    public abstract String getName();
    public abstract ArrayList<String> getLore();
    public abstract boolean removeOnHitGround();
    public abstract boolean allowTrigger();

    public static class GroundHitEvent {
        public LivingEntity shooter;
        public Arrow arrow;
        public Location location;
        public BlockFace blockFace;
        public GroundHitEvent(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace) {
            this.shooter = shooter;
            this.arrow = arrow;
            this.location = location;
            this.blockFace = blockFace;
        }
    }

    public static class EntityHitEvent {
        public LivingEntity shooter;
        public Arrow arrow;
        public Entity entity;
        public boolean arrowStopped;
        public EntityHitEvent(LivingEntity shooter, Arrow arrow, Entity entity, boolean arrowStopped) {
            this.shooter = shooter;
            this.arrow = arrow;
            this.entity = entity;
            this.arrowStopped = arrowStopped;
        }
    }

    public static class DamageEvent {
        public LivingEntity shooter;
        public Arrow arrow;
        public LivingEntity entity;
        public double damage;
        public DamageEvent(LivingEntity shooter, Arrow arrow, LivingEntity entity, double damage) {
            this.shooter = shooter;
            this.arrow = arrow;
            this.entity = entity;
            this.damage = damage;
        }
    }
}
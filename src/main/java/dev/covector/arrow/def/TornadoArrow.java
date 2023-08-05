package dev.covector.customarrows.arrow;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.Particle;

import dev.covector.customarrows.arrow.Utils;
import dev.covector.customarrows.CustomArrowsPlugin;

public class TornadoArrow extends CustomArrow {
    private static Color color = Color.fromRGB(80, 80, 80);
    private static String name = "Tornado Arrow";

    private double radius;
    private double radiusSquare;
    private double strength;
    private int duration;
    private String suffix;

    public TornadoArrow(double radius, double strength, int duration, String suffix) {
        this.radius = radius;
        this.radiusSquare = Math.pow(radius, 2);
        this.strength = strength / 10;
        this.duration = duration;
        this.suffix = suffix;
    }

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
        double period = 10.0;
        int interval = 2;
        new BukkitRunnable() {
            int ti = 0;

            public void run() {
                ti += interval;
                if (ti > duration * 20)
                    cancel();

                double xOffset = radius * Math.cos(ti / period);
                double zOffset = radius * Math.sin(ti / period);
                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.FUCHSIA, 1));
                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.FUCHSIA, 1));

                for (Entity entity : Utils.getNearbyChunkEntities(location)) {
                    if (entity.getLocation().distanceSquared(location) < radiusSquare && Utils.validTarget(entity)) {
                        Vector diffVec = location.clone().subtract(entity.getLocation()).toVector().normalize();
                        try { diffVec.checkFinite(); } catch (IllegalArgumentException e) { continue; }
                        entity.setVelocity((diffVec).multiply(strength));
                    }
                }
            }
        }.runTaskTimer(CustomArrowsPlugin.plugin, 0, interval);

    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(Player shooter, Arrow arrow, LivingEntity entity, double damage) {
        return -1;
    }

    public String getName() {
        return name + " " + suffix;
    }
}
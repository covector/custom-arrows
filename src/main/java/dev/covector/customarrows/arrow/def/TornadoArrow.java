package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.CustomArrow;

public class TornadoArrow extends CustomArrow {
    private static Color color = Color.fromRGB(99, 73, 230);
    private static String name = "Tornado Arrow";

    private double radius;
    // private double radiusSquare;
    private double strength;
    private int duration;
    private String suffix;

    public TornadoArrow(double radius, double strength, int duration, String suffix) {
        this.radius = radius;
        // this.radiusSquare = radius * radius;
        this.strength = strength / 10;
        this.duration = duration;
        this.suffix = suffix;
    }

    public void onHitGround(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace) {
        double period = 10.0;
        int interval = 2;
        new BukkitRunnable() {
            int ti = 0;

            public void run() {
                ti += interval;
                if (ti > duration * 20) {
                    cancel();
                    arrow.remove();
                }

                double xOffset = radius * Math.cos(ti / period);
                double zOffset = radius * Math.sin(ti / period);
                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.FUCHSIA, 1));
                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.FUCHSIA, 1));

                for (Entity entity : location.getWorld().getNearbyEntities(location, radius, radius, radius)) {
                    if (validTarget(entity)) {
                        Vector diffVec = location.clone().subtract(entity.getLocation()).toVector().normalize().multiply(strength);
                        try { diffVec.checkFinite(); } catch (IllegalArgumentException e) { continue; }
                        diffVec.setY(entity.getVelocity().getY());
                        entity.setVelocity(diffVec);
                    }
                }
            }
        }.runTaskTimer(CustomArrowsPlugin.plugin, 0, interval);

    }

    public void onHitEntity(LivingEntity shooter, Arrow arrow, Entity entity) {
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(LivingEntity shooter, Arrow arrow, LivingEntity entity, double damage) {
        return -1;
    }

    public String getName() {
        return name + " " + suffix;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Create a tornado when hit ground");
        lore.add(ChatColor.GRAY + "Players will be pulled as well");
        return lore;
    }

    private static boolean validTarget(Entity entity) {
        return entity instanceof LivingEntity && !(entity instanceof ArmorStand);
    }
}
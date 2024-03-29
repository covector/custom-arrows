package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.CustomArrow;

public class LandMineArrow extends CustomArrow {
    private static Color color = Color.fromRGB(54, 207, 74);
    private static String name = "Land Mine Arrow";

    private double triggerRadius;
    // private double triggerRadiusSquare;
    private double blastRadius;
    // private double blastRadiusSquare;
    private double damage;
    private double setupDuration;
    private double activeDuration;
    private int delayTick;
    private String suffix;
    private Random random = new Random();

    public LandMineArrow(double triggerRadius, double blastRadius, double damage, double setupDuration, double activeDuration, int delayTick, String suffix) {
        this.triggerRadius = triggerRadius;
        // this.triggerRadiusSquare = triggerRadius * triggerRadius;
        this.blastRadius = blastRadius;
        // this.blastRadiusSquare = blastRadius * blastRadius;
        this.damage = damage;
        this.setupDuration = setupDuration;
        this.activeDuration = activeDuration;
        this.delayTick = delayTick;
        this.suffix = suffix;
    }

    public void onHitGround(LivingEntity shooter, Arrow arrow, Location location, BlockFace blockFace) {
        // if (blockFace != BlockFace.UP) {
        //     arrow.remove();
        //     return;
        // }
        int interval = 2;
        new BukkitRunnable() {
            int ti = 0;
            int detonateTick = -1;

            public void run() {
                ti += interval;
                if (ti > (setupDuration + activeDuration) * 20 && (detonateTick == -1 || (ti > (setupDuration + activeDuration) * 20 + delayTick))) {
                    location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 8);
                    arrow.remove();
                    cancel();
                }
                    
                boolean active = ti > setupDuration * 20;  

                double thetaOffset = ti * Math.PI / 90;              

                if (detonateTick == -1) {
                    // setting up
                    if (active) {
                        double theta = random.nextDouble() * Math.PI * 2;
                        double xOffset = triggerRadius * Math.cos(theta);
                        double zOffset = triggerRadius * Math.sin(theta);
                        location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.LIME, 1));
                    } else {
                        for (double i = 0; i < Math.PI / 2; i += Math.PI / 10) {
                            double xOffset = triggerRadius * Math.cos(i + thetaOffset);
                            double zOffset = triggerRadius * Math.sin(i + thetaOffset);
                            location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.GRAY, 1));
                            location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.GRAY, 1));
                            location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.GRAY, 1));
                            location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.GRAY, 1));
                        }
                    }

                    for (Entity entity : location.getWorld().getNearbyEntities(location, triggerRadius, triggerRadius, triggerRadius)) {
                        // if ((entity instanceof LivingEntity || (entity instanceof Arrow && !(entity.getUniqueId().toString().equals(arrow.getUniqueId().toString()))))
                        if (entity instanceof LivingEntity
                            && !(entity instanceof ArmorStand)
                            && !(entity.getUniqueId().toString().equals(shooter.getUniqueId().toString()))) {
                            if (active) {
                                detonateTick = ti;
                            } else {
                                // deactivate
                                location.getWorld().spawnParticle(Particle.SMOKE_LARGE, location, 8);
                                arrow.remove();
                                cancel();
                            }
                            break;
                        }
                    }
                } else {
                    if (ti - detonateTick > delayTick) {
                        // explode
                        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location.add(0, .1, 0), 32, 1.7, 1.7, 1.7, 0);
                        location.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, location, 64, 0, 0, 0, .3);
                        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);

                        for (Entity entity : location.getWorld().getNearbyEntities(location, blastRadius, blastRadius, blastRadius))
                            if (entity instanceof LivingEntity && !(entity instanceof ArmorStand) && !(entity instanceof Player)) {
                                ((LivingEntity) entity).damage(damage);
                                entity.setVelocity(new Vector(entity.getVelocity().getX(), 1, entity.getVelocity().getZ()));
                        }

                        arrow.remove();
                        cancel();
                    } else {
                        // ticking
                        if ((ti - detonateTick) % 5 == 0) {
                            shooter.getWorld().playSound(location, Sound.ENTITY_BLAZE_HURT, 1, 1);
                            for (double i = 0; i < Math.PI / 2; i += Math.PI / 20) {
                                double xOffset = triggerRadius * Math.cos(i + thetaOffset);
                                double zOffset = triggerRadius * Math.sin(i + thetaOffset);
                                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.RED, 1));
                                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() + zOffset, 1, new Particle.DustOptions(Color.RED, 1));
                                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() + xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.RED, 1));
                                location.getWorld().spawnParticle(Particle.REDSTONE, location.getX() - xOffset, location.getY(), location.getZ() - zOffset, 1, new Particle.DustOptions(Color.RED, 1));
                            }
                        }
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
        lore.add(ChatColor.WHITE + "Set up a landmine");
        lore.add(ChatColor.GRAY + "Setup time: " + String.valueOf(setupDuration) + "s");
        lore.add(ChatColor.GRAY + "Any entity except self will deactivate mine during setup");
        lore.add(ChatColor.GRAY + "Mine will detonate " + String.valueOf(delayTick/20D) + "s after triggered by entity except self");
        lore.add(ChatColor.GRAY + "Mine will deactivate after " + String.valueOf(activeDuration) + "s");
        lore.add(ChatColor.GRAY + "Deals " + String.valueOf(damage) + " amount of damage");
        return lore;
    }
}
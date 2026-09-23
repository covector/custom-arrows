package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.CustomArrow;

public class SwapArrow extends CustomArrow {
    private static Color color = Color.PURPLE;
    private static String name = "Swap Arrow";

    public void onHitEntity(EntityHitEvent event) {
        // only run on last hit
        if (event.arrowStopped) {
            swap(event.shooter, event.arrow);
        }
    }

    public void onHitGround(GroundHitEvent event) {
        swap(event.shooter, event.arrow);
    }

    protected void swap(LivingEntity shooter, Arrow arrow) {
        List<Entity> entities = ArrowHelper.getPiercedEntities(arrow);

        if (entities.size() == 0) return;
        // tp all pierced entities to player
        int firstLivingEntityIndex = -1;
        for (int i = 0; i < entities.size(); i++) {
            if (entities.get(i) instanceof LivingEntity && !(entities.get(i).getUniqueId().toString().equals(shooter.getUniqueId().toString()))) {
                firstLivingEntityIndex = i;
                break;
            }
        }
        if (firstLivingEntityIndex == -1) return;
        Location loc = entities.get(firstLivingEntityIndex).getLocation().clone();
        for (Entity e : entities) {
            if (e instanceof LivingEntity && !(e.getUniqueId().toString().equals(shooter.getUniqueId().toString()))) {
                e.teleport(shooter.getLocation().clone());
            }
        }

        // play sound and tp player
        shooter.getWorld().playSound(shooter.getLocation().clone(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        shooter.teleport(loc);
        shooter.getWorld().playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
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

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Swap positions with hit entity");
        return lore;
    }
}
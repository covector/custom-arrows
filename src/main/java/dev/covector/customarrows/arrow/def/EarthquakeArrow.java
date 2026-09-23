package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import dev.covector.customarrows.CustomArrowsPlugin;
import dev.covector.customarrows.arrow.ArrowHelper;
import dev.covector.customarrows.arrow.ArrowRegistry;
import dev.covector.customarrows.arrow.CustomArrow;

public class EarthquakeArrow extends CustomArrow {
    private static Color color = Color.fromRGB(227, 227, 227);
    private static String name = "Earthquake Arrow";

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        LivingEntity shooter = event.shooter;
        Arrow arrow = event.arrow;
        Entity entity = event.entity;
        Location location = entity.getLocation();
        BlockFace blockFace = BlockFace.UP;
        UUID[] piercedEntities = ArrowHelper.getPiercedEntityIDs(arrow);

        GroundHitEvent groundHitEvent = new GroundHitEvent(shooter, arrow, location, blockFace, piercedEntities);
        ArrowHelper.triggerOnHitGround(groundHitEvent, id);
    }

    public Color getColor() {
        return color;
    }

    public double ModifyDamage(DamageEvent event) {
        return -1;
    }

    
    public String getName() {
        return name;
    }

    public boolean removeOnHitGround() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Trigger onHitGround ability");
        lore.add(ChatColor.GRAY + "under every hit mobs.");
        return lore;
    }
}

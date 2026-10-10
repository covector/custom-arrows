package dev.covector.customarrows.arrow.def;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
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
    private static Color color = Color.fromRGB(28, 6, 7);
    private static String name = "Earthquake Arrow";
    private int chainLimitMultiplier = 4;
    NamespacedKey chainedAmount = new NamespacedKey(CustomArrowsPlugin.plugin, "earthquake-chained-amount");

    public void onHitGround(GroundHitEvent event) {
    }

    public void onHitEntity(EntityHitEvent event) {
        // check if over chain limit
        Arrow arrow = event.arrow;
        if (!(arrow.getPersistentDataContainer().has(chainedAmount, PersistentDataType.INTEGER))) {
            arrow.getPersistentDataContainer().set(chainedAmount, PersistentDataType.INTEGER, 0);
        }
        int currentChainedAmount = arrow.getPersistentDataContainer().get(chainedAmount, PersistentDataType.INTEGER);
        int canPierce = arrow.getPierceLevel() + 1;
        int chainLimit = canPierce * chainLimitMultiplier;
        if (currentChainedAmount >= chainLimit) {
            return;
        }
        currentChainedAmount++;
        arrow.getPersistentDataContainer().set(chainedAmount, PersistentDataType.INTEGER, currentChainedAmount);

        // create ground hit event
        LivingEntity shooter = event.shooter;
        Entity entity = event.entity;
        Location location = entity.getLocation();
        BlockFace blockFace = BlockFace.UP;

        // don't trigger for entities pierced before
        UUID[] pierced = ArrowHelper.getPiercedEntityIDs(arrow);
        for (int i = 0; i < pierced.length; i++) {
            if (i == pierced.length - 1) { continue; } // skip last one
            if (pierced[i].toString().equals(entity.getUniqueId().toString())) {
                return;
            }
        }

        // trigger onHitGround
        GroundHitEvent groundHitEvent = new GroundHitEvent(shooter, arrow, location, blockFace);
        ArrowHelper.triggerOnHitGround(groundHitEvent, Set.of(id, 0)); // hardcode don't trigger swap arrow
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

    public boolean allowTrigger() {
        return true;
    }

    public ArrayList<String> getLore() {
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.WHITE + "Trigger onHitGround of other arrows");
        lore.add(ChatColor.WHITE + "under every hit mobs.");
        return lore;
    }
}

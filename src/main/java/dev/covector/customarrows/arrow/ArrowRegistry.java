package dev.covector.customarrows.arrow;

import java.util.ArrayList;

import org.bukkit.Bukkit;

import dev.covector.customarrows.arrow.def.*;

public class ArrowRegistry {
    private static ArrayList<CustomArrow> arrowTypes = new ArrayList<CustomArrow>();

    public static void registerAll() {
        registerArrow(new SwapArrow()); // 0
        registerArrow(new TornadoArrow(1, 2, 8, "Small")); // 1
        registerArrow(new TornadoArrow(3, 0.25, 6, "Big")); // 2
        registerArrow(new PercentDamageArrow(0.25)); // 3
        registerArrow(new HalfHealthArrow()); // 4
        registerArrow(new MarkingArrow(7)); // 5
        registerArrow(new DamageOverTimeArrow(12, 20, 2)); // 6
        registerArrow(new TrueDamageArrow()); // 7
        registerArrow(new DamageNearestArrow()); // 8
        registerArrow(new FramingArrow()); // 9
        registerArrow(new LandMineArrow(1, 1, 190, 3, 6, 20, "Strong")); // 10
        registerArrow(new LandMineArrow(1, 2, 38, 1, 2, 15, "Quick")); // 11
        registerArrow(new ChainingArrow()); // 12
        registerArrow(new RidingArrow()); // 13
        registerArrow(new WallLaserArrow()); // 14
        registerArrow(new LifeStealArrow()); // 15
        registerArrow(new EarthquakeArrow()); // 16
    }

    public static void unregisterAll() {
        arrowTypes.clear();
    }

    public static CustomArrow getArrowType(int index) {
        return arrowTypes.get(index);
    }

    public static void registerArrow(CustomArrow customArrow) {
        int id = getArrowTypeCount();
        customArrow.id = id;
        arrowTypes.add(customArrow);
    }

    public static int getArrowTypeCount() {
        return arrowTypes.size();
    }
}
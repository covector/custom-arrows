package dev.covector.customarrows.arrow;

import java.util.ArrayList;

import dev.covector.customarrows.arrow.def.*;

public class ArrowRegistry {
    private static ArrayList<CustomArrow> arrowTypes = new ArrayList<CustomArrow>();

    public static void register() {
        arrowTypes.add(new SwapArrow()); // 0
        arrowTypes.add(new TornadoArrow(1, 2, 8, "Small")); // 1
        arrowTypes.add(new TornadoArrow(3, 0.25, 6, "Big")); // 2
        arrowTypes.add(new PercentDamageArrow(0.25)); // 3
        arrowTypes.add(new HalfHealthArrow()); // 4
        arrowTypes.add(new MarkingArrow(7)); // 5
        arrowTypes.add(new DamageOverTimeArrow(12, 3)); // 6
        arrowTypes.add(new TrueDamageArrow()); // 7
        arrowTypes.add(new DamageNearestArrow()); // 8
        arrowTypes.add(new FramingArrow()); // 9
        arrowTypes.add(new LandMineArrow(1, 1, 190, 3, 6, 20, "Strong")); // 10
        arrowTypes.add(new LandMineArrow(1, 2, 38, 1, 2, 15, "Quick")); // 11
        arrowTypes.add(new ChainingArrow()); // 12
        arrowTypes.add(new RidingArrow()); // 13
        arrowTypes.add(new WallLaserArrow()); // 14
        arrowTypes.add(new LifeStealArrow()); // 15
    }

    public static CustomArrow getArrowType(int index) {
        return arrowTypes.get(index);
    }

    public static int getArrowTypeCount() {
        return arrowTypes.size();
    }
}
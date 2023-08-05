package dev.covector.customarrows.arrow;

import java.util.ArrayList;

public class ArrowRegistry {
    private static ArrayList<CustomArrow> arrowTypes = new ArrayList<CustomArrow>();

    public static void register() {
        arrowTypes.add(new SwapArrow()); // 0
        arrowTypes.add(new TornadoArrow(1, 1, 8, "Small")); // 1
        arrowTypes.add(new TornadoArrow(3, 0.2, 8, "Big")); // 2
        arrowTypes.add(new PercentDamageArrow(0.25)); // 3
        arrowTypes.add(new HalfHealthArrow()); // 4
        arrowTypes.add(new MarkingArrow(10)); // 5
        arrowTypes.add(new DamageOverTimeArrow(10, 6)); // 6
        arrowTypes.add(new TrueDamageArrow()); // 7
    }

    public static CustomArrow getArrowType(int index) {
        return arrowTypes.get(index);
    }

    public static int getArrowTypeCount() {
        return arrowTypes.size();
    }
}
package dev.covector.customarrows.arrow;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class PierceAwareArrow extends CustomArrow {
    private HashMap<String, PierceEntityData> piercedEntities = new HashMap<String, PierceEntityData>();

    public void onHitGround(Player shooter, Arrow arrow, Location location) {
        if (piercedEntities.containsKey(arrow.getUniqueId().toString())) {
            PierceEntityData data = piercedEntities.get(arrow.getUniqueId().toString());
            onAfterHitAll(shooter, arrow, removeNull(data.piercedEntities));
            piercedEntities.remove(arrow.getUniqueId().toString());
        } else {
            onAfterHitAll(shooter, arrow, new Entity[]{});
        }
    }

    public void onHitEntity(Player shooter, Arrow arrow, Entity entity) {
        if (piercedEntities.containsKey(arrow.getUniqueId().toString())) {
            PierceEntityData data = piercedEntities.get(arrow.getUniqueId().toString());
            data.addPiercedEntity(entity);
            if (arrow.getPierceLevel() == 0) {
                onAfterHitAll(shooter, arrow, removeNull(data.piercedEntities));
                piercedEntities.remove(arrow.getUniqueId().toString());
            }
        } else {
            if (arrow.getPierceLevel() == 0) {
                onAfterHitAll(shooter, arrow, new Entity[]{entity});
                return;
            }
            piercedEntities.put(arrow.getUniqueId().toString(), new PierceEntityData(entity, arrow.getPierceLevel()));
        }
    }

    public abstract void onAfterHitAll(Player shooter, Arrow arrow, Entity[] entity);

    private Entity[] removeNull(Entity[] array) {
        int nullCount = 0;
        for (Entity entity : array) {
            if (entity == null) {
                nullCount++;
            }
        }
        Entity[] newArray = new Entity[array.length - nullCount];
        int i = 0;
        for (Entity entity : array) {
            if (entity != null) {
                newArray[i] = entity;
                i++;
            }
        }
        return newArray;
    }

    public void memoryLeakPrevention() {
        for (String key : piercedEntities.keySet()) {
            PierceEntityData data = piercedEntities.get(key);
            if (data.createdLongerThan(30000)) {
                piercedEntities.remove(key);
            }
        }
    }

    class PierceEntityData {
        public Entity[] piercedEntities;
        public int piercedCount;
        public long creationTime;

        public PierceEntityData(Entity firstPierced, int pierceLevel) {
            this.piercedEntities = new Entity[pierceLevel+1];
            this.piercedEntities[0] = firstPierced;
            this.piercedCount = 1;
            this.creationTime = System.currentTimeMillis();
        }

        public void addPiercedEntity(Entity entity) {
            if (this.piercedCount == this.piercedEntities.length) {
                // havent cleared last pierce data
                piercedCount = 0;
            }
            this.piercedEntities[this.piercedCount] = entity;
            this.piercedCount++;
        }

        public boolean createdLongerThan(int time) {
            return System.currentTimeMillis() - this.creationTime > time;
        }
    }
}
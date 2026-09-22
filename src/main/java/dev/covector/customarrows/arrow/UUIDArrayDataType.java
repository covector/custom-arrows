package dev.covector.customarrows.arrow;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import java.util.UUID;

public class UUIDArrayDataType implements PersistentDataType<long[], UUID[]> {

    public static final UUIDArrayDataType INSTANCE = new UUIDArrayDataType();

    private UUIDArrayDataType() {}

    @Override
    public Class<long[]> getPrimitiveType() {
        return long[].class;
    }

    @Override
    public Class<UUID[]> getComplexType() {
        return UUID[].class;
    }

    @Override
    public long[] toPrimitive(UUID[] complex, PersistentDataAdapterContext context) {
        long[] primitives = new long[complex.length * 2];
        for (int i = 0; i < complex.length; i++) {
            primitives[i * 2] = complex[i].getMostSignificantBits();
            primitives[i * 2 + 1] = complex[i].getLeastSignificantBits();
        }
        return primitives;
    }

    @Override
    public UUID[] fromPrimitive(long[] primitive, PersistentDataAdapterContext context) {
        UUID[] complex = new UUID[primitive.length / 2];
        for (int i = 0; i < complex.length; i++) {
            long mostSig = primitive[i * 2];
            long leastSig = primitive[i * 2 + 1];
            complex[i] = new UUID(mostSig, leastSig);
        }
        return complex;
    }
}
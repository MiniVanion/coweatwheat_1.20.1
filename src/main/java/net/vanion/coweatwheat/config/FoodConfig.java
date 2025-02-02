package net.vanion.coweatwheat.config;

import java.util.List;
import java.util.Map;

public class FoodConfig {

    // Hardcoded configuration mapping entity names to their food settings.
    // The FoodConfigEntry contains:
    //   - dropped: a list of item IDs that the entity is interested in as dropped food.
    //   - crop: the block ID of the planted crop that the entity should target.
    // For animals that should not search for planted crops, set crop to null.
    private static final Map<String, FoodConfigEntry> foodMapping = Map.of(
            "CowEntity", new FoodConfigEntry(List.of("minecraft:wheat"), "minecraft:wheat"),
            "PigEntity", new FoodConfigEntry(List.of("minecraft:carrot", "minecraft:potato", "minecraft:beetroot"), "minecraft:carrots"),
            "ChickenEntity", new FoodConfigEntry(List.of("minecraft:wheat_seeds"), null),
            "SheepEntity", new FoodConfigEntry(List.of("minecraft:wheat"), "minecraft:wheat")
    );

    /**
     * Returns the FoodConfigEntry for the given entity name.
     */
    public static FoodConfigEntry getFoodConfigEntry(String entityName) {
        return foodMapping.get(entityName);
    }

    /**
     * Returns the list of dropped food item IDs for the given entity.
     */
    public static List<String> getDroppedFoodIds(String entityName) {
        FoodConfigEntry entry = foodMapping.get(entityName);
        return entry != null ? entry.dropped : List.of();
    }

    /**
     * Returns the crop block ID for the given entity, or null if not applicable.
     */
    public static String getCropBlockId(String entityName) {
        FoodConfigEntry entry = foodMapping.get(entityName);
        return entry != null ? entry.crop : null;
    }

    /**
     * A simple POJO that holds the food configuration for an entity.
     */
    public static class FoodConfigEntry {
        public List<String> dropped;
        public String crop;

        public FoodConfigEntry(List<String> dropped, String crop) {
            this.dropped = dropped;
            this.crop = crop;
        }
    }
}

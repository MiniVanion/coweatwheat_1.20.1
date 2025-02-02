package net.vanion.coweatwheat.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class FoodConfig {
    private static final String CONFIG_PATH = "config/coweatwheat.json";
    private static Map<String, List<String>> foodMapping;

    /**
     * Loads the configuration from disk. If the file does not exist, default values are used.
     */
    public static void loadConfig() {
        try {
            File file = new File(CONFIG_PATH);
            Gson gson = new Gson();

            if (!file.exists()) {
                // Use default values.
                foodMapping = Map.of(
                        "CowEntity", List.of("minecraft:wheat"),
                        "PigEntity", List.of("minecraft:carrot", "minecraft:potato", "minecraft:beetroot"),
                        "ChickenEntity", List.of("minecraft:wheat_seeds")
                );
                // Write the default config to disk so users have a file to edit.
                file.getParentFile().mkdirs();
                try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
                    gson.toJson(foodMapping, writer);
                }
            } else {
                try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
                    Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
                    foodMapping = gson.fromJson(reader, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback defaults in case of error.
            foodMapping = Map.of(
                    "CowEntity", List.of("minecraft:wheat"),
                    "PigEntity", List.of("minecraft:carrot", "minecraft:potato", "minecraft:beetroot"),
                    "ChickenEntity", List.of("minecraft:wheat_seeds")
            );
        }
    }

    /**
     * Returns a list of Item objects corresponding to the food items for the given entity.
     * @param entityName The simple class name of the entity (e.g. "CowEntity")
     */
    public static List<Item> getFoodItems(String entityName) {
        List<String> ids = foodMapping.get(entityName);
        if (ids == null) return List.of();
        return ids.stream().map(id -> Registry.ITEM.get(new Identifier(id))).toList();
    }
}

package io.github.redpanda4552.SimpleEgg.util;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class MaterialProvider {

    private static final String SPAWN_EGG_STRING = "_SPAWN_EGG";
    
    /**
     * Predetermined mappings for whenever a rough conversion from EntityType
     * to Material fails.
     */
    private static final Object[][] eggTable = new Object[][] {
        {   EntityType.MUSHROOM_COW ,   Material.MOOSHROOM_SPAWN_EGG    },
    };
    
    /**
     * Find the right Material to represent the spawn egg of an entity type.
     * For most living entities, this is it's entity type appended with
     * "_SPAWN_EGG", however a small handful do not follow this rule, and must
     * be manually looked up in a table.
     */
    public static Material fromEntityType(EntityType entityType) {
        String conversionAttempt = entityType.toString() + SPAWN_EGG_STRING;
        Material ret = null;
        
        try {
            ret = Material.valueOf(conversionAttempt);
        } catch (IllegalArgumentException e) {
            for (Object[] objArr : eggTable) {
                if (objArr[0] == entityType) {
                    return (Material) objArr[1];
                }
            }
        }
        
        return ret;
    }
    
    public static EntityType toEntityType(Material material) {
        String conversionAttempt = material.toString().replace(SPAWN_EGG_STRING, "");
        EntityType ret = null;
        
        try {
            ret = EntityType.valueOf(conversionAttempt);
        } catch (IllegalArgumentException e) {
            for (Object[] objArr : eggTable) {
                if (objArr[1] == material) {
                    return (EntityType) objArr[0];
                }
            }
        }
        
        return ret;
    }
}

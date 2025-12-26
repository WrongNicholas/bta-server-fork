package net.minecraft.core.data.legacy;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;

public abstract class LegacyWorldTypes {
   private static final Map<Integer, WorldType> idToWorldTypeMap = new HashMap<>();
   private static final Map<WorldType, Integer> worldTypeToIdMap = new HashMap<>();
   private static final Map<String, WorldType> keyToWorldTypeMap = new HashMap<>();
   private static final Map<WorldType, String> worldTypeToKeyMap = new HashMap<>();

   private static void register(WorldType worldType, int id, String key) {
      idToWorldTypeMap.put(id, worldType);
      worldTypeToIdMap.put(worldType, id);
      keyToWorldTypeMap.put(key, worldType);
      worldTypeToKeyMap.put(worldType, key);
   }

   public static WorldType getWorldTypeByKey(String key) {
      return keyToWorldTypeMap.get(key);
   }

   public static WorldType getWorldTypeById(int id) {
      return idToWorldTypeMap.get(id);
   }

   public static int getWorldTypeId(WorldType worldType) {
      return worldTypeToIdMap.get(worldType);
   }

   public static String getWorldTypeKey(WorldType worldType) {
      return worldTypeToKeyMap.get(worldType);
   }

   static {
      register(WorldTypes.OVERWORLD_DEFAULT, 0, "default");
      register(WorldTypes.OVERWORLD_ISLANDS, 1, "islands");
      register(WorldTypes.OVERWORLD_WINTER, 2, "winter");
      register(WorldTypes.FLAT, 3, "flat");
      register(WorldTypes.OVERWORLD_HELL, 4, "hell");
      register(WorldTypes.OVERWORLD_WOODS, 5, "woods");
      register(WorldTypes.OVERWORLD_PARADISE, 6, "paradise");
      register(WorldTypes.OVERWORLD_RETRO, 7, "retro");
      register(WorldTypes.OVERWORLD_EXTENDED, 10, "extended");
      register(WorldTypes.NETHER_DEFAULT, 11, "nether");
      register(WorldTypes.PARADISE_DEFAULT, 12, "paradise_");
      register(WorldTypes.EMPTY, 13, "empty");
      register(WorldTypes.DEBUG, 14, "debug");
      register(WorldTypes.OVERWORLD_FLOATING, 15, "floating");
      register(WorldTypes.OVERWORLD_AMPLIFIED, 16, "amplified");
   }
}

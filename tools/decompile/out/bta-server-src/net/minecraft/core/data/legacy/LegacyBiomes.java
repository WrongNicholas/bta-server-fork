package net.minecraft.core.data.legacy;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;

public abstract class LegacyBiomes {
   private static final Map<Integer, Biome> idToBiomeMap = new HashMap<>();
   private static final Map<Biome, Integer> biomeToIdMap = new HashMap<>();

   private static void register(Biome biome, int id) {
      idToBiomeMap.put(id, biome);
      biomeToIdMap.put(biome, id);
   }

   public static Biome getBiomeById(int id) {
      return idToBiomeMap.get(id);
   }

   public static int getBiomeId(Biome biome) {
      return biomeToIdMap.get(biome);
   }

   static {
      register(Biomes.OVERWORLD_RAINFOREST, 0);
      register(Biomes.OVERWORLD_SWAMPLAND, 1);
      register(Biomes.OVERWORLD_SEASONAL_FOREST, 2);
      register(Biomes.OVERWORLD_FOREST, 3);
      register(Biomes.OVERWORLD_GRASSLANDS, 4);
      register(Biomes.OVERWORLD_OUTBACK, 5);
      register(Biomes.OVERWORLD_SHRUBLAND, 6);
      register(Biomes.OVERWORLD_TAIGA, 7);
      register(Biomes.OVERWORLD_BOREAL_FOREST, 8);
      register(Biomes.OVERWORLD_DESERT, 9);
      register(Biomes.OVERWORLD_PLAINS, 10);
      register(Biomes.OVERWORLD_GLACIER, 11);
      register(Biomes.OVERWORLD_TUNDRA, 12);
      register(Biomes.OVERWORLD_MEADOW, 13);
      register(Biomes.NETHER_NETHER, 14);
      register(Biomes.PARADISE_PARADISE, 15);
      register(Biomes.OVERWORLD_BIRCH_FOREST, 16);
      register(Biomes.OVERWORLD_RETRO, 17);
      register(Biomes.OVERWORLD_HELL, 18);
      register(Biomes.OVERWORLD_SWAMPLAND_MUDDY, 19);
      register(Biomes.OVERWORLD_OUTBACK_GRASSY, 20);
   }
}

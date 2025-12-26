package net.minecraft.core.world.biome;

import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobChicken;

public class BiomeParadise extends Biome {
   public BiomeParadise(String key) {
      super(key);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableCreatureList.add(new SpawnListEntry(MobChicken.class, 10));
   }

   @Override
   public int getSkyColor(float temperature) {
      return 16762247;
   }
}

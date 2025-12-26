package net.minecraft.core.world.biome;

import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.monster.MobGhast;
import net.minecraft.core.entity.monster.MobZombiePig;

public class BiomeNether extends Biome {
   public BiomeNether(String key) {
      super(key);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableAmbientCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(MobGhast.class, 10));
      this.spawnableMonsterList.add(new SpawnListEntry(MobZombiePig.class, 10));
   }
}

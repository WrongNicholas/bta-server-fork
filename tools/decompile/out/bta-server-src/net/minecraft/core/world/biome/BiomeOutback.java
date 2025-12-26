package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeEucalyptus;

public class BiomeOutback extends Biome {
   public BiomeOutback(String key) {
      super(key);
      this.spawnableMonsterList.clear();
      this.spawnableCreatureList.clear();
      this.spawnableWaterCreatureList.clear();
      this.spawnableMonsterList.add(new SpawnListEntry(MobSpider.class, 20));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreeEucalyptus(Blocks.LEAVES_EUCALYPTUS.id(), Blocks.LOG_EUCALYPTUS.id());
   }
}

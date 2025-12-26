package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobWolf;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeCherry;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;

public class BiomeSeasonalForest extends Biome {
   public BiomeSeasonalForest(String key) {
      super(key);
      this.spawnableCreatureList.add(new SpawnListEntry(MobWolf.class, 25));
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      if (random.nextInt(3) == 0) {
         return (WorldFeature)(random.nextInt(2) == 0
            ? new WorldFeatureTreeFancy(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id())
            : new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 4));
      } else {
         return (WorldFeature)(random.nextInt(3) == 0
            ? new WorldFeatureTreeFancy(Blocks.LEAVES_CHERRY.id(), Blocks.LOG_CHERRY.id())
            : new WorldFeatureTreeCherry(Blocks.LEAVES_CHERRY.id(), Blocks.LOG_CHERRY.id(), 4));
      }
   }
}

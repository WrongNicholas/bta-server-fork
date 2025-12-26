package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancyRainforest;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreePalm;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeRound;

public class BiomeRainforest extends Biome {
   public BiomeRainforest(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      if (random.nextInt(1000) == 0) {
         int treeHeight = 30 + random.nextInt(30);
         return new WorldFeatureSpoonerTreeRound(treeHeight, Blocks.LOG_OAK.id(), 0, Blocks.LEAVES_OAK.id(), 0);
      } else if (random.nextInt(10) == 0) {
         return new WorldFeatureTreePalm(Blocks.LOG_PALM, Blocks.LEAVES_PALM, true, false, true);
      } else if (random.nextInt(3) == 0) {
         return random.nextInt(10) == 0
            ? new WorldFeatureTreeFancyRainforest(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK_MOSSY.id(), 0)
            : new WorldFeatureTreeFancyRainforest(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 0);
      } else if (random.nextInt(5) == 0) {
         return new WorldFeatureTreeShrub(Blocks.LEAVES_SHRUB.id(), Blocks.LOG_OAK.id());
      } else {
         return random.nextInt(10) == 0
            ? new WorldFeatureTree(Blocks.LEAVES_CACAO.id(), Blocks.LOG_OAK_MOSSY.id(), 4)
            : new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 6);
      }
   }
}

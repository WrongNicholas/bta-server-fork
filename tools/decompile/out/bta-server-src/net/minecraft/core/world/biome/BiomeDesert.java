package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreePalm;

public class BiomeDesert extends Biome {
   public BiomeDesert(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreePalm(Blocks.LOG_PALM, Blocks.LEAVES_PALM, random.nextInt(10) == 0, false, true);
   }
}

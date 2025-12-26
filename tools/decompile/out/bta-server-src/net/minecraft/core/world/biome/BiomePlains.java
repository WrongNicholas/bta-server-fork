package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeEucalyptus;

public class BiomePlains extends Biome {
   public BiomePlains(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreeEucalyptus(Blocks.LEAVES_EUCALYPTUS.id(), Blocks.LOG_EUCALYPTUS.id());
   }
}

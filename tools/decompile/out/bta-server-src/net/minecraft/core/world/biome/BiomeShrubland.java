package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;

public class BiomeShrubland extends Biome {
   public BiomeShrubland(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreeShrub(Blocks.LEAVES_SHRUB.id(), Blocks.LOG_OAK.id());
   }
}

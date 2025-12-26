package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShapeSwamp;

public class BiomeSwamp extends Biome {
   public BiomeSwamp(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return new WorldFeatureTreeShapeSwamp(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK_MOSSY.id(), 6);
   }
}

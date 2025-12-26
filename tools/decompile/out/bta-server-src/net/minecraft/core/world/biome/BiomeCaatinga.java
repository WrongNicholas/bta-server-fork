package net.minecraft.core.world.biome;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeThorn;

public class BiomeCaatinga extends Biome {
   public BiomeCaatinga(String key) {
      super(key);
   }

   @Override
   public WorldFeature getRandomWorldGenForTrees(Random random) {
      return (WorldFeature)(random.nextInt(3) != 0
         ? new WorldFeatureTreeShrub(Blocks.LEAVES_THORN.id(), Blocks.LOG_OAK.id())
         : new WorldFeatureTreeThorn(4, Blocks.LOG_THORN.id(), 0, Blocks.LEAVES_THORN.id(), 0));
   }
}

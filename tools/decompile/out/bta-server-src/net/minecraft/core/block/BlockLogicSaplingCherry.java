package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureCherryTreeFancy;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeCherry;

public class BlockLogicSaplingCherry extends BlockLogicSaplingBase {
   public BlockLogicSaplingCherry(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      WorldFeature treeBig = new WorldFeatureCherryTreeFancy(Blocks.LEAVES_CHERRY.id(), Blocks.LOG_CHERRY.id());
      WorldFeature treeSmall = new WorldFeatureTreeCherry(Blocks.LEAVES_CHERRY.id(), Blocks.LOG_CHERRY.id(), 4);
      world.setBlock(x, y, z, 0);
      if (!treeSmall.place(world, random, x, y, z) && !treeBig.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}

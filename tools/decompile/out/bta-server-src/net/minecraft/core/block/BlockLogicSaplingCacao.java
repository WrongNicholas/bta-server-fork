package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;

public class BlockLogicSaplingCacao extends BlockLogicSaplingBase {
   public BlockLogicSaplingCacao(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      WorldFeature treeSmall = new WorldFeatureTree(Blocks.LEAVES_CACAO.id(), Blocks.LOG_OAK_MOSSY.id(), 4);
      world.setBlock(x, y, z, 0);
      if (!treeSmall.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}

package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeEucalyptus;

public class BlockLogicSaplingEucalyptus extends BlockLogicSaplingBase {
   public BlockLogicSaplingEucalyptus(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      world.setBlock(x, y, z, 0);
      WorldFeature tree = new WorldFeatureTreeEucalyptus(Blocks.LEAVES_EUCALYPTUS.id(), Blocks.LOG_EUCALYPTUS.id());
      tree.init(1.0, 1.0, 1.0);
      if (!tree.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}

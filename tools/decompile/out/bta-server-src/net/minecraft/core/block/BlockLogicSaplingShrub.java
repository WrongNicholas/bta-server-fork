package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeShrub;

public class BlockLogicSaplingShrub extends BlockLogicSaplingBase {
   public BlockLogicSaplingShrub(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      world.setBlock(x, y, z, 0);
      WorldFeature tree = new WorldFeatureTreeShrub(Blocks.LEAVES_SHRUB.id(), Blocks.LOG_OAK.id());
      if (!tree.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}

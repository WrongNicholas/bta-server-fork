package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeThorn;

public class BlockLogicSaplingThorn extends BlockLogicSaplingBase {
   public BlockLogicSaplingThorn(Block<?> block) {
      super(block);
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      world.setBlock(x, y, z, 0);
      WorldFeature treeFeature = new WorldFeatureTreeThorn(1, Blocks.LOG_THORN.id(), 0, Blocks.LEAVES_THORN.id(), 0);
      if (!treeFeature.place(world, random, x, y, z)) {
         world.setBlock(x, y, z, this.id());
      }
   }
}

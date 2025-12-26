package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreePalm;

public class BlockLogicSaplingPalm extends BlockLogicSaplingBase {
   public BlockLogicSaplingPalm(Block<?> block) {
      super(block);
      this.canGrowOnSand = true;
   }

   @Override
   public void growTree(World world, int x, int y, int z, Random random) {
      boolean big = world.getBlockId(x, y - 1, z) != Blocks.SAND.id();
      new WorldFeatureTreePalm(Blocks.LOG_PALM, Blocks.LEAVES_PALM, big, true, false).place(world, random, x, y, z);
   }
}

package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureSpinifexPatch extends WorldFeature {
   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int l;
      while (((l = world.getBlockId(x, y, z)) == 0 || Block.hasLogicClass(Blocks.blocksList[l], BlockLogicLeavesBase.class)) && y > 0) {
         y--;
      }

      for (int i1 = 0; i1 < 64; i1++) {
         int j1 = x + random.nextInt(4) - random.nextInt(4);
         int k1 = y + random.nextInt(2) - random.nextInt(2);
         int l1 = z + random.nextInt(4) - random.nextInt(4);
         int meta = 0;
         if (world.isAirBlock(j1, k1, l1) && Blocks.SPINIFEX.canBlockStay(world, j1, k1, l1)) {
            world.setBlockAndMetadata(j1, k1, l1, Blocks.SPINIFEX.id(), meta);
         }
      }

      return true;
   }
}

package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeaturePumpkin extends WorldFeature {
   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      for (int l = 0; l < 64; l++) {
         int i1 = x + random.nextInt(8) - random.nextInt(8);
         int j1 = y + random.nextInt(4) - random.nextInt(4);
         int k1 = z + random.nextInt(8) - random.nextInt(8);
         if (world.isAirBlock(i1, j1, k1) && world.getBlockId(i1, j1 - 1, k1) == Blocks.GRASS.id() && Blocks.PUMPKIN.canPlaceBlockAt(world, i1, j1, k1)) {
            if (random.nextInt(50) == 0) {
               world.setBlockAndMetadata(i1, j1, k1, Blocks.PUMPKIN_CARVED_IDLE.id(), random.nextInt(4));
            } else {
               world.setBlockWithNotify(i1, j1, k1, Blocks.PUMPKIN.id());
            }
         }
      }

      return true;
   }
}

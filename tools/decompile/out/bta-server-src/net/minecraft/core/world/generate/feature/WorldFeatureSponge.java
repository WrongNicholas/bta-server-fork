package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureSponge extends WorldFeature {
   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      if (world.getBlockId(x, y - 1, z) != Blocks.FLUID_WATER_STILL.id()) {
         return false;
      } else {
         for (int l = 0; l < 16; l++) {
            int i1 = x + random.nextInt(8) - random.nextInt(8);
            int k1 = z + random.nextInt(8) - random.nextInt(8);

            while (world.getBlockId(i1, y - 1, k1) == Blocks.FLUID_WATER_STILL.id()) {
               y--;
            }

            if ((world.getBlockId(i1, y - 1, k1) == Blocks.SAND.id() || world.getBlockId(i1, y - 1, k1) == Blocks.DIRT.id())
               && world.getBlockId(i1, y + 1, k1) != 0) {
               world.setBlockRaw(i1, y, k1, Blocks.SPONGE_WET.id());
            }
         }

         return true;
      }
   }
}

package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureGlowstoneB extends WorldFeature {
   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      if (!world.isAirBlock(x, y, z)) {
         return false;
      } else if (world.getBlockId(x, y + 1, z) != Blocks.COBBLE_NETHERRACK.id()) {
         return false;
      } else {
         world.setBlockWithNotify(x, y, z, Blocks.GLOWSTONE.id());

         for (int l = 0; l < 1500; l++) {
            int i1 = x + random.nextInt(8) - random.nextInt(8);
            int j1 = y - random.nextInt(12);
            int k1 = z + random.nextInt(8) - random.nextInt(8);
            if (world.getBlockId(i1, j1, k1) == 0) {
               int l1 = 0;

               for (int i2 = 0; i2 < 6; i2++) {
                  int j2 = 0;
                  if (i2 == 0) {
                     j2 = world.getBlockId(i1 - 1, j1, k1);
                  }

                  if (i2 == 1) {
                     j2 = world.getBlockId(i1 + 1, j1, k1);
                  }

                  if (i2 == 2) {
                     j2 = world.getBlockId(i1, j1 - 1, k1);
                  }

                  if (i2 == 3) {
                     j2 = world.getBlockId(i1, j1 + 1, k1);
                  }

                  if (i2 == 4) {
                     j2 = world.getBlockId(i1, j1, k1 - 1);
                  }

                  if (i2 == 5) {
                     j2 = world.getBlockId(i1, j1, k1 + 1);
                  }

                  if (j2 == Blocks.GLOWSTONE.id()) {
                     l1++;
                  }
               }

               if (l1 == 1) {
                  world.setBlockWithNotify(i1, j1, k1, Blocks.GLOWSTONE.id());
               }
            }
         }

         return true;
      }
   }
}

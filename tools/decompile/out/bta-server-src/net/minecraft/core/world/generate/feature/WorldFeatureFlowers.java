package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlowerStackable;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureFlowers extends WorldFeature {
   private final int plantBlockId;
   private final int count;
   private final boolean varyStackSize;

   @MethodParametersAnnotation(names = {"plantBlockId", "count", "varyStackSize"})
   public WorldFeatureFlowers(int plantBlockId, int count, boolean varyStackSize) {
      this.plantBlockId = plantBlockId;
      this.count = count;
      this.varyStackSize = varyStackSize;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      for (int l = 0; l < this.count; l++) {
         int i1 = x + random.nextInt(8) - random.nextInt(8);
         int j1 = y + random.nextInt(4) - random.nextInt(4);
         int k1 = z + random.nextInt(8) - random.nextInt(8);
         if (world.isAirBlock(i1, j1, k1) && Blocks.blocksList[this.plantBlockId].canBlockStay(world, i1, j1, k1)) {
            int metadata;
            if (Block.hasLogicClass(Blocks.getBlock(this.plantBlockId), BlockLogicFlowerStackable.class)) {
               int stackSize;
               if (this.varyStackSize) {
                  stackSize = getStackSize(random);
               } else {
                  stackSize = 0;
               }

               metadata = BlockLogicFlowerStackable.setStackCount(0, stackSize);
            } else {
               metadata = 0;
            }

            world.setBlockAndMetadata(i1, j1, k1, this.plantBlockId, metadata);
         }
      }

      return true;
   }

   public static int getStackSize(Random random) {
      int odds = random.nextInt(15);
      if (odds < 8) {
         return 0;
      } else if (odds < 12) {
         return 1;
      } else {
         return odds < 14 ? 2 : 3;
      }
   }
}

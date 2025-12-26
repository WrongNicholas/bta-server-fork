package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlowerStackable;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureMeadow extends WorldFeature {
   private final int[] blockIds;

   @MethodParametersAnnotation(names = "blockIds")
   public WorldFeatureMeadow(int[] blockIds) {
      this.blockIds = blockIds;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      int l;
      while (((l = world.getBlockId(x, y, z)) == 0 || Block.hasLogicClass(Blocks.blocksList[l], BlockLogicLeavesBase.class)) && y > 0) {
         y--;
      }

      for (int i1 = 0; i1 < 128; i1++) {
         int j1 = x + random.nextInt(8) - random.nextInt(8);
         int k1 = y + random.nextInt(4) - random.nextInt(4);
         int l1 = z + random.nextInt(8) - random.nextInt(8);
         int index = random.nextInt(this.blockIds.length);
         int flowerId = this.blockIds[index];
         if (world.isAirBlock(j1, k1, l1) && Blocks.blocksList[flowerId].canBlockStay(world, j1, k1, l1)) {
            int meta = BlockLogicFlowerStackable.setStackCount(0, WorldFeatureFlowers.getStackSize(random));
            world.setBlockAndMetadata(j1, k1, l1, flowerId, meta);
         }
      }

      return true;
   }
}

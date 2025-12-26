package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureLiquid extends WorldFeature {
   private int liquidBlockId;

   @MethodParametersAnnotation(names = "liquidId")
   public WorldFeatureLiquid(int liquidId) {
      this.liquidBlockId = liquidId;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      if (world.getBlockId(x, y + 1, z) != Blocks.STONE.id()) {
         return false;
      } else if (world.getBlockId(x, y - 1, z) != Blocks.STONE.id()) {
         return false;
      } else if (world.getBlockId(x, y, z) != 0 && world.getBlockId(x, y, z) != Blocks.STONE.id()) {
         return false;
      } else {
         int l = 0;
         if (world.getBlockId(x - 1, y, z) == Blocks.STONE.id()) {
            l++;
         }

         if (world.getBlockId(x + 1, y, z) == Blocks.STONE.id()) {
            l++;
         }

         if (world.getBlockId(x, y, z - 1) == Blocks.STONE.id()) {
            l++;
         }

         if (world.getBlockId(x, y, z + 1) == Blocks.STONE.id()) {
            l++;
         }

         int i1 = 0;
         if (world.isAirBlock(x - 1, y, z)) {
            i1++;
         }

         if (world.isAirBlock(x + 1, y, z)) {
            i1++;
         }

         if (world.isAirBlock(x, y, z - 1)) {
            i1++;
         }

         if (world.isAirBlock(x, y, z + 1)) {
            i1++;
         }

         if (l == 3 && i1 == 1) {
            world.setBlockWithNotify(x, y, z, this.liquidBlockId);
            world.scheduledUpdatesAreImmediate = true;
            Blocks.blocksList[this.liquidBlockId].updateTick(world, x, y, z, random);
            world.scheduledUpdatesAreImmediate = false;
         }

         return true;
      }
   }
}

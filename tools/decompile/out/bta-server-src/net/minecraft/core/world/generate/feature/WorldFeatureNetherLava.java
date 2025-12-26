package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureNetherLava extends WorldFeature {
   private final int blockId;

   @MethodParametersAnnotation(names = "blockId")
   public WorldFeatureNetherLava(int blockId) {
      this.blockId = blockId;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      if (world.getBlockId(x, y + 1, z) != Blocks.COBBLE_NETHERRACK.id()) {
         return false;
      } else if (world.getBlockId(x, y, z) != 0 && world.getBlockId(x, y, z) != Blocks.COBBLE_NETHERRACK.id()) {
         return false;
      } else {
         int l = 0;
         if (world.getBlockId(x - 1, y, z) == Blocks.COBBLE_NETHERRACK.id()) {
            l++;
         }

         if (world.getBlockId(x + 1, y, z) == Blocks.COBBLE_NETHERRACK.id()) {
            l++;
         }

         if (world.getBlockId(x, y, z - 1) == Blocks.COBBLE_NETHERRACK.id()) {
            l++;
         }

         if (world.getBlockId(x, y, z + 1) == Blocks.COBBLE_NETHERRACK.id()) {
            l++;
         }

         if (world.getBlockId(x, y - 1, z) == Blocks.COBBLE_NETHERRACK.id()) {
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

         if (world.isAirBlock(x, y - 1, z)) {
            i1++;
         }

         if (l == 4 && i1 == 1) {
            world.setBlockWithNotify(x, y, z, this.blockId);
            world.scheduledUpdatesAreImmediate = true;
            Blocks.blocksList[this.blockId].updateTick(world, x, y, z, random);
            world.scheduledUpdatesAreImmediate = false;
         }

         return true;
      }
   }
}

package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

public class WorldFeatureRichScorchedDirt extends WorldFeature {
   private final int numberOfBlocks;

   @MethodParametersAnnotation(names = "numberOfBlocks")
   public WorldFeatureRichScorchedDirt(int numberOfBlocks) {
      this.numberOfBlocks = numberOfBlocks;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      for (int x1 = x - this.numberOfBlocks; x1 < x + this.numberOfBlocks; x1++) {
         for (int y1 = y - 2; y1 <= y + 2; y1++) {
            for (int z1 = z - this.numberOfBlocks; z1 < z + this.numberOfBlocks; z1++) {
               if (random.nextInt(8) == 0 && world.getBlockId(x1, y1, z1) == Blocks.DIRT_SCORCHED.id()) {
                  world.setBlock(x1, y1, z1, Blocks.DIRT_SCORCHED_RICH.id());
               }
            }
         }
      }

      return true;
   }
}

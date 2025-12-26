package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

public class WorldFeatureGrassPatch extends WorldFeature {
   private int numberOfBlocks;
   private int blockToPlace;

   @MethodParametersAnnotation(names = {"size", "blockToPlace"})
   public WorldFeatureGrassPatch(int size, int blockToPlace) {
      this.numberOfBlocks = size;
      this.blockToPlace = blockToPlace;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      float f = random.nextFloat() * (float) Math.PI;
      double d = x + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double d1 = x + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double d2 = z + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d3 = z + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d4 = y + random.nextInt(3) + 2;
      double d5 = y + random.nextInt(3) + 2;

      for (int l = 0; l <= this.numberOfBlocks; l++) {
         double d6 = d + (d1 - d) * l / this.numberOfBlocks;
         double d7 = d4 + (d5 - d4) * l / this.numberOfBlocks;
         double d8 = d2 + (d3 - d2) * l / this.numberOfBlocks;
         double d9 = random.nextDouble() * this.numberOfBlocks / 16.0;
         double d10 = (MathHelper.sin(l * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
         double d11 = (MathHelper.sin(l * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
         int i1 = MathHelper.floor(d6 - d10 / 2.0);
         int j1 = MathHelper.floor(d7 - d11 / 2.0);
         int k1 = MathHelper.floor(d8 - d10 / 2.0);
         int l1 = MathHelper.floor(d6 + d10 / 2.0);
         int i2 = MathHelper.floor(d7 + d11 / 2.0);
         int j2 = MathHelper.floor(d8 + d10 / 2.0);

         for (int k2 = i1; k2 <= l1; k2++) {
            double d12 = (k2 + 0.5 - d6) / (d10 / 2.0);
            if (!(d12 * d12 >= 1.0)) {
               for (int l2 = j1; l2 <= i2; l2++) {
                  double d13 = (l2 + 0.5 - d7) / (d11 / 2.0);
                  if (!(d12 * d12 + d13 * d13 >= 1.0)) {
                     for (int i3 = k1; i3 <= j2; i3++) {
                        double d14 = (i3 + 0.5 - d8) / (d10 / 2.0);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0
                           && (world.getBlockId(k2, l2, i3) == Blocks.DIRT.id() || world.getBlockId(k2, l2, i3) == Blocks.GRASS.id())) {
                           world.setBlockAndMetadata(k2, l2, i3, this.blockToPlace, 0);
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}

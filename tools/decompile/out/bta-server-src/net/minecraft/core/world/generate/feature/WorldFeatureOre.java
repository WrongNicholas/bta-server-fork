package net.minecraft.core.world.generate.feature;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class WorldFeatureOre extends WorldFeature {
   private int minableBlockId;
   private int numberOfBlocks;
   private final WorldFeatureOre.OreMap variantMap;

   @MethodParametersAnnotation(names = {"blockId", "numberOfBlocks"})
   public WorldFeatureOre(int blockId, int numberOfBlocks) {
      this.minableBlockId = blockId;
      this.numberOfBlocks = numberOfBlocks;
      this.variantMap = null;
   }

   @MethodParametersAnnotation(names = {"blockId", "numberOfBlocks", "variantMap"})
   public WorldFeatureOre(@NotNull WorldFeatureOre.OreMap variantMap, int numberOfBlocks) {
      this.numberOfBlocks = numberOfBlocks;
      this.variantMap = variantMap;
   }

   @Override
   public boolean place(World world, Random random, int xStart, int yStart, int zStart) {
      float f = random.nextFloat() * (float) Math.PI;
      double xMax = xStart + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double xMin = xStart + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double zMax = zStart + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double zMin = zStart + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double yMax = yStart + random.nextInt(3) + 2;
      double yMin = yStart - random.nextInt(3) + 2;

      for (int l = 0; l <= this.numberOfBlocks; l++) {
         double d6 = xMax + (xMin - xMax) * l / this.numberOfBlocks;
         double d7 = yMax + (yMin - yMax) * l / this.numberOfBlocks;
         double d8 = zMax + (zMin - zMax) * l / this.numberOfBlocks;
         double d9 = random.nextDouble() * this.numberOfBlocks / 16.0;
         double d10 = (MathHelper.sin(l * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
         double d11 = (MathHelper.sin(l * (float) Math.PI / this.numberOfBlocks) + 1.0F) * d9 + 1.0;
         int xVeinStart = MathHelper.floor(d6 - d10 / 2.0);
         int yVeinStart = MathHelper.floor(d7 - d11 / 2.0);
         int zVeinStart = MathHelper.floor(d8 - d10 / 2.0);
         int xVeinEnd = MathHelper.floor(d6 + d10 / 2.0);
         int yVeinEnd = MathHelper.floor(d7 + d11 / 2.0);
         int zVeinEnd = MathHelper.floor(d8 + d10 / 2.0);

         for (int x = xVeinStart; x <= xVeinEnd; x++) {
            double d12 = (x + 0.5 - d6) / (d10 / 2.0);
            if (!(d12 * d12 >= 1.0)) {
               for (int y = yVeinStart; y <= yVeinEnd; y++) {
                  double d13 = (y + 0.5 - d7) / (d11 / 2.0);
                  if (!(d12 * d12 + d13 * d13 >= 1.0)) {
                     for (int z = zVeinStart; z <= zVeinEnd; z++) {
                        double d14 = (z + 0.5 - d8) / (d10 / 2.0);
                        if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0) {
                           if (this.variantMap != null) {
                              int blockId = world.getBlockId(x, y, z);
                              if (blockId > 0 && this.variantMap.containsKey(blockId)) {
                                 world.setBlock(x, y, z, this.variantMap.get(blockId));
                              }
                           } else {
                              int id = world.getBlockId(x, y, z);
                              if (id == Blocks.STONE.id()
                                 || id == Blocks.COBBLE_NETHERRACK.id()
                                 || id == Blocks.BASALT.id()
                                 || id == Blocks.LIMESTONE.id()
                                 || id == Blocks.GRANITE.id()) {
                                 world.setBlock(x, y, z, this.minableBlockId);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }

   public static class OreMap {
      private int @NotNull [] keyArr = new int[0];
      private int @NotNull [] valArr = new int[0];

      private static int @NotNull [] increaseArraySize(int @NotNull [] array) {
         int[] outArr = new int[array.length + 1];
         System.arraycopy(array, 0, outArr, 0, array.length);
         return outArr;
      }

      public void put(@NotNull Block<?> stone, @NotNull Block<?> ore) {
         this.put(stone.id(), ore.id());
      }

      public void put(int stoneId, int oreId) {
         int index = this.indexForKey(stoneId);
         if (index == -1) {
            this.keyArr = increaseArraySize(this.keyArr);
            this.keyArr[this.keyArr.length - 1] = stoneId;
            this.valArr = increaseArraySize(this.valArr);
            this.valArr[this.valArr.length - 1] = oreId;
         } else {
            this.valArr[index] = oreId;
         }
      }

      public int get(@NotNull Block<?> stone) {
         return this.get(stone.id());
      }

      public int get(int stoneId) {
         int index = this.indexForKey(stoneId);
         return index == -1 ? index : this.valArr[index];
      }

      private int indexForKey(int stoneId) {
         for (int i = 0; i < this.keyArr.length; i++) {
            if (this.keyArr[i] == stoneId) {
               return i;
            }
         }

         return -1;
      }

      public boolean containsKey(@NotNull Block<?> stone) {
         return this.containsKey(stone.id());
      }

      public boolean containsKey(int stoneId) {
         for (int i = 0; i < this.keyArr.length; i++) {
            if (this.keyArr[i] == stoneId) {
               return true;
            }
         }

         return false;
      }
   }
}

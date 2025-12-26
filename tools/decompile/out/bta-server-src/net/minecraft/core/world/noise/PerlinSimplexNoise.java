package net.minecraft.core.world.noise;

import java.util.Arrays;
import java.util.Random;

public class PerlinSimplexNoise extends SurfaceNoise {
   private final SimplexNoise[] noiseLevels;
   private final int levels;

   public PerlinSimplexNoise(Random random, int levels) {
      this.levels = levels;
      this.noiseLevels = new SimplexNoise[levels];

      for (int i = 0; i < levels; i++) {
         this.noiseLevels[i] = new SimplexNoise(random);
      }
   }

   public double getMaximumValue() {
      return this.getMaximumValue(0.5);
   }

   public double getMaximumValue(double d5) {
      double max = 0.0;
      double scaleB = 1.0;

      for (int l = 0; l < this.levels; l++) {
         max += 1.0 * (0.55 / scaleB);
         scaleB *= d5;
      }

      return max;
   }

   public double[] getValue(double[] out, double x, double z, int xSize, int zSize, double xScale, double zScale, double d4) {
      return this.getValue(out, x, z, xSize, zSize, xScale, zScale, d4, 0.5);
   }

   public double[] getValue(double[] out, double x, double z, int xSize, int zSize, double xScale, double zScale, double d4, double d5) {
      xScale /= 1.5;
      zScale /= 1.5;
      if (out != null && out.length >= xSize * zSize) {
         Arrays.fill(out, 0.0);
      } else {
         out = new double[xSize * zSize];
      }

      double scaleA = 1.0;
      double scaleB = 1.0;

      for (int l = 0; l < this.levels; l++) {
         this.noiseLevels[l].add(out, x, z, xSize, zSize, xScale * scaleA, zScale * scaleA, 0.55 / scaleB);
         scaleA *= d4;
         scaleB *= d5;
      }

      return out;
   }
}

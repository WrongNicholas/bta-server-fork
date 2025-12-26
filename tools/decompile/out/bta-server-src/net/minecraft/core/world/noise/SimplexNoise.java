package net.minecraft.core.world.noise;

import java.util.Random;

public class SimplexNoise {
   private static final int[][] GRAD = new int[][]{
      {1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}
   };
   private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
   private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoise(Random random) {
      this.xo = random.nextDouble() * 256.0;
      this.yo = random.nextDouble() * 256.0;
      this.zo = random.nextDouble() * 256.0;
      int i = 0;

      while (i < 256) {
         this.p[i] = i++;
      }

      for (int ix = 0; ix < 256; ix++) {
         int k = random.nextInt(256 - ix) + ix;
         int l = this.p[ix];
         this.p[ix] = this.p[k];
         this.p[k] = l;
         this.p[ix + 256] = this.p[ix];
      }
   }

   private static int wrap(double d) {
      return d <= 0.0 ? (int)d - 1 : (int)d;
   }

   private static double dot(int[] vec, double x, double y) {
      return vec[0] * x + vec[1] * y;
   }

   public void add(double[] out, double xOffset, double zOffset, int xSize, int zSize, double xScale, double zScale, double valueScale) {
      int index = 0;

      for (int dx = 0; dx < xSize; dx++) {
         double x = (xOffset + dx) * xScale + this.xo;

         for (int dz = 0; dz < zSize; dz++) {
            double z = (zOffset + dz) * zScale + this.yo;
            double s = (x + z) * F2;
            int i = wrap(x + s);
            int j = wrap(z + s);
            double t = (i + j) * G2;
            double X0 = i - t;
            double Z0 = j - t;
            double x0 = x - X0;
            double z0 = z - Z0;
            int i1;
            int j1;
            if (x0 > z0) {
               i1 = 1;
               j1 = 0;
            } else {
               i1 = 0;
               j1 = 1;
            }

            double x1 = x0 - i1 + G2;
            double z1 = z0 - j1 + G2;
            double x2 = x0 - 1.0 + 2.0 * G2;
            double z2 = z0 - 1.0 + 2.0 * G2;
            int ib = i & 0xFF;
            int jb = j & 0xFF;
            int gi0 = this.p[ib + this.p[jb]] % 12;
            int gi1 = this.p[ib + i1 + this.p[jb + j1]] % 12;
            int gi2 = this.p[ib + 1 + this.p[jb + 1]] % 12;
            double t0 = 0.5 - x0 * x0 - z0 * z0;
            double n0;
            if (t0 < 0.0) {
               n0 = 0.0;
            } else {
               t0 *= t0;
               n0 = t0 * t0 * dot(GRAD[gi0], x0, z0);
            }

            double t1 = 0.5 - x1 * x1 - z1 * z1;
            double n1;
            if (t1 < 0.0) {
               n1 = 0.0;
            } else {
               t1 *= t1;
               n1 = t1 * t1 * dot(GRAD[gi1], x1, z1);
            }

            double t2 = 0.5 - x2 * x2 - z2 * z2;
            double n2;
            if (t2 < 0.0) {
               n2 = 0.0;
            } else {
               t2 *= t2;
               n2 = t2 * t2 * dot(GRAD[gi2], x2, z2);
            }

            double value = 70.0 * (n0 + n1 + n2);
            out[index++] += value * valueScale;
         }
      }
   }
}

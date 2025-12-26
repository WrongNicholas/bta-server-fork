package net.minecraft.core.world.noise;

import java.util.Random;

public abstract class BaseImprovedNoise {
   protected final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public BaseImprovedNoise(Random random) {
      this.xo = random.nextDouble() * 256.0;
      this.yo = random.nextDouble() * 256.0;
      this.zo = random.nextDouble() * 256.0;
      int i = 0;

      while (i < 256) {
         this.p[i] = i++;
      }

      for (int ix = 0; ix < 256; ix++) {
         int newI = random.nextInt(256 - ix) + ix;
         int temp = this.p[ix];
         this.p[ix] = this.p[newI];
         this.p[newI] = temp;
         this.p[ix + 256] = this.p[ix];
      }
   }

   public double getValue(double x, double y) {
      return this.getValue(x, y, 0.0);
   }

   public double getValue(double x, double y, double z) {
      int X = (int)((long)Math.floor(x) & 255L);
      int Y = (int)((long)Math.floor(y) & 255L);
      int Z = (int)((long)Math.floor(z) & 255L);
      x -= Math.floor(x);
      y -= Math.floor(y);
      z -= Math.floor(z);
      double u = this.fade(x);
      double v = this.fade(y);
      double w = this.fade(z);
      int A = this.p[X] + Y;
      int AA = this.p[A] + Z;
      int AB = this.p[A + 1] + Z;
      int B = this.p[X + 1] + Y;
      int BA = this.p[B] + Z;
      int BB = this.p[B + 1] + Z;
      return this.lerp(
         w,
         this.lerp(
            v,
            this.lerp(u, this.grad(this.p[AA], x, y, z), this.grad(this.p[BA], x - 1.0, y, z)),
            this.lerp(u, this.grad(this.p[AB], x, y - 1.0, z), this.grad(this.p[BB], x - 1.0, y - 1.0, z))
         ),
         this.lerp(
            v,
            this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1.0), this.grad(this.p[BA + 1], x - 1.0, y, z - 1.0)),
            this.lerp(u, this.grad(this.p[AB + 1], x, y - 1.0, z - 1.0), this.grad(this.p[BB + 1], x - 1.0, y - 1.0, z - 1.0))
         )
      );
   }

   protected double grad(int hash, double x, double y) {
      int j = hash & 15;
      double d2 = (1 - ((j & 8) >> 3)) * x;
      double d3 = j >= 4 ? (j != 12 && j != 14 ? y : x) : 0.0;
      return ((j & 1) != 0 ? -d2 : d2) + ((j & 2) != 0 ? -d3 : d3);
   }

   protected double fade(double t) {
      return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
   }

   protected double lerp(double t, double a, double b) {
      return a + t * (b - a);
   }

   protected double grad(int hash, double x, double y, double z) {
      int h = hash & 15;
      double u = h < 8 ? x : y;
      double v = h < 4 ? y : (h != 12 && h != 14 ? z : x);
      return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
   }

   public abstract void add(
      double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15, double var17
   );
}

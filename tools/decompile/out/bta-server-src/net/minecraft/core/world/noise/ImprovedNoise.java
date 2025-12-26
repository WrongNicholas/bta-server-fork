package net.minecraft.core.world.noise;

import java.util.Random;

public class ImprovedNoise extends BaseImprovedNoise {
   public ImprovedNoise(Random random) {
      super(random);
   }

   @Override
   public void add(
      double[] densityArray, double x, double y, double z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double levelScale
   ) {
      if (ySize == 1) {
         int j3 = 0;
         double d12 = 1.0 / levelScale;

         for (int i4 = 0; i4 < xSize; i4++) {
            double d14 = (x + i4) * xScale + this.xo;
            int j4 = (int)d14;
            if (d14 < j4) {
               j4--;
            }

            int k4 = j4 & 0xFF;
            d14 -= j4;
            double d17 = d14 * d14 * d14 * (d14 * (d14 * 6.0 - 15.0) + 10.0);

            for (int l4 = 0; l4 < zSize; l4++) {
               double d19 = (z + l4) * zScale + this.zo;
               int j5 = (int)d19;
               if (d19 < j5) {
                  j5--;
               }

               int l5 = j5 & 0xFF;
               d19 -= j5;
               double d21 = d19 * d19 * d19 * (d19 * (d19 * 6.0 - 15.0) + 10.0);
               int l = this.p[k4] + 0;
               int j1 = this.p[l] + l5;
               int k1 = this.p[k4 + 1] + 0;
               int l1 = this.p[k1] + l5;
               double d9 = this.lerp(d17, this.grad(this.p[j1], d14, d19), this.grad(this.p[l1], d14 - 1.0, 0.0, d19));
               double d11 = this.lerp(d17, this.grad(this.p[j1 + 1], d14, 0.0, d19 - 1.0), this.grad(this.p[l1 + 1], d14 - 1.0, 0.0, d19 - 1.0));
               double d23 = this.lerp(d21, d9, d11);
               densityArray[j3++] += d23 * d12;
            }
         }
      } else if (zSize == 1) {
         int j3 = 0;
         double d12 = 1.0 / levelScale;

         for (int i4 = 0; i4 < xSize; i4++) {
            double d14 = (x + i4) * xScale + this.xo;
            int j4 = (int)d14;
            if (d14 < j4) {
               j4--;
            }

            int k4 = j4 & 0xFF;
            d14 -= j4;
            double d17 = d14 * d14 * d14 * (d14 * (d14 * 6.0 - 15.0) + 10.0);

            for (int l4 = 0; l4 < ySize; l4++) {
               double d19 = (y + l4) * yScale + this.zo;
               int j5 = (int)d19;
               if (d19 < j5) {
                  j5--;
               }

               int l5 = j5 & 0xFF;
               d19 -= j5;
               double d21 = d19 * d19 * d19 * (d19 * (d19 * 6.0 - 15.0) + 10.0);
               int l = this.p[k4] + 0;
               int j1 = this.p[l] + l5;
               int k1 = this.p[k4 + 1] + 0;
               int l1 = this.p[k1] + l5;
               double d9 = this.lerp(d17, this.grad(this.p[j1], d14, d19), this.grad(this.p[l1], d14 - 1.0, 0.0, d19));
               double d11 = this.lerp(d17, this.grad(this.p[j1 + 1], d14, 0.0, d19 - 1.0), this.grad(this.p[l1 + 1], d14 - 1.0, 0.0, d19 - 1.0));
               double d23 = this.lerp(d21, d9, d11);
               densityArray[j3++] += d23 * d12;
            }
         }
      } else {
         int i1 = 0;
         double d7 = 1.0 / levelScale;
         int i2 = -1;
         double d13 = 0.0;
         double d15 = 0.0;
         double d16 = 0.0;
         double d18 = 0.0;

         for (int i5 = 0; i5 < xSize; i5++) {
            double d20 = (x + i5) * xScale + this.xo;
            int k5 = (int)d20;
            if (d20 < k5) {
               k5--;
            }

            int i6 = k5 & 0xFF;
            d20 -= k5;
            double d22 = d20 * d20 * d20 * (d20 * (d20 * 6.0 - 15.0) + 10.0);

            for (int j6 = 0; j6 < zSize; j6++) {
               double d24 = (z + j6) * zScale + this.zo;
               int k6 = (int)d24;
               if (d24 < k6) {
                  k6--;
               }

               int l6 = k6 & 0xFF;
               d24 -= k6;
               double d25 = d24 * d24 * d24 * (d24 * (d24 * 6.0 - 15.0) + 10.0);

               for (int i7 = 0; i7 < ySize; i7++) {
                  double d26 = (y + i7) * yScale + this.yo;
                  int j7 = (int)d26;
                  if (d26 < j7) {
                     j7--;
                  }

                  int k7 = j7 & 0xFF;
                  d26 -= j7;
                  double d27 = d26 * d26 * d26 * (d26 * (d26 * 6.0 - 15.0) + 10.0);
                  if (i7 == 0 || k7 != i2) {
                     i2 = k7;
                     int j2 = this.p[i6] + k7;
                     int k2 = this.p[j2] + l6;
                     int l2 = this.p[j2 + 1] + l6;
                     int i3 = this.p[i6 + 1] + k7;
                     int k3 = this.p[i3] + l6;
                     int l3 = this.p[i3 + 1] + l6;
                     d13 = this.lerp(d22, this.grad(this.p[k2], d20, d26, d24), this.grad(this.p[k3], d20 - 1.0, d26, d24));
                     d15 = this.lerp(d22, this.grad(this.p[l2], d20, d26 - 1.0, d24), this.grad(this.p[l3], d20 - 1.0, d26 - 1.0, d24));
                     d16 = this.lerp(d22, this.grad(this.p[k2 + 1], d20, d26, d24 - 1.0), this.grad(this.p[k3 + 1], d20 - 1.0, d26, d24 - 1.0));
                     d18 = this.lerp(d22, this.grad(this.p[l2 + 1], d20, d26 - 1.0, d24 - 1.0), this.grad(this.p[l3 + 1], d20 - 1.0, d26 - 1.0, d24 - 1.0));
                  }

                  double d28 = this.lerp(d27, d13, d15);
                  double d29 = this.lerp(d27, d16, d18);
                  double d30 = this.lerp(d25, d28, d29);
                  densityArray[i1++] += d30 * d7;
               }
            }
         }
      }
   }
}

package net.minecraft.core.world.noise;

import java.util.Random;

public class RetroImprovedNoise extends BaseImprovedNoise {
   public RetroImprovedNoise(Random random) {
      super(random);
   }

   @Override
   public void add(double[] arr, double x, double y, double z, int xSize, int ySize, int zSize, double xScale, double yScale, double zScale, double levelScale) {
      int var19 = 0;
      double var20 = 1.0 / levelScale;
      int var22 = -1;
      double var29 = 0.0;
      double var31 = 0.0;
      double var33 = 0.0;
      double var35 = 0.0;

      for (int ix = 0; ix < xSize; ix++) {
         double X = (x + ix) * xScale + this.xo;
         int Xi = (int)X;
         if (X < Xi) {
            Xi--;
         }

         int Xb = Xi & 0xFF;
         X -= Xi;
         double fadeX = X * X * X * (X * (X * 6.0 - 15.0) + 10.0);

         for (int iz = 0; iz < zSize; iz++) {
            double Z = (z + iz) * zScale + this.zo;
            int Zi = (int)Z;
            if (Z < Zi) {
               Zi--;
            }

            int Zb = Zi & 0xFF;
            Z -= Zi;
            double fadeZ = Z * Z * Z * (Z * (Z * 6.0 - 15.0) + 10.0);

            for (int iy = 0; iy < ySize; iy++) {
               double Y = (y + iy) * yScale + this.yo;
               int Yi = (int)Y;
               if (Y < Yi) {
                  Yi--;
               }

               int Yb = Yi & 0xFF;
               Y -= Yi;
               double fadeY = Y * Y * Y * (Y * (Y * 6.0 - 15.0) + 10.0);
               if (iy == 0 || Yb != var22) {
                  var22 = Yb;
                  int var64 = this.p[Xb] + Yb;
                  int var65 = this.p[var64] + Zb;
                  int var66 = this.p[var64 + 1] + Zb;
                  int var67 = this.p[Xb + 1] + Yb;
                  int var68 = this.p[var67] + Zb;
                  int var69 = this.p[var67 + 1] + Zb;
                  var29 = this.lerp(fadeX, this.grad(this.p[var65], X, Y, Z), this.grad(this.p[var68], X - 1.0, Y, Z));
                  var31 = this.lerp(fadeX, this.grad(this.p[var66], X, Y - 1.0, Z), this.grad(this.p[var69], X - 1.0, Y - 1.0, Z));
                  var33 = this.lerp(fadeX, this.grad(this.p[var65 + 1], X, Y, Z - 1.0), this.grad(this.p[var68 + 1], X - 1.0, Y, Z - 1.0));
                  var35 = this.lerp(fadeX, this.grad(this.p[var66 + 1], X, Y - 1.0, Z - 1.0), this.grad(this.p[var69 + 1], X - 1.0, Y - 1.0, Z - 1.0));
               }

               double var58 = this.lerp(fadeY, var29, var31);
               double var60 = this.lerp(fadeY, var33, var35);
               double var62 = this.lerp(fadeZ, var58, var60);
               int var10001 = var19++;
               arr[var10001] += var62 * var20;
            }
         }
      }
   }
}

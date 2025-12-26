package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureTreeEucalyptus extends WorldFeature {
   Random random;
   World world;
   int[] origin = new int[]{0, 0, 0};
   int height;
   int trunkHeight;
   double trunkHeightScale;
   double branchDensity;
   double branchSlop;
   double widthScale;
   double foliageDensity;
   int trunkWidth;
   int heightVariance;
   int foliageHeight;
   int[][] foliageCoords;
   protected int leavesID;
   protected int logID;
   static final byte[] axisConversionArray = new byte[]{2, 0, 0, 1, 2, 1};

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureTreeEucalyptus(int leavesID, int logID) {
      this.random = new Random();
      this.height = 0;
      this.trunkHeightScale = 0.381;
      this.branchDensity = 1.0;
      this.branchSlop = 0.381;
      this.widthScale = 1.0;
      this.foliageDensity = 1.0;
      this.trunkWidth = 1;
      this.heightVariance = 20;
      this.foliageHeight = 10;
      this.leavesID = leavesID;
      this.logID = logID;
   }

   void prepare() {
      this.trunkHeight = (int)(this.height * this.trunkHeightScale);
      if (this.trunkHeight >= this.height) {
         this.trunkHeight = this.height - 1;
      }

      int i = (int)(1.381 + Math.pow(this.foliageDensity * this.height / 7.0, 2.0));
      if (i < 1) {
         i = 1;
      }

      int[][] ai = new int[i * this.height][4];
      int j = this.origin[1] + this.height - this.foliageHeight;
      int k = 1;
      int l = this.origin[1] + this.trunkHeight;
      int i1 = j - this.origin[1];
      ai[0][0] = this.origin[0];
      ai[0][1] = j;
      ai[0][2] = this.origin[2];
      ai[0][3] = l;
      j--;

      while (i1 >= 0) {
         int j1 = 0;
         float treeShape = this.treeShape(i1);
         if (treeShape < 0.0F) {
            j--;
            i1--;
         } else {
            for (double d = 0.5; j1 < i; j1++) {
               double d1 = this.widthScale * (treeShape * (this.random.nextFloat() + 0.328));
               double d2 = this.random.nextFloat() * 2.0 * 3.14159;
               int k1 = MathHelper.floor(d1 * Math.sin(d2) + this.origin[0] + d);
               int l1 = MathHelper.floor(d1 * Math.cos(d2) + this.origin[2] + d);
               int[] ai1 = new int[]{k1, j, l1};
               int[] ai2 = new int[]{k1, j + this.foliageHeight, l1};
               if (this.checkLine(ai1, ai2) == -1) {
                  int[] ai3 = new int[]{this.origin[0], this.origin[1], this.origin[2]};
                  double d3 = Math.sqrt(Math.pow(Math.abs(this.origin[0] - ai1[0]), 2.0) + Math.pow(Math.abs(this.origin[2] - ai1[2]), 2.0));
                  double d4 = d3 * this.branchSlop;
                  if (ai1[1] - d4 > l) {
                     ai3[1] = l;
                  } else {
                     ai3[1] = (int)(ai1[1] - d4);
                  }

                  if (this.checkLine(ai3, ai1) == -1) {
                     ai[k][0] = k1;
                     ai[k][1] = j;
                     ai[k][2] = l1;
                     ai[k][3] = ai3[1];
                     k++;
                  }
               }
            }

            j--;
            i1--;
         }
      }

      this.foliageCoords = new int[k][4];
      System.arraycopy(ai, 0, this.foliageCoords, 0, k);
   }

   void crossSection(int x, int y, int z, float f, byte byte0, int l) {
      int i1 = (int)(f + 0.618);
      byte byte1 = axisConversionArray[byte0];
      byte byte2 = axisConversionArray[byte0 + 3];
      int[] ai = new int[]{x, y, z};
      int[] ai1 = new int[]{0, 0, 0};
      int j1 = -i1;
      int k1 = -i1;

      for (ai1[byte0] = ai[byte0]; j1 <= i1; j1++) {
         ai1[byte1] = ai[byte1] + j1;
         int l1 = -i1;

         while (l1 <= i1) {
            double d = Math.sqrt(Math.pow(Math.abs(j1) + 0.5, 4.0) + Math.pow(Math.abs(l1) + 0.5, 4.0));
            if (d > f) {
               l1++;
            } else {
               ai1[byte2] = ai[byte2] + l1;
               int i2 = this.world.getBlockId(ai1[0], ai1[1], ai1[2]);
               if (i2 != 0 && i2 != this.leavesID) {
                  l1++;
               } else {
                  this.world.setBlockWithNotify(ai1[0], ai1[1], ai1[2], l);
                  l1++;
               }
            }
         }
      }
   }

   float treeShape(int i) {
      if (i < this.height * 0.3) {
         return -1.618F;
      } else {
         float f = this.height / 2.0F;
         float f1 = this.height / 2.0F - i;
         float f2;
         if (f1 == 0.0F) {
            f2 = f;
         } else if (Math.abs(f1) >= f) {
            f2 = 0.0F;
         } else {
            f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2.0) - Math.pow(Math.abs(f1), 2.0));
         }

         return f2 * 0.5F;
      }
   }

   float foliageShape(int i) {
      if (i >= 0 && i < this.foliageHeight) {
         return i != 0 && i != this.foliageHeight - 1 ? 3.0F : 2.0F;
      } else {
         return -1.0F;
      }
   }

   void foliageCluster(int x, int y, int z) {
      int l = y;

      for (int i1 = y + this.foliageHeight; l < i1; l++) {
         this.crossSection(x, l, z, this.foliageShape(l - y), (byte)1, this.leavesID);
      }
   }

   void makeFoliage() {
      int i = 0;

      for (int j = this.foliageCoords.length; i < j; i++) {
         int x = this.foliageCoords[i][0];
         int y = this.foliageCoords[i][1];
         int z = this.foliageCoords[i][2];
         this.foliageCluster(x, y, z);
      }
   }

   void limb(int[] ai, int[] ai1, int i) {
      int[] ai2 = new int[]{0, 0, 0};
      byte byte0 = 0;

      int j;
      for (j = 0; byte0 < 3; byte0++) {
         ai2[byte0] = ai1[byte0] - ai[byte0];
         if (Math.abs(ai2[byte0]) > Math.abs(ai2[j])) {
            j = byte0;
         }
      }

      if (ai2[j] != 0) {
         byte byte1 = axisConversionArray[j];
         byte byte2 = axisConversionArray[j + 3];
         byte byte3;
         if (ai2[j] > 0) {
            byte3 = 1;
         } else {
            byte3 = -1;
         }

         double d = (double)ai2[byte1] / ai2[j];
         double d1 = (double)ai2[byte2] / ai2[j];
         int[] ai3 = new int[]{0, 0, 0};
         int k = 0;

         for (int l = ai2[j] + byte3; k != l; k += byte3) {
            ai3[j] = MathHelper.floor(ai[j] + k + 0.5);
            ai3[byte1] = MathHelper.floor(ai[byte1] + k * d + 0.5);
            ai3[byte2] = MathHelper.floor(ai[byte2] + k * d1 + 0.5);
            this.world.setBlockWithNotify(ai3[0], ai3[1], ai3[2], i);
         }
      }
   }

   boolean trimBranches(int i) {
      return i >= this.height * 0.2;
   }

   void makeTrunk() {
      int x = this.origin[0];
      int y = this.origin[1];
      int yTop = this.origin[1] + this.trunkHeight;
      int z = this.origin[2];
      int[] posBottom = new int[]{x, y, z};
      int[] posTop = new int[]{x, yTop, z};
      this.limb(posBottom, posTop, this.logID);
      if (this.trunkWidth == 2) {
         posBottom[0]++;
         posTop[0]++;
         this.limb(posBottom, posTop, this.logID);
         posBottom[2]++;
         posTop[2]++;
         this.limb(posBottom, posTop, this.logID);
         posBottom[0]--;
         posTop[0]--;
         this.limb(posBottom, posTop, this.logID);
      }
   }

   void makeBranches() {
      int i = 0;
      int j = this.foliageCoords.length;

      for (int[] ai = new int[]{this.origin[0], this.origin[1], this.origin[2]}; i < j; i++) {
         int[] ai1 = this.foliageCoords[i];
         int[] ai2 = new int[]{ai1[0], ai1[1], ai1[2]};
         ai[1] = ai1[3];
         int k = ai[1] - this.origin[1];
         if (this.trimBranches(k)) {
            this.limb(ai, ai2, this.logID);
         }
      }
   }

   int checkLine(int[] ai, int[] ai1) {
      int[] ai2 = new int[]{0, 0, 0};
      byte byte0 = 0;

      int i;
      for (i = 0; byte0 < 3; byte0++) {
         ai2[byte0] = ai1[byte0] - ai[byte0];
         if (Math.abs(ai2[byte0]) > Math.abs(ai2[i])) {
            i = byte0;
         }
      }

      if (ai2[i] == 0) {
         return -1;
      } else {
         byte byte1 = axisConversionArray[i];
         byte byte2 = axisConversionArray[i + 3];
         byte byte3;
         if (ai2[i] > 0) {
            byte3 = 1;
         } else {
            byte3 = -1;
         }

         double d = (double)ai2[byte1] / ai2[i];
         double d1 = (double)ai2[byte2] / ai2[i];
         int[] ai3 = new int[]{0, 0, 0};
         int j = 0;

         int k;
         for (k = ai2[i] + byte3; j != k; j += byte3) {
            ai3[i] = ai[i] + j;
            ai3[byte1] = MathHelper.floor(ai[byte1] + j * d);
            ai3[byte2] = MathHelper.floor(ai[byte2] + j * d1);
            int l = this.world.getBlockId(ai3[0], ai3[1], ai3[2]);
            if (l != 0 && l != this.leavesID) {
               break;
            }
         }

         return j == k ? -1 : Math.abs(j);
      }
   }

   boolean checkLocation() {
      int[] ai = new int[]{this.origin[0], this.origin[1], this.origin[2]};
      int[] ai1 = new int[]{this.origin[0], this.origin[1] + this.height - 1, this.origin[2]};
      int idBelow = this.world.getBlockId(this.origin[0], this.origin[1] - 1, this.origin[2]);
      if (!Blocks.hasTag(idBelow, BlockTags.GROWS_TREES)) {
         return false;
      } else {
         int j = this.checkLine(ai, ai1);
         if (j == -1) {
            return true;
         } else if (j < 6) {
            return false;
         } else {
            this.height = j;
            return true;
         }
      }
   }

   @Override
   public void init(double d, double d1, double d2) {
      this.heightVariance = (int)(d * 20.0);
      if (d > 0.5) {
         this.foliageHeight = 5;
      }

      this.widthScale = d1;
      this.foliageDensity = d2;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      this.world = world;
      long l = random.nextLong();
      this.random.setSeed(l);
      this.origin[0] = x;
      this.origin[1] = y;
      this.origin[2] = z;
      if (this.height == 0) {
         this.height = 12 + this.random.nextInt(4);
      }

      if (!this.checkLocation()) {
         return false;
      } else {
         WorldFeatureTree.onTreeGrown(this.world, this.origin[0], this.origin[1], this.origin[2]);
         this.prepare();
         this.makeFoliage();
         this.makeTrunk();
         this.makeBranches();
         return true;
      }
   }
}

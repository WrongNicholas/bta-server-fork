package net.minecraft.core.world.generate.feature.tree;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.WorldFeature;

public class WorldFeatureCherryTreeFancy extends WorldFeature {
   protected int leavesID;
   protected int logID;
   static final byte[] axisConversionArray = new byte[]{2, 0, 0, 1, 2, 1};
   Random rnd;
   World world;
   int[] origin = new int[]{0, 0, 0};
   int height;
   int trunkHeight;
   double trunkHeightScale;
   double branchDensity;
   double branchSlope;
   double widthScale;
   double foliageDensity;
   int trunkWidth;
   int heightVariance;
   int foliageHeight;
   int[][] foliageCoords;
   int heightMod;

   @MethodParametersAnnotation(names = {"leavesID", "logID"})
   public WorldFeatureCherryTreeFancy(int leavesID, int logID) {
      this(leavesID, logID, 0);
   }

   @MethodParametersAnnotation(names = {"leavesID", "logID", "heightMod"})
   public WorldFeatureCherryTreeFancy(int leavesID, int logID, int heightMod) {
      this.rnd = new Random();
      this.height = 0;
      this.trunkHeightScale = 0.6;
      this.branchDensity = 1.0;
      this.branchSlope = 0.4;
      this.widthScale = 1.0;
      this.foliageDensity = 1.0;
      this.trunkWidth = 1;
      this.heightVariance = 12;
      this.foliageHeight = 4;
      this.leavesID = leavesID;
      this.logID = logID;
      this.heightMod = heightMod;
   }

   void prepare() {
      this.trunkHeight = (int)(this.height * this.trunkHeightScale);
      if (this.trunkHeight >= this.height) {
         this.trunkHeight = this.height - 1;
      }

      int numOfClustersPerY = (int)(1.4 + Math.pow(this.foliageDensity * this.height / 13.0, 2.0));
      if (numOfClustersPerY < 1) {
         numOfClustersPerY = 1;
      }

      int[][] foliageCoords = new int[numOfClustersPerY * this.height][4];
      int yEnd = this.origin[1] + this.height - this.foliageHeight;
      int foliageCoordsIndex = 1;
      int topY = this.origin[1] + this.trunkHeight;
      int y = yEnd - this.origin[1];
      foliageCoords[0][0] = this.origin[0];
      foliageCoords[0][1] = yEnd;
      foliageCoords[0][2] = this.origin[2];
      foliageCoords[0][3] = topY;
      yEnd--;

      while (y >= 0) {
         float shapeFac = this.shapeFunc(y);
         if (shapeFac < 0.0F) {
            yEnd--;
            y--;
         } else {
            double offset = 0.5;

            for (int i = 0; i < numOfClustersPerY; i++) {
               double r = this.widthScale * (shapeFac * (this.rnd.nextFloat() + 0.328));
               double theta = this.rnd.nextFloat() * 2.0 * Math.PI;
               int x = MathHelper.floor(r * Math.sin(theta) + this.origin[0] + offset);
               int z = MathHelper.floor(r * Math.cos(theta) + this.origin[2] + offset);
               int[] branchEnd = new int[]{x, yEnd, z};
               int[] branchTopOfTree = new int[]{x, yEnd + this.foliageHeight, z};
               if (this.distToMat(branchEnd, branchTopOfTree) == -1) {
                  int[] branchStart = new int[]{this.origin[0], this.origin[1], this.origin[2]};
                  double dist = Math.sqrt(Math.pow(Math.abs(this.origin[0] - branchEnd[0]), 2.0) + Math.pow(Math.abs(this.origin[2] - branchEnd[2]), 2.0));
                  if (branchEnd[1] - dist * this.branchSlope > topY) {
                     branchStart[1] = topY;
                  } else {
                     branchStart[1] = (int)(branchEnd[1] - dist * this.branchSlope);
                  }

                  if (this.distToMat(branchStart, branchEnd) == -1) {
                     foliageCoords[foliageCoordsIndex][0] = x;
                     foliageCoords[foliageCoordsIndex][1] = yEnd;
                     foliageCoords[foliageCoordsIndex][2] = z;
                     foliageCoords[foliageCoordsIndex][3] = branchStart[1];
                     foliageCoordsIndex++;
                  }
               }
            }

            yEnd--;
            y--;
         }
      }

      this.foliageCoords = new int[foliageCoordsIndex][4];
      System.arraycopy(foliageCoords, 0, this.foliageCoords, 0, foliageCoordsIndex);
   }

   void crossSection(int x, int y, int z, float radius, int blockId, Random random) {
      int rad = (int)(radius + 0.618);
      int[] center = new int[]{x, y, z};
      int[] coord = new int[]{0, 0, 0};
      coord[1] = center[1];

      for (int off1 = -rad; off1 <= rad; off1++) {
         coord[0] = center[0] + off1;

         for (int off2 = -rad; off2 <= rad; off2++) {
            coord[2] = center[2] + off2;
            double thisDist = Math.sqrt(Math.pow(Math.abs(off1) + 0.5, 2.0) + Math.pow(Math.abs(off2) + 0.5, 2.0));
            if (thisDist <= radius) {
               int id = this.world.getBlockId(coord[0], coord[1], coord[2]);
               if (id == 0 || id == this.leavesID || id == Blocks.LEAVES_CHERRY_FLOWERING.id()) {
                  this.world.setBlockWithNotify(coord[0], coord[1], coord[2], random.nextInt(5) == 0 ? Blocks.LEAVES_CHERRY_FLOWERING.id() : blockId);
               }
            }
         }
      }
   }

   float shapeFunc(int y) {
      if (y < this.height * 0.3) {
         return -1.618F;
      } else {
         float radius = this.height / 2.0F;
         float adj = this.height / 2.0F - y;
         float dist;
         if (adj == 0.0F) {
            dist = radius;
         } else if (Math.abs(adj) >= radius) {
            dist = 0.0F;
         } else {
            dist = (float)Math.sqrt(Math.pow(Math.abs(radius), 2.0) - Math.pow(Math.abs(adj), 2.0));
         }

         return dist * 0.5F;
      }
   }

   float getFoliageShape(int y) {
      if (y < 0 || y >= this.foliageHeight) {
         return -1.0F;
      } else {
         return y != 0 && y != this.foliageHeight - 1 ? 3.0F : 2.0F;
      }
   }

   void foliageCluster(int x, int y, int z, Random random) {
      for (int leavesY = y; leavesY < y + this.foliageHeight; leavesY++) {
         float radius = this.getFoliageShape(leavesY - y);
         this.crossSection(x, leavesY, z, radius, this.leavesID, random);
      }
   }

   void makeFoliage(Random random) {
      for (int[] foliageCoord : this.foliageCoords) {
         int x = foliageCoord[0];
         int y = foliageCoord[1];
         int z = foliageCoord[2];
         this.foliageCluster(x, y, z, random);
      }
   }

   void makeBranch(int[] startPos, int[] endPos, int blockId) {
      int[] dimensions = new int[]{0, 0, 0};
      int dim0 = 0;

      for (int i = 0; i < 3; i++) {
         dimensions[i] = endPos[i] - startPos[i];
         if (Math.abs(dimensions[i]) > Math.abs(dimensions[dim0])) {
            dim0 = i;
         }
      }

      if (dimensions[dim0] != 0) {
         byte dim1 = axisConversionArray[dim0];
         byte dim2 = axisConversionArray[dim0 + 3];
         byte delta;
         if (dimensions[dim0] > 0) {
            delta = 1;
         } else {
            delta = -1;
         }

         double dim1DeltaScale = (double)dimensions[dim1] / dimensions[dim0];
         double dim2DeltaScale = (double)dimensions[dim2] / dimensions[dim0];

         for (int ix = 0; ix != dimensions[dim0] + delta; ix += delta) {
            int[] pos = new int[]{0, 0, 0};
            pos[dim0] = MathHelper.floor((double)startPos[dim0] + ix + 0.5);
            pos[dim1] = MathHelper.floor(startPos[dim1] + ix * dim1DeltaScale + 0.5);
            pos[dim2] = MathHelper.floor(startPos[dim2] + ix * dim2DeltaScale + 0.5);
            this.world.setBlockWithNotify(pos[0], pos[1], pos[2], blockId);
         }
      }
   }

   boolean isHighEnoughToBranch(int i) {
      return i >= this.height * 0.2;
   }

   void makeTrunk() {
      int x = this.origin[0];
      int minY = this.origin[1];
      int maxY = this.origin[1] + this.trunkHeight;
      int z = this.origin[2];
      int[] startPos = new int[]{x, minY, z};
      int[] endPos = new int[]{x, maxY, z};
      this.makeBranch(startPos, endPos, this.logID);
      if (this.trunkWidth == 2) {
         startPos[0]++;
         endPos[0]++;
         this.makeBranch(startPos, endPos, this.logID);
         startPos[2]++;
         endPos[2]++;
         this.makeBranch(startPos, endPos, this.logID);
         startPos[0]--;
         endPos[0]--;
         this.makeBranch(startPos, endPos, this.logID);
      }
   }

   void makeBranches() {
      int[] startPos = new int[]{this.origin[0], this.origin[1], this.origin[2]};

      for (int[] branchPoint : this.foliageCoords) {
         int[] endPos = new int[]{branchPoint[0], branchPoint[1], branchPoint[2]};
         startPos[1] = branchPoint[3];
         int dy = startPos[1] - this.origin[1];
         if (this.isHighEnoughToBranch(dy)) {
            this.makeBranch(startPos, endPos, this.logID);
         }
      }
   }

   int distToMat(int[] startPos, int[] endPos) {
      int[] dimensions = new int[]{0, 0, 0};
      int dim0 = 0;

      for (byte i = 0; i < 3; i++) {
         dimensions[i] = endPos[i] - startPos[i];
         if (Math.abs(dimensions[i]) > Math.abs(dimensions[dim0])) {
            dim0 = i;
         }
      }

      if (dimensions[dim0] == 0) {
         return -1;
      } else {
         byte dim1 = axisConversionArray[dim0];
         byte dim2 = axisConversionArray[dim0 + 3];
         byte delta;
         if (dimensions[dim0] > 0) {
            delta = 1;
         } else {
            delta = -1;
         }

         double dim1DeltaScale = (double)dimensions[dim1] / dimensions[dim0];
         double dim2DeltaScale = (double)dimensions[dim2] / dimensions[dim0];
         int linePos = 0;

         int lineLength;
         for (lineLength = dimensions[dim0] + delta; linePos != lineLength; linePos += delta) {
            int[] pos = new int[]{0, 0, 0};
            pos[dim0] = startPos[dim0] + linePos;
            pos[dim1] = MathHelper.floor(startPos[dim1] + linePos * dim1DeltaScale);
            pos[dim2] = MathHelper.floor(startPos[dim2] + linePos * dim2DeltaScale);
            int idAtPos = this.world.getBlockId(pos[0], pos[1], pos[2]);
            if (idAtPos != 0 && idAtPos != this.leavesID && idAtPos != Blocks.LEAVES_CHERRY_FLOWERING.id()) {
               break;
            }
         }

         return linePos == lineLength ? -1 : Math.abs(linePos);
      }
   }

   boolean canGenerateTree() {
      int[] bottomPos = new int[]{this.origin[0], this.origin[1], this.origin[2]};
      int[] topPos = new int[]{this.origin[0], this.origin[1] + this.height - 1, this.origin[2]};
      int blockIdUnderneath = this.world.getBlockId(this.origin[0], this.origin[1] - 1, this.origin[2]);
      if (!Blocks.hasTag(blockIdUnderneath, BlockTags.GROWS_TREES)) {
         return false;
      } else {
         int interruptedTrunkHeight = this.distToMat(bottomPos, topPos);
         if (interruptedTrunkHeight == -1) {
            return true;
         } else if (interruptedTrunkHeight < 6) {
            return false;
         } else {
            this.height = interruptedTrunkHeight;
            return true;
         }
      }
   }

   @Override
   public void init(double d, double d1, double d2) {
      this.heightVariance = (int)(d * (12.0 + this.heightMod));
      if (d > 0.5) {
         this.foliageHeight = 5;
      }

      this.widthScale = d1;
      this.foliageDensity = d2;
   }

   @Override
   public boolean place(World world, Random random, int x, int y, int z) {
      this.world = world;
      long seed = random.nextLong();
      this.rnd.setSeed(seed);
      this.origin[0] = x;
      this.origin[1] = y;
      this.origin[2] = z;
      if (this.height == 0) {
         this.height = 5 + this.rnd.nextInt(this.heightVariance);
      }

      if (this.canGenerateTree()) {
         WorldFeatureTree.onTreeGrown(this.world, this.origin[0], this.origin[1], this.origin[2]);
         this.prepare();
         this.makeFoliage(random);
         this.makeTrunk();
         this.makeBranches();
         return true;
      } else {
         return false;
      }
   }
}

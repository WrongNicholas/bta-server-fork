package net.minecraft.core.world.generate.feature.tree.spooner;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public abstract class WorldFeatureSpoonerTreeProcedural extends WorldFeatureSpoonerTree {
   protected static double TRUNK_THICKNESS = 1.0;
   protected static double BRANCH_DENSITY = 1.0;
   protected static double FOLIAGE_DENSITY = 1.0;
   protected static double TRUNK_HEIGHT = 0.7;
   protected static double HEIGHT = 25.0;
   protected double trunkHeight = 0.0;
   protected double trunkRadius = 0.0;
   protected double branchDensity = 0.0;
   protected double branchSlope = 0.0;
   protected double[] foliageShape = null;
   protected List<int[]> foliageCoords = null;

   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeProcedural(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   protected void crossSection(int[] center, double radius, int dirAxis, int id, int data, World world) {
      int rad = MathHelper.floor(radius + 0.618);
      int secidx1 = (dirAxis + 1) % 3;
      int secidx2 = (dirAxis + 2) % 3;
      int[] coord = new int[3];

      for (int off1 = -rad; off1 < rad + 1; off1++) {
         for (int off2 = -rad; off2 < rad + 1; off2++) {
            double thisDist = Math.sqrt(Math.pow(Math.abs(off1) + 0.5, 2.0) + Math.pow(Math.abs(off2) + 0.5, 2.0));
            if (!(thisDist > radius)) {
               int pri = center[dirAxis];
               int sec1 = center[secidx1] + off1;
               int sec2 = center[secidx2] + off2;
               coord[dirAxis] = pri;
               coord[secidx1] = sec1;
               coord[secidx2] = sec2;
               this.assignValue(coord[0], coord[1], coord[2], id, data, world);
            }
         }
      }
   }

   protected Double shapeFunc(int y) {
      return this.random.nextDouble() < 100.0 / Math.pow(this.height, 2.0) && y < this.trunkHeight ? this.height * 0.12 : null;
   }

   protected void foliageCluster(int[] center, World world) {
      double[] levelRadius = this.foliageShape;
      int x = center[0];
      int y = center[1];
      int z = center[2];

      for (double i : levelRadius) {
         this.crossSection(new int[]{x, y, z}, i, 1, this.leavesId, this.leavesData, world);
         y++;
      }
   }

   protected void taperedLimb(int[] start, int[] end, double startSize, double endSize, World world) {
      int[] delta = new int[]{end[0] - start[0], end[1] - start[1], end[2] - start[2]};
      int maxDist = MathHelper.maxAbs(delta[0], MathHelper.maxAbs(delta[1], delta[2]));
      if (maxDist != 0) {
         int primidx = 0;

         for (int i = 0; i < 3; i++) {
            if (delta[i] == maxDist) {
               primidx = i;
               break;
            }
         }

         int secidx1 = (primidx - 1) % 3;
         if (secidx1 < 0) {
            secidx1 += 3;
         }

         int secidx2 = (1 + primidx) % 3;
         int primSign = delta[primidx] / Math.abs(delta[primidx]);
         int secdelta1 = delta[secidx1];
         float secfac1 = (float)secdelta1 / delta[primidx];
         int secdelta2 = delta[secidx2];
         float secfac2 = (float)secdelta2 / delta[primidx];
         int[] coord = new int[3];
         int endOffset = delta[primidx] + primSign;
         int endOffsetAbs = Math.abs(endOffset);
         int iterator = Math.abs(primSign);

         for (int primOffsetAbs = 0; primOffsetAbs < endOffsetAbs; primOffsetAbs += iterator) {
            int primOffset = primOffsetAbs * primSign;
            int primLoc = start[primidx] + primOffset;
            int secloc1 = (int)(start[secidx1] + primOffset * secfac1);
            int secloc2 = (int)(start[secidx2] + primOffset * secfac2);
            coord[primidx] = primLoc;
            coord[secidx1] = secloc1;
            coord[secidx2] = secloc2;
            int primDist = Math.abs(delta[primidx]);
            double radius = endSize + (startSize - endSize) * Math.abs(delta[primidx] - primOffset) / primDist;
            this.crossSection(coord, radius, primidx, this.trunkId, this.trunkData, world);
         }
      }
   }

   @Override
   protected void makeFoliage(World world) {
      List<int[]> foliageCoords = this.foliageCoords;
      if (this.foliageCoords != null) {
         for (int[] coord : foliageCoords) {
            this.foliageCluster(coord, world);
         }

         for (int[] coord : foliageCoords) {
            this.assignValue(coord[0], coord[1], coord[2], this.trunkId, this.trunkData, world);
         }
      }
   }

   protected void makeBranches(World world) {
      int[] treePosition = this.pos;
      int height = this.height;
      int topY = treePosition[1] + (int)(this.trunkHeight + 0.5);
      double endRad = this.trunkRadius * (1.0 - this.trunkHeight / height);
      if (endRad < 1.0) {
         endRad = 1.0;
      }

      for (int[] coord : this.foliageCoords) {
         double dist = Math.sqrt(Math.pow(coord[0] - treePosition[0], 2.0) + Math.pow(coord[2] - treePosition[2], 2.0));
         int posY = coord[1];
         double slope = this.branchSlope + (0.5 - this.random.nextDouble()) * 0.16;
         double branchY;
         double baseSize;
         if (coord[1] - dist * slope > topY) {
            branchY = topY;
            baseSize = endRad;
         } else {
            branchY = posY - dist * slope;
            baseSize = endRad + (this.trunkRadius - endRad) * (topY - branchY) / this.trunkHeight;
         }

         double startSize = baseSize * (1.0 + this.random.nextDouble()) * 0.618 * Math.pow(dist / height, 0.618);
         double rndr = Math.sqrt(this.random.nextDouble()) * baseSize * 0.618;
         double rndang = Math.random() * 2.0 * Math.PI;
         int rndx = (int)(rndr * Math.sin(rndang) + 0.5);
         int rndz = (int)(rndr * Math.cos(rndang) + 0.5);
         int[] startCoord = new int[]{treePosition[0] + rndx, (int)branchY, treePosition[2] + rndz};
         if (startSize < 1.0) {
            startSize = 1.0;
         }

         double endSize = 1.0;
         this.taperedLimb(startCoord, coord, startSize, endSize, world);
      }
   }

   void makeRoots(List<double[]> rootBases, World world) {
      int[] treePosition = this.pos;
      int height = this.height;

      for (int[] coord : this.foliageCoords) {
         double dist = Math.sqrt(Math.pow(coord[0] - treePosition[0], 2.0) + Math.pow(coord[2] - treePosition[2], 2.0));
         int yDist = coord[1] - treePosition[1];
         double value = this.branchDensity * 220.0 * height / Math.pow(yDist + dist, 3.0);
         if (!(value < this.random.nextDouble())) {
            double[] rootBase = rootBases.get(this.random.nextInt(rootBases.size()));
            int rootX = (int)rootBase[0];
            int rootZ = (int)rootBase[1];
            double rootBaseRadius = rootBase[2];
            double rndr = Math.sqrt(this.random.nextDouble()) * rootBaseRadius * 0.618;
            double rndang = this.random.nextDouble() * 2.0 * Math.PI;
            int rndx = (int)(rndr * Math.sin(rndang) + 0.5);
            int rndz = (int)(rndr * Math.cos(rndang) + 0.5);
            int rndy = (int)(this.random.nextDouble() * rootBaseRadius * 0.5);
            int[] startCoord = new int[]{rootX + rndx, treePosition[1] + rndy, rootZ + rndz};
            int[] offset = new int[]{startCoord[0] - coord[0], startCoord[1] - coord[1], startCoord[2] - coord[2]};
            if (this instanceof WorldFeatureSpoonerTreeMangrove) {
               offset = new int[]{(int)(offset[0] * 1.618 - 1.5), (int)(offset[1] * 1.618 - 1.5), (int)(offset[2] * 1.618 - 1.5)};
            }

            int[] endCoord = new int[]{startCoord[0] + offset[0], startCoord[1] + offset[1], startCoord[2] + offset[2]};
            double rootStartSize = rootBaseRadius * 0.618 * Math.abs(offset[1]) / (height * 0.618);
            if (rootStartSize < 1.0) {
               rootStartSize = 1.0;
            }

            double endSize = 1.0;
            this.taperedLimb(startCoord, endCoord, rootStartSize, endSize, world);
         }
      }
   }

   @Override
   protected void makeTrunk(World world) {
      int height = this.height;
      double trunkHeight = this.trunkHeight;
      double trunkRadius = this.trunkRadius;
      int[] treePosition = this.pos;
      int startY = treePosition[1];
      int midY = treePosition[1] + (int)(trunkHeight * 0.382);
      int topY = treePosition[1] + (int)(trunkHeight + 0.5);
      int x = treePosition[0];
      int z = treePosition[2];
      double midRad = trunkRadius * 0.8;
      double endRad = trunkRadius * (1.0 - trunkHeight / height);
      if (endRad < 1.0) {
         endRad = 1.0;
      }

      if (midRad < endRad) {
         midRad = endRad;
      }

      double startRad;
      if (this instanceof WorldFeatureSpoonerTreeMangrove) {
         startRad = trunkRadius * 0.8;
         List<double[]> rootBases = new ArrayList<>();
         rootBases.add(new double[]{x, z, startRad});
         double buttressRadius = trunkRadius * 0.382;
         double posRadius = trunkRadius;
         if (this instanceof WorldFeatureSpoonerTreeMangrove) {
            posRadius = trunkRadius * 2.618;
         }

         int numOfButtresses = (int)(Math.sqrt(trunkRadius) + 3.5);

         for (int i = 0; i < numOfButtresses; i++) {
            double rndang = this.random.nextDouble() * 2.0 * Math.PI;
            double thisPosRadius = posRadius * (0.9 + this.random.nextDouble() * 0.2);
            int thisX = x + (int)(thisPosRadius * Math.sin(rndang));
            int thisZ = z + (int)(thisPosRadius * Math.cos(rndang));
            double thisButtressRadius = buttressRadius * (0.618 * this.random.nextDouble());
            if (thisButtressRadius < 1.0) {
               thisButtressRadius = 1.0;
            }

            this.taperedLimb(new int[]{thisX, startY, thisZ}, new int[]{x, midY, z}, thisButtressRadius, thisButtressRadius, world);
            rootBases.add(new double[]{thisX, thisZ, thisButtressRadius});
         }
      } else {
         startRad = trunkRadius;
         List<double[]> rootBases = new ArrayList<>();
         rootBases.add(new double[]{x, z, trunkRadius});
      }

      this.taperedLimb(new int[]{x, startY, z}, new int[]{x, midY, z}, startRad, midRad, world);
      this.taperedLimb(new int[]{x, midY, z}, new int[]{x, topY, z}, midRad, endRad, world);
      this.makeBranches(world);
   }

   @Override
   protected void prepare(World world) {
      this.trunkRadius = Math.sqrt(this.height * TRUNK_THICKNESS);
      if (this.trunkRadius < 1.0) {
         this.trunkRadius = 1.0;
      }

      this.trunkHeight = this.height * 0.618;
      this.branchDensity = BRANCH_DENSITY / FOLIAGE_DENSITY;
      List<int[]> foliageCoords = new ArrayList<>();
      int yStart = this.pos[1];
      int yEnd = this.pos[1] + this.height;
      int numOfClustersPerY = (int)(1.5 + Math.pow(FOLIAGE_DENSITY * this.height / 19.0, 2.0));
      if (numOfClustersPerY < 1) {
         numOfClustersPerY = 1;
      }

      for (int y = yEnd; y > yStart; y--) {
         for (int i = 0; i < numOfClustersPerY; i++) {
            Double shapefac = this.shapeFunc(y - yStart);
            if (shapefac != null) {
               double r = (Math.sqrt(this.random.nextDouble()) + 0.328) * shapefac;
               double theta = this.random.nextDouble() * 2.0 * Math.PI;
               int x = (int)(r * Math.sin(theta)) + this.pos[0];
               int z = (int)(r * Math.cos(theta)) + this.pos[2];
               foliageCoords.add(new int[]{x, y, z});
            }
         }
      }

      this.foliageCoords = foliageCoords;
   }
}

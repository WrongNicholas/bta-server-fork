package net.minecraft.core.world.generate.feature.tree;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;
import net.minecraft.core.world.generate.feature.tree.spooner.WorldFeatureSpoonerTreeProcedural;

public class WorldFeatureTreeThorn extends WorldFeatureSpoonerTreeProcedural {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureTreeThorn(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void prepare(World world) {
      this.height = this.height + this.random.nextInt(3);
      this.foliageShape = new double[]{4.5, 3.5, 2.0};
      this.branchSlope = 1.0;
      this.trunkRadius = 1.0;
      this.trunkHeight = this.height;
      this.branchDensity = BRANCH_DENSITY / FOLIAGE_DENSITY;
      int axis = this.random.nextInt(2);
      List<int[]> foliageCoords = new ArrayList<>();
      int x = this.pos[0];
      int y = this.pos[1] + this.height + this.random.nextInt(4);
      int z = this.pos[2];
      int x1 = this.pos[0];
      int z1 = this.pos[2];
      int y1 = this.pos[1] + this.height + this.random.nextInt(4);
      if (axis == 1) {
         x += 2 + this.random.nextInt(2);
         x1 -= 2 + this.random.nextInt(2);
      } else {
         z += 2 + this.random.nextInt(2);
         z1 -= 2 + this.random.nextInt(2);
      }

      foliageCoords.add(new int[]{x, y, z});
      foliageCoords.add(new int[]{x1, y1, z1});
      this.foliageCoords = foliageCoords;
   }

   @Override
   protected void makeBranches(World world) {
      int[] treePosition = this.pos;
      int topY = treePosition[1] + (int)(this.trunkHeight + 1.5);

      for (int[] coord : this.foliageCoords) {
         double dist = Math.sqrt(Math.pow(coord[0] - treePosition[0], 2.0) + Math.pow(coord[2] - treePosition[2], 2.0));
         int posY = coord[1];
         double slope = this.branchSlope + (0.5 - this.random.nextDouble()) * 0.16;
         double branchY;
         if (coord[1] - dist * slope > topY) {
            branchY = topY;
         } else {
            branchY = posY - dist * slope;
         }

         branchY = Math.max(branchY, (double)this.pos[1]);
         int[] startCoord = new int[]{treePosition[0], (int)branchY, treePosition[2]};
         this.taperedLimb(startCoord, coord, 1.0, 1.0, world);
      }
   }
}

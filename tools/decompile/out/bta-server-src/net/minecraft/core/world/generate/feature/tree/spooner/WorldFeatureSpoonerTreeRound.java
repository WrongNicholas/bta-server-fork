package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeRound extends WorldFeatureSpoonerTreeProcedural {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeRound(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void prepare(World world) {
      super.prepare(world);
      this.branchSlope = 0.382;
      this.foliageShape = new double[]{2.0, 3.0, 3.0, 2.5, 1.6};
      this.trunkRadius *= 0.8;
      this.trunkHeight = TRUNK_HEIGHT * this.height;
   }

   @Override
   protected Double shapeFunc(int y) {
      Double twigs = super.shapeFunc(y);
      if (twigs != null) {
         return twigs;
      } else if (y < this.height * (0.282 + 0.1 * Math.sqrt(this.random.nextDouble()))) {
         return null;
      } else {
         double radius = this.height / 2.0;
         double adj = this.height / 2.0 - y;
         double dist;
         if (adj == 0.0) {
            dist = radius;
         } else if (Math.abs(adj) >= radius) {
            dist = 0.0;
         } else {
            dist = Math.sqrt(Math.pow(radius, 2.0) - Math.pow(adj, 2.0));
         }

         dist *= 0.618;
         return dist;
      }
   }
}

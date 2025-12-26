package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeCone extends WorldFeatureSpoonerTreeProcedural {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeCone(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void prepare(World world) {
      super.prepare(world);
      this.branchSlope = 0.15;
      this.foliageShape = new double[]{3.0, 2.6, 2.0, 1.0};
      this.trunkRadius *= 0.618;
      this.trunkHeight = this.height;
   }

   @Override
   protected Double shapeFunc(int y) {
      Double twigs = super.shapeFunc(y);
      if (twigs != null) {
         return twigs;
      } else if (y < this.height * (0.25 + 0.05 * Math.sqrt(this.random.nextDouble()))) {
         return null;
      } else {
         double radius = (this.height - y) * 0.382;
         if (radius < 0.0) {
            radius = 0.0;
         }

         return radius;
      }
   }
}

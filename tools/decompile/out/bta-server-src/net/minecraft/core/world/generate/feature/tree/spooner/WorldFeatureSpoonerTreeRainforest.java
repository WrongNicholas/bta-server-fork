package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeRainforest extends WorldFeatureSpoonerTreeProcedural {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeRainforest(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void prepare(World world) {
      this.foliageShape = new double[]{3.4, 2.6};
      super.prepare(world);
      this.branchSlope = 1.0;
      this.trunkRadius *= 0.382;
      this.trunkHeight = this.height * 0.9;
   }

   @Override
   protected Double shapeFunc(int y) {
      if (y < this.height * 0.8) {
         if (HEIGHT < this.height) {
            Double twigs = super.shapeFunc(y);
            if (twigs != null && this.random.nextDouble() < 0.05) {
               return twigs;
            }
         }

         return null;
      } else {
         double width = this.height * 0.382;
         double topDist = (this.height - y) / (this.height * 0.2);
         return width * (0.618 + topDist) * (0.618 + this.random.nextDouble()) * 0.382;
      }
   }
}

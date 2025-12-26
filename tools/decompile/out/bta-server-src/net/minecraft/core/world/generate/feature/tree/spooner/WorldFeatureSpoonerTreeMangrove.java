package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeMangrove extends WorldFeatureSpoonerTreeRound {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeMangrove(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void prepare(World world) {
      super.prepare(world);
      this.branchSlope = 1.0;
      this.trunkRadius *= 0.618;
   }

   @Override
   protected Double shapeFunc(int y) {
      Double val = super.shapeFunc(y);
      return val == null ? val : val * 1.618;
   }
}

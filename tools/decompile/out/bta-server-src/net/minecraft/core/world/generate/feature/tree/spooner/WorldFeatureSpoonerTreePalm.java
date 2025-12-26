package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreePalm extends WorldFeatureSpoonerTree {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreePalm(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void makeFoliage(World world) {
      int y = this.pos[1] + this.height;

      for (int xoff = -2; xoff < 3; xoff++) {
         for (int zoff = -2; zoff < 3; zoff++) {
            int x = this.pos[0] + xoff;
            int z = this.pos[2] + zoff;
            this.assignValue(x, y, z, this.leavesId, this.leavesData, world);
         }
      }
   }
}

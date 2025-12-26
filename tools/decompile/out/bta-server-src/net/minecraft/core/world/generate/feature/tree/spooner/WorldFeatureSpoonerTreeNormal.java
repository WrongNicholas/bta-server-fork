package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeNormal extends WorldFeatureSpoonerTreeStick {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeNormal(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void makeFoliage(World world) {
      int topY = this.pos[1] + this.height - 1;
      int start = topY - 2;
      int end = topY + 2;

      for (int y = start; y < end; y++) {
         int rad;
         if (y > start + 1) {
            rad = 1;
         } else {
            rad = 2;
         }

         for (int xoff = -rad; xoff < rad + 1; xoff++) {
            for (int zoff = -rad; zoff < rad + 1; zoff++) {
               if (!(this.random.nextDouble() > 0.618) || Math.abs(xoff) != Math.abs(zoff) || Math.abs(xoff) != rad) {
                  int x = this.pos[0] + xoff;
                  int z = this.pos[2] + zoff;
                  this.assignValue(x, y, z, this.leavesId, this.leavesData, world);
               }
            }
         }
      }
   }
}

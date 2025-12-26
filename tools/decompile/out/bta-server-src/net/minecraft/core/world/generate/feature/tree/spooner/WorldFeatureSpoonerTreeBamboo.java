package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public class WorldFeatureSpoonerTreeBamboo extends WorldFeatureSpoonerTreeStick {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeBamboo(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void makeFoliage(World world) {
      int start = this.pos[1];
      int end = this.pos[1] + this.height + 1;

      for (int y = start; y < end; y++) {
         for (int i = 0; i < 2; i++) {
            int xoff = this.random.nextInt(2) * 2 - 1;
            int zoff = this.random.nextInt(2) * 2 - 1;
            int x = this.pos[0] + xoff;
            int z = this.pos[2] + zoff;
            this.assignValue(x, y, z, this.leavesId, this.leavesData, world);
         }
      }
   }
}

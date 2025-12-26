package net.minecraft.core.world.generate.feature.tree.spooner;

import net.minecraft.core.world.World;
import net.minecraft.core.world.generate.feature.MethodParametersAnnotation;

public abstract class WorldFeatureSpoonerTreeStick extends WorldFeatureSpoonerTree {
   @MethodParametersAnnotation(names = {"height", "trunkId", "trunkData", "leavesId", "leavesData"})
   public WorldFeatureSpoonerTreeStick(int height, int trunkId, int trunkData, int leavesId, int leavesData) {
      super(height, trunkId, trunkData, leavesId, leavesData);
   }

   @Override
   protected void makeTrunk(World world) {
      int x = this.pos[0];
      int y = this.pos[1];
      int z = this.pos[2];

      for (int i = 0; i < this.height; i++) {
         this.assignValue(x, y, z, this.trunkId, this.trunkData, world);
         y++;
      }
   }
}

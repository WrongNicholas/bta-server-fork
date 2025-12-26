package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;

public class BlockLogicLeavesShrub extends BlockLogicLeavesBase {
   public BlockLogicLeavesShrub(Block<?> block) {
      super(block, Material.leaves, Blocks.SAPLING_SHRUB);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (world.getSeasonManager().getCurrentSeason() != null && world.getSeasonManager().getCurrentSeason().hasFallingLeaves && rand.nextInt(40) == 0) {
         world.spawnParticle("fallingleaf", x, y - 0.1F, z, 0.0, 0.0, 0.0, 0);
      }
   }
}

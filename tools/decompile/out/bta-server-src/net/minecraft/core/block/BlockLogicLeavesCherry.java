package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.season.Seasons;

public class BlockLogicLeavesCherry extends BlockLogicLeavesBase {
   public BlockLogicLeavesCherry(Block<?> block) {
      super(block, Material.leaves, Blocks.SAPLING_CHERRY);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (world.getSeasonManager().getCurrentSeason() != null
         && world.getSeasonManager().getCurrentSeason() == Seasons.OVERWORLD_SPRING
         && rand.nextInt(40) == 0) {
         world.spawnParticle("fallingleaf", x, y - 0.1F, z, 0.0, 0.0, 0.0, 0);
      }
   }
}

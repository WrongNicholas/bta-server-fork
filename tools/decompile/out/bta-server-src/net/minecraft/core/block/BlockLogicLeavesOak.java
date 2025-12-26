package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.World;
import net.minecraft.core.world.wind.WindProvider;

public class BlockLogicLeavesOak extends BlockLogicLeavesBase {
   public BlockLogicLeavesOak(Block<?> block) {
      super(block, Material.leaves, Blocks.SAPLING_OAK);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      super.updateTick(world, x, y, z, rand);
      if (rand.nextInt(128) == 0 && world.getSeasonManager().getCurrentSeason() != null && world.getSeasonManager().getCurrentSeason().hasFallingLeaves) {
         for (int q = -1; q > -16; q--) {
            int id = world.getBlockId(x, y + q, z);
            if (id != 0) {
               break;
            }

            if (Blocks.LAYER_LEAVES_OAK.canPlaceBlockAt(world, x, y + q, z)
               && Blocks.blocksList[world.getBlockId(x, y + q - 1, z)].getMaterial().blocksMotion()) {
               world.setBlockWithNotify(x, y + q, z, Blocks.LAYER_LEAVES_OAK.id());
               break;
            }

            if (world.getBlockId(x, y + q, z) == Blocks.LAYER_LEAVES_OAK.id()) {
               ((BlockLogicLayerLeaves) Blocks.LAYER_LEAVES_OAK.getLogic()).accumulate(world, x, y + q, z);
               break;
            }
         }
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (world.getSeasonManager().getCurrentSeason() != null && world.getSeasonManager().getCurrentSeason().hasFallingLeaves) {
         WindProvider wind = world.getWorldType().getWindManager();
         float windIntensity = wind.getWindIntensity(world, x, y, z);
         if (rand.nextInt((int)(40.0F + 200.0F * (1.0F - windIntensity))) == 0) {
            world.spawnParticle("fallingleaf", x, y - 0.1F, z, 0.0, 0.0, 0.0, 0);
         }
      }
   }
}

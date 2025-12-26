package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicBedrock extends BlockLogic {
   public BlockLogicBedrock(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (world.getBlockId(x, y - 1, z) == Blocks.GLOWSTONE.id()) {
         ((BlockLogicPortal)Blocks.PORTAL_PARADISE.getLogic()).tryToCreatePortal(world, x, y, z, DyeColor.SILVER);
      }
   }
}

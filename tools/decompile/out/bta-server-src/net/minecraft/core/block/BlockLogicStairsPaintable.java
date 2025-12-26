package net.minecraft.core.block;

import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicStairsPaintable extends BlockLogicStairs implements IPaintable {
   public BlockLogicStairsPaintable(Block<?> block, Block<?> modelBlock) {
      super(block, modelBlock);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadata(x, y, z, Blocks.STAIRS_PLANKS_PAINTED.id(), meta);
      ((BlockLogicStairsPainted) Blocks.STAIRS_PLANKS_PAINTED.getLogic()).setColor(world, x, y, z, color);
   }
}

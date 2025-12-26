package net.minecraft.core.block;

import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicSlabPaintable extends BlockLogicSlab implements IPaintable {
   public BlockLogicSlabPaintable(Block<?> block, Block<?> modelBlock) {
      super(block, modelBlock);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadata(x, y, z, Blocks.SLAB_PLANKS_PAINTED.id(), meta);
      ((BlockLogicPlanksPainted) Blocks.SLAB_PLANKS_PAINTED.getLogic()).setColor(world, x, y, z, color);
   }
}

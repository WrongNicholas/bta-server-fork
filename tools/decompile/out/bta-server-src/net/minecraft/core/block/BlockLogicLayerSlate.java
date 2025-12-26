package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class BlockLogicLayerSlate extends BlockLogicLayerBase {
   public BlockLogicLayerSlate(Block<?> block, Block<?> fullBlock, Material material) {
      super(block, fullBlock, material);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         case SILK_TOUCH:
         case EXPLOSION:
         case PROPER_TOOL:
            return new ItemStack[]{new ItemStack(this, meta + 1)};
         default:
            return null;
      }
   }
}

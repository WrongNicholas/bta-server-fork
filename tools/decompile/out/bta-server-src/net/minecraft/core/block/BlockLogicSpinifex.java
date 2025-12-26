package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class BlockLogicSpinifex extends BlockLogicFlower {
   public BlockLogicSpinifex(Block<?> block) {
      super(block);
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return null;
      }
   }
}

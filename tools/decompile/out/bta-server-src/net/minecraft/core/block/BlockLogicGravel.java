package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class BlockLogicGravel extends BlockLogicSand {
   public BlockLogicGravel(Block<?> block) {
      super(block);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PISTON_CRUSH:
            return new ItemStack[]{new ItemStack(Blocks.SAND)};
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return world.rand.nextInt(10) == 0 ? new ItemStack[]{new ItemStack(Items.FLINT)} : new ItemStack[]{new ItemStack(this)};
      }
   }
}

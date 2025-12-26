package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class BlockLogicLeavesPine extends BlockLogicLeavesBase {
   public BlockLogicLeavesPine(Block<?> block) {
      super(block, Material.leaves, Blocks.SAPLING_PINE);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      if (dropCause != EnumDropCause.PICK_BLOCK && dropCause != EnumDropCause.SILK_TOUCH) {
         int numDropped = 1;
         return world.rand.nextInt(20) != 0 ? null : new ItemStack[]{new ItemStack(this.getSapling(), numDropped)};
      } else {
         return new ItemStack[]{new ItemStack(this)};
      }
   }
}

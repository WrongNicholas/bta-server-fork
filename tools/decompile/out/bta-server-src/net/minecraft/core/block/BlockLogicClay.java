package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class BlockLogicClay extends BlockLogic {
   public BlockLogicClay(Block<?> block) {
      super(block, Material.clay);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case SILK_TOUCH:
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return new ItemStack[]{new ItemStack(Items.CLAY, 4)};
      }
   }
}

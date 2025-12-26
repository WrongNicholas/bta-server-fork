package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class BlockLogicDeadBush extends BlockLogicFlower {
   public BlockLogicDeadBush(Block<?> block) {
      super(block);
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, 0.8F, 0.5F + f);
   }

   @Override
   public boolean mayPlaceOn(int blockId) {
      return blockId == Blocks.SAND.id() || blockId == Blocks.DIRT_SCORCHED.id() || BlockTags.GROWS_FLOWERS.appliesTo(Blocks.getBlock(blockId));
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      int quantity = world.rand.nextInt(3);
      switch (dropCause) {
         case SILK_TOUCH:
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return quantity == 0 ? null : new ItemStack[]{new ItemStack(Items.STICK, quantity)};
      }
   }
}

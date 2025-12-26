package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicBookshelf extends BlockLogic {
   public BlockLogicBookshelf(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return super.getBreakResult(world, dropCause, meta, tileEntity);
   }
}

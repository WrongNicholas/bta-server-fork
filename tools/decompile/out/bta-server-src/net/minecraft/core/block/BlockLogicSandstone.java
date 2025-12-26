package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicSandstone extends BlockLogic {
   public BlockLogicSandstone(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause == EnumDropCause.PISTON_CRUSH
         ? new ItemStack[]{new ItemStack(Blocks.SAND, world.rand.nextInt(2) + 1)}
         : super.getBreakResult(world, dropCause, meta, tileEntity);
   }
}

package net.minecraft.core.block;

import java.util.function.Supplier;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCobble extends BlockLogic {
   @Nullable
   private final Supplier<? extends IItemConvertible> crushDrop;

   public BlockLogicCobble(Block<?> block, Material material, @Nullable Supplier<? extends IItemConvertible> crushResult) {
      super(block, material);
      this.crushDrop = crushResult;
   }

   @Override
   public ItemStack @Nullable [] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return this.crushDrop != null && dropCause == EnumDropCause.PISTON_CRUSH
         ? new ItemStack[]{new ItemStack(this.crushDrop.get())}
         : super.getBreakResult(world, dropCause, meta, tileEntity);
   }
}

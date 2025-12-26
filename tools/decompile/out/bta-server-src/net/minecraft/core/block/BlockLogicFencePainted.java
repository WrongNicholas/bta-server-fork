package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicFencePainted extends BlockLogicFence implements IPainted {
   public BlockLogicFencePainted(Block<?> block) {
      super(block);
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return stack.getMetadata();
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(this, 1, meta & 15)};
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return DyeColor.colorFromBlockMeta(meta & 15);
   }

   @Override
   public int toMetadata(DyeColor color) {
      return color.blockMeta;
   }

   @Override
   public int stripColorFromMetadata(int meta) {
      return 0;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      world.setBlockWithNotify(x, y, z, Blocks.FENCE_PLANKS_OAK.id());
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      IPainted.super.setColor(world, x, y, z, color);
   }
}

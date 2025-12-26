package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicChestPainted extends BlockLogicChest implements IPainted {
   public static final int colorBits = 240;
   public static final int colorOffset = 4;

   protected BlockLogicChestPainted(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return stack.getMetadata();
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(this.block, 1, meta & 240)};
   }

   public static int getMetaForDyeColor(int i) {
      return ~i << 4 & 240;
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return DyeColor.colorFromBlockMeta((meta & 240) >> 4);
   }

   @Override
   public int toMetadata(DyeColor color) {
      return color.blockMeta << 4;
   }

   @Override
   public int stripColorFromMetadata(int meta) {
      return meta & -241;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      int meta = this.stripColorFromMetadata(world.getBlockMetadata(x, y, z));
      world.setBlockAndMetadataWithNotify(x, y, z, Blocks.CHEST_PLANKS_OAK.id(), meta);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      IPainted.super.setColor(world, x, y, z, color);
   }
}

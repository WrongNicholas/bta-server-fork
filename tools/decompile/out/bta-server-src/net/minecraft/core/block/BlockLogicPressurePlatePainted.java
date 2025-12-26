package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockLogicPressurePlatePainted<T extends Entity> extends BlockLogicPressurePlate<T> implements IPainted {
   public static final int MASK_COLOR = 240;

   public BlockLogicPressurePlatePainted(Block<?> block, Class<T> mobType, Material material) {
      super(block, mobType, material);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(this.block, 1, meta & 240)};
   }

   @Override
   public boolean canBePainted() {
      return true;
   }

   @Override
   public DyeColor fromMetadata(int meta) {
      return DyeColor.colorFromBlockMeta(meta >> 4);
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
      world.setBlockWithNotify(x, y, z, Blocks.PRESSURE_PLATE_PLANKS_OAK.id());
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return stack.getMetadata() & 240;
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, this.stripColorFromMetadata(meta) | this.toMetadata(color));
   }
}

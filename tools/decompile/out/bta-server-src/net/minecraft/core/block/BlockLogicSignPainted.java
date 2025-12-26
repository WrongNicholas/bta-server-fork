package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicSignPainted extends BlockLogicSign implements IPainted {
   public static final int MASK_COLOR = 240;

   public BlockLogicSignPainted(Block<?> block, boolean isFreeStanding) {
      super(block, isFreeStanding);
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
      world.setBlockMetadata(x, y, z, this.stripColorFromMetadata(world.getBlockMetadata(x, y, z)));
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      world.setBlockMetadataWithNotify(x, y, z, color.blockMeta << 4 | this.stripColorFromMetadata(world.getBlockMetadata(x, y, z)));
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.SIGN_PAINTED, 1, this.fromMetadata(meta).itemMeta)};
   }
}

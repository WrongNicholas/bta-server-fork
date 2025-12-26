package net.minecraft.core.block;

import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicStairsPainted extends BlockLogicStairs implements IPainted {
   public BlockLogicStairsPainted(Block<?> block, Block<?> modelBlock) {
      super(block, modelBlock);
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
      return meta & 15;
   }

   @Override
   public void removeDye(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataWithNotify(x, y, z, Blocks.STAIRS_PLANKS_OAK.id(), this.stripColorFromMetadata(meta));
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      IPainted.super.setColor(world, x, y, z, color);
   }
}

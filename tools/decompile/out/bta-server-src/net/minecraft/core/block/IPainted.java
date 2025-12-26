package net.minecraft.core.block;

import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public interface IPainted extends IPaintable {
   DyeColor fromMetadata(int var1);

   int toMetadata(DyeColor var1);

   int stripColorFromMetadata(int var1);

   void removeDye(World var1, int var2, int var3, int var4);

   default DyeColor getColor(World world, int x, int y, int z) {
      return this.fromMetadata(world.getBlockMetadata(x, y, z));
   }

   @Override
   default void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, this.stripColorFromMetadata(meta) | this.toMetadata(color));
   }
}

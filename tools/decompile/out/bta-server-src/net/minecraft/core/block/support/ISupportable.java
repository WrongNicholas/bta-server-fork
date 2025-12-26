package net.minecraft.core.block.support;

import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public interface ISupportable {
   ISupport getSupportConstraint(World var1, int var2, int var3, int var4, Side var5);

   default boolean isSupported(World world, int x, int y, int z, Side side) {
      Block<?> supportingBlock = world.getBlock(x, y, z);
      if (supportingBlock == null) {
         return false;
      } else {
         ISupport supporting = supportingBlock.getSupport(world, x, y, z, side.getOpposite());
         ISupport supported = this.getSupportConstraint(world, x, y, z, side);
         return supporting.canSupport(supported, side.getOpposite());
      }
   }
}

package net.minecraft.core.block;

import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public class BlockLogicPiePumpkin extends BlockLogicEdible {
   public BlockLogicPiePumpkin(Block<?> block) {
      super(block, 4, 5, () -> Items.FOOD_PUMPKIN_PIE);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z);
      float f = 0.0625F;
      float xMin = l >= 2 ? 0.5F : f;
      float zMin = l >= 3 ? 0.5F : f;
      float f2 = 0.375F;
      return AABB.getTemporaryBB(xMin, 0.0, zMin, 1.0F - f, f2, 1.0F - f);
   }
}

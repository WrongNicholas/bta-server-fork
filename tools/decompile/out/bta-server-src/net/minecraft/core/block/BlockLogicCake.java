package net.minecraft.core.block;

import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public class BlockLogicCake extends BlockLogicEdible {
   public BlockLogicCake(Block<?> block) {
      super(block, 6, 3, () -> Items.FOOD_CAKE);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z);
      float f = 0.0625F;
      float f1 = (1 + l * 2) / 16.0F;
      float f2 = 0.5F;
      return AABB.getTemporaryBB(f1, 0.0, f, 1.0F - f, f2, 1.0F - f);
   }
}

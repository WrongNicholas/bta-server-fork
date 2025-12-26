package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.block.ItemBlockLayer;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public abstract class BlockLogicLayerBase extends BlockLogic {
   public Block<?> fullBlock = null;

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public BlockLogicLayerBase(Block<?> block, Block<?> fullBlock, Material material) {
      super(block, material);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);

      Block typed = (Block) block;
      block.setBlockItem(() -> new ItemBlockLayer<>(typed));
      this.fullBlock = fullBlock;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z) & 7;
      float f = 2 * (1 + l) / 16.0F;
      return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, f, 1.0);
   }
}

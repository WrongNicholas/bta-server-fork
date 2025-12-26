package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;

public class BlockLogicTransparent extends BlockLogic {
   public BlockLogicTransparent(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }
}

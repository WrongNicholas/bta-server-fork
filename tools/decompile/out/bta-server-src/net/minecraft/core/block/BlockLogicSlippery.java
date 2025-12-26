package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;

public class BlockLogicSlippery extends BlockLogic {
   public BlockLogicSlippery(Block<?> block, Material material) {
      super(block, material);
      block.friction = 0.98F;
   }
}

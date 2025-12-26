package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFenceSteel extends BlockLogicFenceThin {
   public BlockLogicFenceSteel(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean canConnectTo(WorldSource world, int x, int y, int z) {
      Block<?> block = world.getBlock(x, y, z);
      return BlockTags.CHAINLINK_FENCES_CONNECT.appliesTo(block) || block != null && (block.getMaterial().isStone() || block.getMaterial().isMetal());
   }
}

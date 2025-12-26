package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFenceWallPaper extends BlockLogicFenceThin {
   public BlockLogicFenceWallPaper(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean canConnectTo(WorldSource world, int x, int y, int z) {
      int l = world.getBlockId(x, y, z);
      return Blocks.hasTag(l, BlockTags.FENCES_CONNECT);
   }
}

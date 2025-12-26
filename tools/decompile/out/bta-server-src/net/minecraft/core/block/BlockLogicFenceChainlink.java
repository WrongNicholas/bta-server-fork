package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicFenceChainlink extends BlockLogicFenceThin {
   public BlockLogicFenceChainlink(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean canConnectTo(WorldSource world, int x, int y, int z) {
      Block<?> b = world.getBlock(x, y, z);
      return BlockTags.CHAINLINK_FENCES_CONNECT.appliesTo(b) || b != null && (b.getMaterial().isStone() || b.getMaterial().isMetal());
   }

   @Override
   public boolean isClimbable(World world, int x, int y, int z) {
      return true;
   }
}

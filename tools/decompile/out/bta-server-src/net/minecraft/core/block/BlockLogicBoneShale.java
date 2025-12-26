package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicBoneShale extends BlockLogicFullyRotatable {
   public BlockLogicBoneShale(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public float blockStrength(World world, int x, int y, int z, Side side, Player player) {
      float blockHardness = this.block.getHardness();
      int meta = world.getBlockMetadata(x, y, z);
      Direction direction = BlockLogicFullyRotatable.metaToDirection(meta);
      if (side != direction.getSide()) {
         blockHardness *= 66.6F;
      }

      return !player.canHarvestBlock(this.block) ? 1.0F / blockHardness / 30.0F : player.getCurrentPlayerStrVsBlock(this.block) / blockHardness / 30.0F;
   }
}

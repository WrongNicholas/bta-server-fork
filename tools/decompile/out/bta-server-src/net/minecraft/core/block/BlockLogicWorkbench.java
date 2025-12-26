package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicWorkbench extends BlockLogic {
   public BlockLogicWorkbench(Block<?> block) {
      super(block, Material.wood);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide) {
         player.displayWorkbenchScreen(x, y, z);
      }

      return true;
   }
}

package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.World;

public class BlockLogicPlanks extends BlockLogic implements IPaintable {
   public BlockLogicPlanks(Block<?> block) {
      super(block, Material.wood);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      world.setBlock(x, y, z, Blocks.PLANKS_OAK_PAINTED.id());
      Blocks.PLANKS_OAK_PAINTED.getLogic().setColor(world, x, y, z, color);
   }
}

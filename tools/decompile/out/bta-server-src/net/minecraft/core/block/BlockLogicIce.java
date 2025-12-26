package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class BlockLogicIce extends BlockLogicTransparent {
   public BlockLogicIce(Block<?> block) {
      super(block, Material.ice);
      block.friction = 0.98F;
      block.setTicking(true);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return null;
      }
   }

   @Override
   public void harvestBlock(World world, Player player, int x, int y, int z, int meta, TileEntity tileEntity) {
      super.harvestBlock(world, player, x, y, z, meta, tileEntity);
      Material material = world.getBlockMaterial(x, y - 1, z);
      if (material.blocksMotion() || material.isLiquid()) {
         world.setBlockWithNotify(x, y, z, Blocks.FLUID_WATER_FLOWING.id());
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11 - this.block.lightBlock) {
         world.setBlockWithNotify(x, y, z, Blocks.FLUID_WATER_STILL.id());
      }
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 0;
   }
}

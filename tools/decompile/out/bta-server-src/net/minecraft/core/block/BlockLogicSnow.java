package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

public class BlockLogicSnow extends BlockLogic {
   public BlockLogicSnow(Block<?> block) {
      super(block, Material.snow);
      block.setTicking(true);
   }

   public int quantityDropped(int meta, Random rand) {
      return 4;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return new ItemStack[]{new ItemStack(Items.AMMO_SNOWBALL, 4)};
      }
   }
}

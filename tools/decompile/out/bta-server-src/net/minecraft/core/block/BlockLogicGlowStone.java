package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicGlowStone extends BlockLogic {
   public BlockLogicGlowStone(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return new ItemStack[]{new ItemStack(Items.DUST_GLOWSTONE, 4)};
      }
   }

   @Override
   public float getAmbientOcclusionStrength(WorldSource blockAccess, int x, int y, int z) {
      return 0.0F;
   }
}

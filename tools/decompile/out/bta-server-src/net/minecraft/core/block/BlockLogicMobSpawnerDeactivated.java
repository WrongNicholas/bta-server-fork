package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class BlockLogicMobSpawnerDeactivated extends BlockLogicTransparent {
   public BlockLogicMobSpawnerDeactivated(Block<?> block) {
      super(block, Material.stone);
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean canPlaceOnSurface() {
      return true;
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      return !(entity instanceof EntityItem);
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
}

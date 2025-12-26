package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicCobweb extends BlockLogic {
   public BlockLogicCobweb(Block<?> block, Material mat) {
      super(block, mat);
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      entity.fallDistance = 0.0F;
      entity.stuckInCobweb = true;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case PICK_BLOCK:
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this)};
         case EXPLOSION:
         case PROPER_TOOL:
            return new ItemStack[]{new ItemStack(Items.STRING)};
         default:
            return null;
      }
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }
}

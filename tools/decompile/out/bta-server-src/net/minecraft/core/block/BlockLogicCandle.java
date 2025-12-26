package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicCandle extends BlockLogic {
   public BlockLogicCandle(Block<?> block, Material material) {
      super(block, material);
      this.setBlockBounds(0.40625, 0.0, 0.40625, 0.59375, 0.5, 0.59375);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide) {
         boolean flag = !world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
         if (flag) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
            world.setBlockWithNotify(x, y, z, 0);
         }
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{null};
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      double d = x + 0.5;
      double d1 = y + 0.7;
      double d2 = z + 0.5;
      world.spawnParticle("smoke", d, d1, d2, 0.0, 0.0, 0.0, 0);
      world.spawnParticle("soulflame", d, d1, d2, 0.0, 0.0, 0.0, 0);
   }
}

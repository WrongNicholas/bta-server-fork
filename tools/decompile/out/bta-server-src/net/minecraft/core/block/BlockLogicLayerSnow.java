package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicLayerSnow extends BlockLogicLayerBase {
   public BlockLogicLayerSnow(Block<?> block, Block<?> fullBlock, Material material) {
      super(block, fullBlock, material);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
      block.setTicking(true);
   }

   public void accumulate(World world, int x, int y, int z) {
      int myMetadata = world.getBlockMetadata(x, y, z);
      if (myMetadata != 7) {
         boolean posXValid = world.isBlockOpaqueCube(x + 1, y, z)
            || world.getBlockId(x + 1, y, z) == this.block.id() && world.getBlockMetadata(x + 1, y, z) >= myMetadata;
         if (posXValid) {
            boolean posZValid = world.isBlockOpaqueCube(x, y, z + 1)
               || world.getBlockId(x, y, z + 1) == this.block.id() && world.getBlockMetadata(x, y, z + 1) >= myMetadata;
            if (posZValid) {
               boolean negXValid = world.isBlockOpaqueCube(x - 1, y, z)
                  || world.getBlockId(x - 1, y, z) == this.block.id() && world.getBlockMetadata(x - 1, y, z) >= myMetadata;
               if (negXValid) {
                  boolean negZValid = world.isBlockOpaqueCube(x, y, z - 1)
                     || world.getBlockId(x, y, z - 1) == this.block.id() && world.getBlockMetadata(x, y, z - 1) >= myMetadata;
                  if (negZValid) {
                     world.setBlockMetadata(x, y, z, myMetadata + 1);
                     world.markBlockNeedsUpdate(x, y, z);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      int l = world.getBlockId(x, y - 1, z);
      if (l != 0 && (Blocks.blocksList[l].isSolidRender() || Blocks.blocksList[l].getLogic() instanceof BlockLogicLeavesBase)) {
         Material material = world.getBlockMaterial(x, y - 1, z);
         return material == Material.leaves || material.blocksMotion();
      } else {
         return false;
      }
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      AABB aabb = super.getCollisionBoundingBoxFromPool(world, x, y, z);
      aabb.maxY = Math.max(aabb.maxY - 0.125, (double)y);
      return aabb;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!this.canPlaceBlockAt(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      switch (dropCause) {
         case SILK_TOUCH:
            return new ItemStack[]{new ItemStack(this, meta + 1)};
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         case EXPLOSION:
         case PROPER_TOOL:
            return new ItemStack[]{new ItemStack(Items.AMMO_SNOWBALL, meta + 1)};
         default:
            return null;
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }

      if (!world.getBlockBiome(x, y, z).hasSurfaceSnow()
         && world.getSeasonManager().getCurrentSeason() != null
         && world.getSeasonManager().getCurrentSeason().letWeatherCleanUpSnow) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }
}

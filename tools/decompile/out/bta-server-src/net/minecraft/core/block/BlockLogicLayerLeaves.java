package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlockLayerLeaves;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicLayerLeaves extends BlockLogicLayerBase {
   public static final int MASK_PERMANENT = 128;

   @SuppressWarnings({ "unchecked", "rawtypes" })
   public BlockLogicLayerLeaves(Block<?> block, Block<?> fullBlock, Material material) {
      super(block, fullBlock, material);

      Block typed = (Block) block;

      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.125, 1.0);
      block.setTicking(true);
      block.setBlockItem(() -> new ItemBlockLayerLeaves<>(typed));
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
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
         return material.blocksMotion();
      } else {
         return false;
      }
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
            return new ItemStack[]{new ItemStack(this, (meta & -129) + 1)};
         case PICK_BLOCK:
            return new ItemStack[]{new ItemStack(this)};
         default:
            return null;
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (world.getSeasonManager().getCurrentSeason() != null && !world.getSeasonManager().getCurrentSeason().hasFallingLeaves) {
         int metadata = world.getBlockMetadata(x, y, z);
         if (!isPermanent(metadata) && rand.nextInt(2) == 0) {
            if (metadata > 0) {
               world.setBlockMetadata(x, y, z, metadata - 1);
            } else {
               world.setBlockWithNotify(x, y, z, 0);
            }
         }
      }
   }

   public static boolean isPermanent(int meta) {
      return (meta & 128) != 0;
   }

   public static int setPermanent(int meta, boolean flag) {
      return flag ? meta | 128 : meta & -129;
   }
}

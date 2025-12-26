package net.minecraft.core.block.piston;

import java.util.ArrayList;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicPistonHead extends BlockLogic {
   public static final int MASK_DIRECTION = 7;
   public static final int MASK_TYPE = 248;
   public static final int TYPE_NORMAL = 0;
   public static final int TYPE_STICKY = 1;
   public static final int TYPE_STEEL = 2;
   private final double headThickness;
   private final double shaftThickness;

   public BlockLogicPistonHead(Block<?> block, double headThickness, double shaftThickness) {
      super(block, Material.piston);
      block.withHardness(0.5F);
      this.headThickness = headThickness;
      this.shaftThickness = shaftThickness;
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      super.onBlockRemoved(world, x, y, z, data);
      Direction direction = getDirectionFromMeta(data);
      if (direction != Direction.NONE) {
         Direction orientation = direction.getOpposite();
         int x2 = x + orientation.getOffsetX();
         int y2 = y + orientation.getOffsetY();
         int z2 = z + orientation.getOffsetZ();
         Block<?> block = world.getBlock(x2, y2, z2);
         if (block != null && block.getLogic() instanceof BlockLogicPistonBase) {
            int data2 = world.getBlockMetadata(x2, y2, z2);
            if (BlockLogicPistonBase.isPowered(data2)) {
               block.dropBlockWithCause(world, EnumDropCause.PROPER_TOOL, x2, y2, z2, data2, null, null);
               world.setBlockWithNotify(x2, y2, z2, 0);
            }
         }
      }
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
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World world, int x, int y, int z, Side side) {
      return false;
   }

   @Override
   public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList<AABB> aabbList) {
      switch (getDirectionFromMeta(world.getBlockMetadata(x, y, z))) {
         case DOWN:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, this.headThickness, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     0.5 - this.shaftThickness / 2.0,
                     this.headThickness,
                     0.5 - this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0,
                     1.0 + this.headThickness,
                     0.5 + this.shaftThickness / 2.0
                  )
                  .move(x, y, z),
               aabbList
            );
            break;
         case UP:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 1.0 - this.headThickness, 0.0, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     0.5 - this.shaftThickness / 2.0,
                     -this.headThickness,
                     0.5 - this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0,
                     1.0 - this.headThickness,
                     0.5 + this.shaftThickness / 2.0
                  )
                  .move(x, y, z),
               aabbList
            );
            break;
         case NORTH:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, this.headThickness).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     0.5 - this.shaftThickness / 2.0,
                     0.5 - this.shaftThickness / 2.0,
                     this.headThickness,
                     0.5 + this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0,
                     1.0 + this.headThickness
                  )
                  .move(x, y, z),
               aabbList
            );
            break;
         case SOUTH:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 1.0 - this.headThickness, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     0.5 - this.shaftThickness / 2.0,
                     0.5 - this.shaftThickness / 2.0,
                     -this.headThickness,
                     0.5 + this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0,
                     1.0 - this.headThickness
                  )
                  .move(x, y, z),
               aabbList
            );
            break;
         case WEST:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(0.0, 0.0, 0.0, this.headThickness, 1.0, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     this.headThickness,
                     0.5 - this.shaftThickness / 2.0,
                     0.5 - this.shaftThickness / 2.0,
                     1.0 + this.headThickness,
                     0.5 + this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0
                  )
                  .move(x, y, z),
               aabbList
            );
            break;
         case EAST:
            this.addIntersectingBoundingBox(aabb, AABB.getTemporaryBB(1.0 - this.headThickness, 0.0, 0.0, 1.0, 1.0, 1.0).move(x, y, z), aabbList);
            this.addIntersectingBoundingBox(
               aabb,
               AABB.getTemporaryBB(
                     -this.headThickness,
                     0.5 - this.shaftThickness / 2.0,
                     0.5 - this.shaftThickness / 2.0,
                     1.0 - this.headThickness,
                     0.5 + this.shaftThickness / 2.0,
                     0.5 + this.shaftThickness / 2.0
                  )
                  .move(x, y, z),
               aabbList
            );
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int l = world.getBlockMetadata(x, y, z);
      switch (getDirectionFromMeta(l)) {
         case DOWN:
            return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, this.headThickness, 1.0);
         case UP:
            return AABB.getTemporaryBB(0.0, 1.0 - this.headThickness, 0.0, 1.0, 1.0, 1.0);
         case NORTH:
            return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, this.headThickness);
         case SOUTH:
            return AABB.getTemporaryBB(0.0, 0.0, 1.0 - this.headThickness, 1.0, 1.0, 1.0);
         case WEST:
            return AABB.getTemporaryBB(0.0, 0.0, 0.0, this.headThickness, 1.0, 1.0);
         case EAST:
            return AABB.getTemporaryBB(1.0 - this.headThickness, 0.0, 0.0, 1.0, 1.0, 1.0);
         default:
            return super.getBlockBoundsFromState(world, x, y, z);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      Direction direction = getDirectionFromMeta(world.getBlockMetadata(x, y, z));
      if (direction != Direction.NONE) {
         Block<?> block = world.getBlock(x - direction.getOffsetX(), y - direction.getOffsetY(), z - direction.getOffsetZ());
         if (block != null && block.getLogic() instanceof BlockLogicPistonBase) {
            block.onNeighborBlockChange(world, x - direction.getOffsetX(), y - direction.getOffsetY(), z - direction.getOffsetZ(), blockId);
         } else {
            world.setBlockWithNotify(x, y, z, 0);
         }
      }
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 2;
   }

   public static Direction getDirectionFromMeta(int meta) {
      return Direction.getDirectionById(meta & 7);
   }

   public static int getPistonType(int data) {
      return (data & 0xFF & 248) >> 3;
   }

   public static int setPistonType(int type, int data) {
      return (data & -249 | type << 3) & 0xFF;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      if (dropCause == EnumDropCause.PICK_BLOCK) {
         switch (getPistonType(meta)) {
            case 0:
               return new ItemStack[]{new ItemStack(Blocks.PISTON_BASE)};
            case 1:
               return new ItemStack[]{new ItemStack(Blocks.PISTON_BASE_STICKY)};
            case 2:
               return new ItemStack[]{new ItemStack(Blocks.PISTON_BASE_STEEL)};
         }
      }

      return null;
   }
}

package net.minecraft.core.block.piston;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.Nullable;

public class BlockLogicPistonMoving extends BlockLogic {
   public BlockLogicPistonMoving(Block<?> container) {
      super(container, Material.piston);
      container.withHardness(-1.0F);
      container.withEntity(null);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      if (tileEntity instanceof TileEntityMovingPistonBlock) {
         ((TileEntityMovingPistonBlock)tileEntity).finalTick();
      } else {
         super.onBlockRemoved(world, x, y, z, data);
      }
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
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (!world.isClientSide && world.getTileEntity(x, y, z) == null) {
         int id = 0;
         TileEntityMovingPistonBlock piston = this.getTileEntity(world, x, y, z);
         if (piston != null) {
            id = piston.getMovedId();
         }

         world.setBlockWithNotify(x, y, z, id);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return null;
   }

   @Override
   public void dropBlockWithCause(World world, EnumDropCause cause, int x, int y, int z, int meta, TileEntity tileEntity, Player player) {
      if (!world.isClientSide) {
         TileEntityMovingPistonBlock piston = this.getTileEntity(world, x, y, z);
         if (piston != null) {
            Blocks.blocksList[piston.getMovedId()].dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, meta, null, null);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide && world.getTileEntity(x, y, z) != null) {
      }
   }

   public static TileEntity createTileEntity(
      int movedId, int movedData, @Nullable TileEntity movedEntity, Direction direction, boolean extending, boolean isSourcePiston
   ) {
      assert !(movedEntity instanceof TileEntityMovingPistonBlock) : "Moving piston entity should not be able to be put into another moving piston!";

      return new TileEntityMovingPistonBlock(movedId, movedData, movedEntity, direction, extending, isSourcePiston);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      TileEntityMovingPistonBlock tileEntity = this.getTileEntity(world, x, y, z);
      if (tileEntity == null) {
         return null;
      } else {
         float progress = tileEntity.getProgress(0.0F);
         if (tileEntity.isExtending()) {
            progress = 1.0F - progress;
         }

         return this.getCollisionShapeFromTileEntity(world, x, y, z, tileEntity.getMovedId(), progress, tileEntity.getDirection());
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      TileEntityMovingPistonBlock pistonTileEntity = this.getTileEntity(world, x, y, z);
      if (pistonTileEntity != null) {
         Block<?> block = Blocks.blocksList[pistonTileEntity.getMovedId()];
         if (block != null && block != this.block) {
            float f = pistonTileEntity.getProgress(0.0F);
            if (pistonTileEntity.isExtending()) {
               f = 1.0F - f;
            }

            Direction direction = pistonTileEntity.getDirection();
            AABB otherBounds = block.getBlockBoundsFromState(world, x, y, z);
            return AABB.getTemporaryBB(
               otherBounds.minX - direction.getOffsetX() * f,
               otherBounds.maxX - direction.getOffsetX() * f,
               otherBounds.minY - direction.getOffsetY() * f,
               otherBounds.maxY - direction.getOffsetY() * f,
               otherBounds.minZ - direction.getOffsetZ() * f,
               otherBounds.maxZ - direction.getOffsetZ() * f
            );
         } else {
            return super.getBlockBoundsFromState(world, x, y, z);
         }
      } else {
         return super.getBlockBoundsFromState(world, x, y, z);
      }
   }

   public AABB getCollisionShapeFromTileEntity(WorldSource world, int x, int y, int z, int blockID, float directionStretch, Direction direction) {
      if (blockID != 0 && blockID != this.block.id()) {
         AABB aabb = Blocks.blocksList[blockID].getCollisionBoundingBoxFromPool(world, x, y, z);
         if (aabb == null) {
            return null;
         } else {
            aabb.minX = aabb.minX - direction.getOffsetX() * directionStretch;
            aabb.maxX = aabb.maxX - direction.getOffsetX() * directionStretch;
            aabb.minY = aabb.minY - direction.getOffsetY() * directionStretch;
            aabb.maxY = aabb.maxY - direction.getOffsetY() * directionStretch;
            aabb.minZ = aabb.minZ - direction.getOffsetZ() * directionStretch;
            aabb.maxZ = aabb.maxZ - direction.getOffsetZ() * directionStretch;
            return aabb;
         }
      } else {
         return null;
      }
   }

   private TileEntityMovingPistonBlock getTileEntity(WorldSource world, int x, int y, int z) {
      TileEntity tileEntity = world.getTileEntity(x, y, z);
      return tileEntity instanceof TileEntityMovingPistonBlock ? (TileEntityMovingPistonBlock)tileEntity : null;
   }
}

package net.minecraft.core.block.piston;

import java.util.ArrayList;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityFallingBlock;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.IVehicle;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicPistonBaseSteel extends BlockLogicPistonBase {
   public static final double HEAD_THICKNESS_STEEL = 0.375;
   private Entity flungBlock = null;

   public BlockLogicPistonBaseSteel(Block<?> container, int maxPushedBlocks) {
      super(container, maxPushedBlocks);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int data = world.getBlockMetadata(x, y, z);
      if (isPowered(data)) {
         switch (getDirection(data)) {
            case DOWN:
               return AABB.getTemporaryBB(0.0, 0.375, 0.0, 1.0, 1.0, 1.0);
            case UP:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0);
            case NORTH:
               return AABB.getTemporaryBB(0.0, 0.0, 0.375, 1.0, 1.0, 1.0);
            case SOUTH:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.625);
            case WEST:
               return AABB.getTemporaryBB(0.375, 0.0, 0.0, 1.0, 1.0, 1.0);
            case EAST:
            default:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 0.625, 1.0, 1.0);
         }
      } else {
         return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      }
   }

   @Override
   protected boolean canPushLine(World world, int x, int y, int z, Direction direction, int maxPushedBlocks) {
      int xo = x + direction.getOffsetX();
      int yo = y + direction.getOffsetY();
      int zo = z + direction.getOffsetZ();
      int blocks = 0;

      boolean didCrush;
      for (didCrush = false; blocks < maxPushedBlocks + 1; blocks++) {
         if (yo < 0 || yo >= world.getHeightBlocks()) {
            return false;
         }

         int id = world.getBlockId(xo, yo, zo);
         if (id != 0) {
            if (!this.isPushable(id, world, xo, yo, zo, true)) {
               if (blocks != 1 || !BlockTags.PISTON_CRUSHING.appliesTo(Blocks.getBlock(id)) || !this.tryCrush(world, x, y, z, direction)) {
                  return false;
               }

               didCrush = true;
            } else if (Blocks.blocksList[id].getPistonPushReaction(world, xo, yo, zo) != 1) {
               if (blocks == maxPushedBlocks) {
                  return false;
               }

               xo += direction.getOffsetX();
               yo += direction.getOffsetY();
               zo += direction.getOffsetZ();
               continue;
            }
         }
         break;
      }

      if (!didCrush && blocks == 1) {
         xo = x + direction.getOffsetX();
         yo = y + direction.getOffsetY();
         zo = z + direction.getOffsetZ();
         Block<?> block = world.getBlock(xo, yo, zo);
         if (block == null) {
            return true;
         }

         int blockMeta = world.getBlockMetadata(xo, yo, zo);
         TileEntity tileEntity = world.getTileEntity(xo, yo, zo);
         world.removeBlockTileEntity(xo, yo, zo);
         world.setBlockWithNotify(xo, yo, zo, 0);
         if (!world.isClientSide) {
            EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, xo + 0.5, yo + 0.5, zo + 0.5, block.id(), blockMeta, tileEntity);
            entityFallingBlock.hasRemovedBlock = true;
            if (tileEntity instanceof IVehicle) {
               Entity rider = ((IVehicle)tileEntity).getPassenger();
               ((IVehicle)tileEntity).ejectRider();
               if (rider != null) {
                  rider.startRiding(entityFallingBlock);
               }
            }

            world.entityJoinedWorld(entityFallingBlock);
            double speed = 2.0;
            entityFallingBlock.fling(direction.getOffsetX() * 2.0, direction.getOffsetY() * 2.0, direction.getOffsetZ() * 2.0, 1.0F);
            this.flungBlock = entityFallingBlock;
         }
      }

      return true;
   }

   @Override
   public boolean tryCrush(World world, int x, int y, int z, Direction direction) {
      int x2 = x + direction.getOffsetX();
      int y2 = y + direction.getOffsetY();
      int z2 = z + direction.getOffsetZ();
      Block<?> block = world.getBlock(x2, y2, z2);
      if (block == null) {
         return true;
      } else {
         world.playBlockEvent(null, 2001, x2, y2, z2, world.getBlockId(x2, y2, z2));
         block.dropBlockWithCause(world, EnumDropCause.PISTON_CRUSH, x2, y2, z2, world.getBlockMetadata(x2, y2, z2), null, null);
         world.setBlockWithNotify(x2, y2, z2, 0);
         return true;
      }
   }

   @Override
   protected boolean tryExtend(World world, int x, int y, int z, Direction direction, int maxPushedBlocks) {
      if (world.isClientSide) {
         int data = world.getBlockMetadata(x, y, z);
         int xo = x + direction.getOffsetX();
         int yo = y + direction.getOffsetY();
         int zo = z + direction.getOffsetZ();
         int blocks = 0;

         while (true) {
            if (blocks < maxPushedBlocks + 1) {
               if (yo < 0 || yo >= world.getHeightBlocks()) {
                  return false;
               }

               int blockId = world.getBlockId(xo, yo, zo);
               if (blockId != 0) {
                  if (!this.isPushable(blockId, world, xo, yo, zo, true)) {
                     return false;
                  }

                  if (Blocks.blocksList[blockId].getPistonPushReaction(world, xo, yo, zo) != 1) {
                     if (blocks == maxPushedBlocks) {
                        return false;
                     }

                     xo += direction.getOffsetX();
                     yo += direction.getOffsetY();
                     zo += direction.getOffsetZ();
                     blocks++;
                     continue;
                  }

                  Blocks.blocksList[blockId]
                     .dropBlockWithCause(world, EnumDropCause.WORLD, xo, yo, zo, world.getBlockMetadata(xo, yo, zo), world.getTileEntity(xo, yo, zo), null);
               }
            }

            if (blocks == 1) {
               while (xo != x || yo != y || zo != z) {
                  int px = xo - direction.getOffsetX();
                  int py = yo - direction.getOffsetY();
                  int pz = zo - direction.getOffsetZ();
                  int pushId = world.getBlockId(px, py, pz);
                  TileEntity pushEntity = world.getTileEntity(px, py, pz);
                  if (pushEntity instanceof TileEntityMovingPistonBlock && px == x && py == y && pz == z) {
                     break;
                  }

                  if (pushId == this.block.id() && px == x && py == y && pz == z) {
                     this.createPistonHeadAt(world, xo, yo, zo, data, direction);
                  }

                  if (px != x || py != y || pz != z) {
                     world.setBlockAndMetadataRaw(px, py, pz, 0, 0);
                  }

                  xo = px;
                  yo = py;
                  zo = pz;
               }

               return true;
            }
            break;
         }
      }

      return super.tryExtend(world, x, y, z, direction, maxPushedBlocks);
   }

   @Override
   public void extendEvent(World world, int x, int y, int z, int data, Direction direction) {
      for (Entity entity : new ArrayList<>(
         world.getEntitiesWithinAABBExcludingEntity(
            null,
            AABB.getTemporaryBB(
               (double)x + direction.getOffsetX(),
               (double)y + direction.getOffsetY(),
               (double)z + direction.getOffsetZ(),
               (double)x + direction.getOffsetX() + 1.0,
               (double)y + direction.getOffsetY() + 1.0,
               (double)z + direction.getOffsetZ() + 1.0
            )
         )
      )) {
         if (entity != null
            && !entity.noPhysics
            && entity != this.flungBlock
            && (entity.world == null || !entity.world.isClientSide || !(entity instanceof Player))) {
            double speed = 2.0;
            entity.fling(direction.getOffsetX() * 2.0, direction.getOffsetY() * 2.0, direction.getOffsetZ() * 2.0, 1.0F);
         }
      }

      this.flungBlock = null;
      super.extendEvent(world, x, y, z, data, direction);
   }

   @Override
   public void createPistonHeadAt(World world, int x, int y, int z, int data, Direction direction) {
      world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), BlockLogicPistonHead.setPistonType(2, direction.getId()));
      world.replaceBlockTileEntity(
         x,
         y,
         z,
         BlockLogicPistonMoving.createTileEntity(
            Blocks.PISTON_HEAD_STEEL.id(), BlockLogicPistonHead.setPistonType(2, direction.getId()), null, direction, true, false
         )
      );
   }
}

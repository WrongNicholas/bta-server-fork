package net.minecraft.core.block.piston;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicPistonBase extends BlockLogic {
   public static final int MASK_DIRECTION = 7;
   public static final int MASK_POWERED = 8;
   public static final int EVENT_EXTEND = 0;
   public static final int EVENT_RETRACT = 1;
   private final int maxPushedBlocks;

   public BlockLogicPistonBase(Block<?> block, int maxPushedBlocks) {
      super(block, Material.piston);
      this.maxPushedBlocks = maxPushedBlocks;
      block.withHardness(0.5F);
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      Direction placementDirection = mob.getPlacementDirection(side).getOpposite();
      world.setBlockMetadataWithNotify(x, y, z, placementDirection.getId());
      if (!world.isClientSide) {
         this.checkIfExtend(world, x, y, z);
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      Direction placementDirection = side.getDirection();
      world.setBlockMetadataWithNotify(x, y, z, placementDirection.getId());
      if (!world.isClientSide) {
         this.checkIfExtend(world, x, y, z);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      this.checkIfExtend(world, x, y, z);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (!world.isClientSide && world.getTileEntity(x, y, z) == null) {
         this.checkIfExtend(world, x, y, z);
      }
   }

   private void checkIfExtend(World world, int x, int y, int z) {
      int data = world.getBlockMetadata(x, y, z);
      Direction direction = getDirection(data);
      boolean hasNeighborSignal = this.getNeighborSignal(world, x, y, z, direction);
      if (data != 7) {
         if (hasNeighborSignal && !isPowered(data)) {
            if (this.canPushLine(world, x, y, z, direction, this.maxPushedBlocks)) {
               world.setBlockMetadata(x, y, z, direction.getId() | 8);
               world.triggerEvent(x, y, z, 0, direction.getId());
            }
         } else if (!hasNeighborSignal && isPowered(data)) {
            world.setBlockMetadata(x, y, z, direction.getId());
            world.triggerEvent(x, y, z, 1, direction.getId());
         }
      }
   }

   private boolean getNeighborSignal(World world, int x, int y, int z, Direction direction) {
      if (direction != Direction.DOWN && world.getSignal(x, y - 1, z, Side.BOTTOM)) {
         return true;
      } else if (direction != Direction.UP && world.getSignal(x, y + 1, z, Side.TOP)) {
         return true;
      } else if (direction != Direction.NORTH && world.getSignal(x, y, z - 1, Side.NORTH)) {
         return true;
      } else if (direction != Direction.SOUTH && world.getSignal(x, y, z + 1, Side.SOUTH)) {
         return true;
      } else if (direction != Direction.EAST && world.getSignal(x + 1, y, z, Side.EAST)) {
         return true;
      } else if (direction != Direction.WEST && world.getSignal(x - 1, y, z, Side.WEST)) {
         return true;
      } else if (world.getSignal(x, y, z, Side.BOTTOM)) {
         return true;
      } else if (direction != Direction.UP && world.getSignal(x, y + 2, z, Side.TOP)) {
         return true;
      } else if (world.getSignal(x, y + 1, z - 1, Side.NORTH)) {
         return true;
      } else if (world.getSignal(x, y + 1, z + 1, Side.SOUTH)) {
         return true;
      } else {
         return world.getSignal(x - 1, y + 1, z, Side.WEST) ? true : world.getSignal(x + 1, y + 1, z, Side.EAST);
      }
   }

   @Override
   public void triggerEvent(World world, int x, int y, int z, int index, int data) {
      Direction direction = getDirection(data);
      if (direction != Direction.NONE) {
         if (index == 0) {
            this.extendEvent(world, x, y, z, data, direction);
         } else if (index == 1) {
            this.retractEvent(world, x, y, z, data, direction);
         }
      }
   }

   public void extendEvent(World world, int x, int y, int z, int data, Direction direction) {
      if (this.tryExtend(world, x, y, z, direction, this.maxPushedBlocks)) {
         world.setBlockMetadataWithNotify(x, y, z, direction.getId() | 8);
         world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
      }
   }

   public void retractEvent(World world, int x, int y, int z, int data, Direction direction) {
      TileEntity tileEntity = world.getTileEntity(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ());
      if (tileEntity instanceof TileEntityMovingPistonBlock) {
         TileEntityMovingPistonBlock moving = (TileEntityMovingPistonBlock)tileEntity;
         if (!moving.isExtending()) {
            ((TileEntityMovingPistonBlock)tileEntity).finalTick();
         } else {
            world.setBlock(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), 0);
         }
      }

      world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), direction.getId());
      world.replaceBlockTileEntity(x, y, z, BlockLogicPistonMoving.createTileEntity(this.block.id(), direction.getId(), null, direction, false, true));
      world.setBlockWithNotify(x + direction.getOffsetX(), y + direction.getOffsetY(), z + direction.getOffsetZ(), 0);
      world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int data = world.getBlockMetadata(x, y, z);
      if (isPowered(data)) {
         switch (getDirection(data)) {
            case DOWN:
               return AABB.getTemporaryBB(0.0, 0.25, 0.0, 1.0, 1.0, 1.0);
            case UP:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 0.75, 1.0);
            case NORTH:
               return AABB.getTemporaryBB(0.0, 0.0, 0.25, 1.0, 1.0, 1.0);
            case SOUTH:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.75);
            case WEST:
               return AABB.getTemporaryBB(0.25, 0.0, 0.0, 1.0, 1.0, 1.0);
            case EAST:
            default:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 0.75, 1.0, 1.0);
         }
      } else {
         return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   public static Direction getDirection(int data) {
      return Direction.getDirectionById(data & 7);
   }

   public static boolean isPowered(int data) {
      return (data & 8) != 0;
   }

   protected boolean isPushable(int id, World world, int x, int y, int z, boolean canDestroy) {
      Block<?> block = Blocks.getBlock(id);
      if (block == null) {
         return false;
      } else {
         int pushReaction = block.getPistonPushReaction(world, x, y, z);
         return !block.hasTag(BlockTags.PISTON_CRUSHING)
            && !block.getImmovable()
            && pushReaction != 2
            && (canDestroy || pushReaction != 1)
            && block.getHardness() != -1.0F;
      }
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return isPowered(world.getBlockMetadata(x, y, z)) ? 2 : super.getPistonPushReaction(world, x, y, z);
   }

   protected boolean canPushLine(World world, int x, int y, int z, Direction direction, int maxPushedBlocks) {
      int xo = x + direction.getOffsetX();
      int yo = y + direction.getOffsetY();
      int zo = z + direction.getOffsetZ();

      for (int blocks = 0; blocks < maxPushedBlocks + 1; blocks++) {
         if (yo < 0 || yo >= world.getHeightBlocks()) {
            return false;
         }

         int id = world.getBlockId(xo, yo, zo);
         if (id != 0) {
            if (!this.isPushable(id, world, xo, yo, zo, true)) {
               if (blocks != 1 || !BlockTags.PISTON_CRUSHING.appliesTo(Blocks.getBlock(id)) || !this.tryCrush(world, x, y, z, direction)) {
                  return false;
               }
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

      return true;
   }

   public boolean tryCrush(World world, int x, int y, int z, Direction direction) {
      int x2 = x + direction.getOffsetX();
      int y2 = y + direction.getOffsetY();
      int z2 = z + direction.getOffsetZ();
      Block<?> block = world.getBlock(x2, y2, z2);
      if (block == null) {
         return true;
      } else {
         world.playBlockEvent(null, 2001, x2, y2, z2, world.getBlockId(x2, y2, z2));
         block.dropBlockWithCause(world, EnumDropCause.SILK_TOUCH, x2, y2, z2, world.getBlockMetadata(x2, y2, z2), null, null);
         world.setBlockWithNotify(x2, y2, z2, 0);
         return true;
      }
   }

   protected boolean tryExtend(World world, int x, int y, int z, Direction direction, int maxPushedBlocks) {
      int data = world.getBlockMetadata(x, y, z);
      int xo = x + direction.getOffsetX();
      int yo = y + direction.getOffsetY();
      int zo = z + direction.getOffsetZ();
      int blocks = 0;

      while (blocks < maxPushedBlocks + 1) {
         if (yo < 0 || yo >= world.getHeightBlocks()) {
            return false;
         }

         int blockId = world.getBlockId(xo, yo, zo);
         if (blockId == 0) {
            break;
         }

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
         } else {
            Blocks.blocksList[blockId]
               .dropBlockWithCause(world, EnumDropCause.WORLD, xo, yo, zo, world.getBlockMetadata(xo, yo, zo), world.getTileEntity(xo, yo, zo), null);
            break;
         }
      }

      while (xo != x || yo != y || zo != z) {
         int px = xo - direction.getOffsetX();
         int py = yo - direction.getOffsetY();
         int pz = zo - direction.getOffsetZ();
         int pushId = world.getBlockId(px, py, pz);
         int pushMeta = world.getBlockMetadata(px, py, pz);
         TileEntity pushEntity = world.getTileEntity(px, py, pz);
         if (pushEntity instanceof TileEntityMovingPistonBlock && px == x && py == y && pz == z) {
            break;
         }

         if (pushId == this.block.id() && px == x && py == y && pz == z) {
            this.createPistonHeadAt(world, xo, yo, zo, data, direction);
         } else if (pushId == Blocks.PISTON_MOVING.id()) {
            TileEntity old = world.getTileEntity(px, py, pz);
            if (old instanceof TileEntityMovingPistonBlock) {
               TileEntityMovingPistonBlock moving = (TileEntityMovingPistonBlock)old;
               if (!moving.isSourcePiston()) {
                  pushId = moving.getMovedId();
                  pushMeta = moving.getMovedData();
                  pushEntity = moving.getMovedEntity();
               }
            }

            world.removeBlockTileEntity(px, py, pz);
            world.setBlockAndMetadata(xo, yo, zo, Blocks.PISTON_MOVING.id(), pushMeta);
            world.replaceBlockTileEntity(xo, yo, zo, BlockLogicPistonMoving.createTileEntity(pushId, pushMeta, pushEntity, direction, true, false));
         } else {
            world.removeBlockTileEntity(px, py, pz);
            world.setBlockAndMetadata(xo, yo, zo, Blocks.PISTON_MOVING.id(), pushMeta);
            world.replaceBlockTileEntity(xo, yo, zo, BlockLogicPistonMoving.createTileEntity(pushId, pushMeta, pushEntity, direction, true, false));
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

   public void createPistonHeadAt(World world, int x, int y, int z, int data, Direction direction) {
      world.setBlockAndMetadata(x, y, z, Blocks.PISTON_MOVING.id(), BlockLogicPistonHead.setPistonType(0, direction.getId()));
      world.replaceBlockTileEntity(
         x,
         y,
         z,
         BlockLogicPistonMoving.createTileEntity(
            Blocks.PISTON_HEAD.id(), BlockLogicPistonHead.setPistonType(0, direction.getId()), null, direction, true, false
         )
      );
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return 7;
   }
}

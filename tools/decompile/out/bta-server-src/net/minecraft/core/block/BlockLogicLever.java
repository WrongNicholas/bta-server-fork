package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicLever extends BlockLogic {
   public static final int MASK_POWERED = 16;
   public static final int MASK_ROTATION = 15;
   public static final int ROTATION_EAST = 1;
   public static final int ROTATION_WEST = 2;
   public static final int ROTATION_SOUTH = 3;
   public static final int ROTATION_NORTH = 4;
   public static final int ROTATION_TOP_NS = 5;
   public static final int ROTATION_TOP_WE = 6;
   public static final int ROTATION_BOTTOM_NS = 7;
   public static final int ROTATION_BOTTOM_WE = 8;

   public BlockLogicLever(Block<?> block) {
      super(block, Material.decoration);
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
   public boolean canPlaceBlockOnSide(World world, int x, int y, int z, Side side) {
      Side checkSide = side.getOpposite();
      return world.isBlockNormalCube(x + checkSide.getOffsetX(), y + checkSide.getOffsetY(), z + checkSide.getOffsetZ());
   }

   @Override
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      for (Side s : Side.sides) {
         if (world.isBlockNormalCube(x + s.getOffsetX(), y + s.getOffsetY(), z + s.getOffsetZ())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean isPowered = isPowered(meta);
      int rotation = -1;
      switch (side) {
         case BOTTOM:
            if (world.canPlaceOnSurfaceOfBlock(x, y + 1, z)) {
               rotation = 7 + (mob.getHorizontalPlacementDirection(side).getAxis() == Axis.Z ? 1 : 0);
            }
            break;
         case TOP:
            if (world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
               rotation = 5 + (mob.getHorizontalPlacementDirection(side).getAxis() == Axis.Z ? 1 : 0);
            }
            break;
         case NORTH:
            if (world.isBlockNormalCube(x, y, z + 1)) {
               rotation = 4;
            }
            break;
         case SOUTH:
            if (world.isBlockNormalCube(x, y, z - 1)) {
               rotation = 3;
            }
            break;
         case WEST:
            if (world.isBlockNormalCube(x + 1, y, z)) {
               rotation = 2;
            }
            break;
         case EAST:
            if (world.isBlockNormalCube(x - 1, y, z)) {
               rotation = 1;
            }
      }

      if (rotation == -1) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      } else {
         world.setBlockMetadataWithNotify(x, y, z, rotation | (isPowered ? 16 : 0));
      }
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean isPowered = isPowered(meta);
      int rotation = -1;
      switch (side.getOpposite()) {
         case BOTTOM:
            if (world.canPlaceOnSurfaceOfBlock(x, y + 1, z)) {
               rotation = 7;
            }
            break;
         case TOP:
            if (world.canPlaceOnSurfaceOfBlock(x, y - 1, z)) {
               rotation = 5;
            }
            break;
         case NORTH:
            if (world.isBlockNormalCube(x, y, z + 1)) {
               rotation = 4;
            }
            break;
         case SOUTH:
            if (world.isBlockNormalCube(x, y, z - 1)) {
               rotation = 3;
            }
            break;
         case WEST:
            if (world.isBlockNormalCube(x + 1, y, z)) {
               rotation = 2;
            }
            break;
         case EAST:
            if (world.isBlockNormalCube(x - 1, y, z)) {
               rotation = 1;
            }
      }

      if (rotation == -1) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
      } else {
         world.setBlockMetadataWithNotify(x, y, z, rotation | (isPowered ? 16 : 0));
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (this.checkIfAttachedToBlock(world, x, y, z)) {
         int rotation = getRotation(world.getBlockMetadata(x, y, z));
         boolean shouldBreak;
         switch (rotation) {
            case 1:
               shouldBreak = !world.isBlockNormalCube(x - 1, y, z);
               break;
            case 2:
               shouldBreak = !world.isBlockNormalCube(x + 1, y, z);
               break;
            case 3:
               shouldBreak = !world.isBlockNormalCube(x, y, z - 1);
               break;
            case 4:
               shouldBreak = !world.isBlockNormalCube(x, y, z + 1);
               break;
            case 5:
            case 6:
               shouldBreak = !world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
               break;
            case 7:
            case 8:
               shouldBreak = !world.canPlaceOnSurfaceOfBlock(x, y + 1, z);
               break;
            default:
               shouldBreak = false;
         }

         if (shouldBreak) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
            world.setBlockWithNotify(x, y, z, 0);
         }
      }
   }

   private boolean checkIfAttachedToBlock(World world, int x, int y, int z) {
      if (!this.canPlaceBlockAt(world, x, y, z)) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
         world.setBlockWithNotify(x, y, z, 0);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int rotation = getRotation(world.getBlockMetadata(x, y, z));
      float f = 0.1875F;
      if (rotation == 1) {
         return AABB.getTemporaryBB(0.0, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      } else if (rotation == 2) {
         return AABB.getTemporaryBB(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0, 0.8F, 0.5F + f);
      } else if (rotation == 3) {
         return AABB.getTemporaryBB(0.5F - f, 0.2F, 0.0, 0.5F + f, 0.8F, f * 2.0F);
      } else if (rotation == 4) {
         return AABB.getTemporaryBB(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0);
      } else if (rotation != 5 && rotation != 6) {
         float f1 = 0.25F;
         return AABB.getTemporaryBB(0.5F - f1, 0.4, 0.5F - f1, 0.5F + f1, 1.0, 0.5F + f1);
      } else {
         float f1 = 0.25F;
         return AABB.getTemporaryBB(0.5F - f1, 0.0, 0.5F - f1, 0.5F + f1, 0.6F, 0.5F + f1);
      }
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (!Item.hasTag(player.getCurrentEquippedItem(), ItemTags.PREVENT_LEFT_CLICK_INTERACTIONS)) {
         this.onBlockRightClicked(world, x, y, z, player, null, 0.0, 0.0);
      }
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      this.flip(world, x, y, z, player);
      return true;
   }

   public void flip(World world, int x, int y, int z, @Nullable Player player) {
      int meta = world.getBlockMetadata(x, y, z);
      int rotation = getRotation(meta);
      if (isPowered(meta)) {
         world.setBlockMetadataWithNotify(x, y, z, meta & -17);
      } else {
         world.setBlockMetadataWithNotify(x, y, z, meta | 16);
      }

      world.markBlocksDirty(x, y, z, x, y, z);
      world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, isPowered(meta) ? 0.5F : 0.6F);
      world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
      if (rotation == 1) {
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.block.id());
      } else if (rotation == 2) {
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.block.id());
      } else if (rotation == 3) {
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.block.id());
      } else if (rotation == 4) {
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.block.id());
      } else if (rotation != 5 && rotation != 6) {
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.block.id());
      } else {
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.block.id());
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.flip(world, x, y, z, null);
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (isPowered(data)) {
         world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
         int rotation = getRotation(data);
         if (rotation == 1) {
            world.notifyBlocksOfNeighborChange(x - 1, y, z, this.block.id());
         } else if (rotation == 2) {
            world.notifyBlocksOfNeighborChange(x + 1, y, z, this.block.id());
         } else if (rotation == 3) {
            world.notifyBlocksOfNeighborChange(x, y, z - 1, this.block.id());
         } else if (rotation == 4) {
            world.notifyBlocksOfNeighborChange(x, y, z + 1, this.block.id());
         } else if (rotation != 5 && rotation != 6) {
            world.notifyBlocksOfNeighborChange(x, y + 1, z, this.block.id());
         } else {
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.block.id());
         }
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   public static int getRotation(int meta) {
      return meta & 15;
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return isPowered(worldSource.getBlockMetadata(x, y, z));
   }

   public static boolean isPowered(int meta) {
      return (meta & 16) != 0;
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      int meta = world.getBlockMetadata(x, y, z);
      if (!isPowered(meta)) {
         return false;
      } else {
         int rotation = getRotation(meta);
         if (rotation == 8 && side == Side.BOTTOM) {
            return true;
         } else if (rotation == 7 && side == Side.BOTTOM) {
            return true;
         } else if (rotation == 6 && side == Side.TOP) {
            return true;
         } else if (rotation == 5 && side == Side.TOP) {
            return true;
         } else if (rotation == 4 && side == Side.NORTH) {
            return true;
         } else if (rotation == 3 && side == Side.SOUTH) {
            return true;
         } else {
            return rotation == 2 && side == Side.WEST ? true : rotation == 1 && side == Side.EAST;
         }
      }
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }
}

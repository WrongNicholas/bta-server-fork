package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicButton extends BlockLogic implements IPaintable {
   public static final int MASK_POWERED = 8;
   public static final int MASK_DIRECTION = 7;
   public static final int SIDE_WEST = 1;
   public static final int SIDE_EAST = 2;
   public static final int SIDE_NORTH = 3;
   public static final int SIDE_SOUTH = 4;
   public static final int SIDE_TOP = 5;
   public static final int SIDE_BOTTOM = 6;

   public BlockLogicButton(Block<?> block) {
      super(block, Material.decoration);
      block.setTicking(true);
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public int tickDelay() {
      return 20;
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
      if (world.isBlockNormalCube(x, y - 1, z)) {
         return true;
      } else if (world.isBlockNormalCube(x, y + 1, z)) {
         return true;
      } else if (world.isBlockNormalCube(x - 1, y, z)) {
         return true;
      } else if (world.isBlockNormalCube(x + 1, y, z)) {
         return true;
      } else {
         return world.isBlockNormalCube(x, y, z - 1) ? true : world.isBlockNormalCube(x, y, z + 1);
      }
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side side, Mob mob, double xPlaced, double yPlaced) {
      int orientation = -1;
      switch (side) {
         case BOTTOM:
            if (world.isBlockNormalCube(x, y + 1, z)) {
               orientation = 5;
            }
            break;
         case TOP:
            if (world.isBlockNormalCube(x, y - 1, z)) {
               orientation = 6;
            }
            break;
         case NORTH:
            if (world.isBlockNormalCube(x, y, z + 1)) {
               orientation = 4;
            }
            break;
         case SOUTH:
            if (world.isBlockNormalCube(x, y, z - 1)) {
               orientation = 3;
            }
            break;
         case WEST:
            if (world.isBlockNormalCube(x + 1, y, z)) {
               orientation = 2;
            }
            break;
         case EAST:
            if (world.isBlockNormalCube(x - 1, y, z)) {
               orientation = 1;
            }
      }

      if (orientation == -1) {
         orientation = this.getOrientation(world, x, y, z);
      }

      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, meta & -8 | orientation);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      int orientation = -1;
      if (side.isHorizontal()) {
         side = side.getOpposite();
      }

      switch (side) {
         case BOTTOM:
            if (world.isBlockNormalCube(x, y + 1, z)) {
               orientation = 5;
            }
            break;
         case TOP:
            if (world.isBlockNormalCube(x, y - 1, z)) {
               orientation = 6;
            }
            break;
         case NORTH:
            if (world.isBlockNormalCube(x, y, z + 1)) {
               orientation = 4;
            }
            break;
         case SOUTH:
            if (world.isBlockNormalCube(x, y, z - 1)) {
               orientation = 3;
            }
            break;
         case WEST:
            if (world.isBlockNormalCube(x + 1, y, z)) {
               orientation = 2;
            }
            break;
         case EAST:
            if (world.isBlockNormalCube(x - 1, y, z)) {
               orientation = 1;
            }
      }

      if (orientation == -1) {
         orientation = this.getOrientation(world, x, y, z);
      }

      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockMetadataWithNotify(x, y, z, meta & -8 | orientation);
   }

   private int getOrientation(World world, int x, int y, int z) {
      if (world.isBlockNormalCube(x - 1, y, z)) {
         return 1;
      } else if (world.isBlockNormalCube(x + 1, y, z)) {
         return 2;
      } else if (world.isBlockNormalCube(x, y, z - 1)) {
         return 3;
      } else if (world.isBlockNormalCube(x, y, z + 1)) {
         return 4;
      } else if (world.isBlockNormalCube(x, y + 1, z)) {
         return 5;
      } else {
         return world.isBlockNormalCube(x, y - 1, z) ? 6 : 1;
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      int meta = world.getBlockMetadata(x, y, z);
      int side = meta & 7;
      boolean invalid;
      switch (side) {
         case 1:
            invalid = !world.isBlockNormalCube(x - 1, y, z);
            break;
         case 2:
            invalid = !world.isBlockNormalCube(x + 1, y, z);
            break;
         case 3:
            invalid = !world.isBlockNormalCube(x, y, z - 1);
            break;
         case 4:
            invalid = !world.isBlockNormalCube(x, y, z + 1);
            break;
         case 5:
            invalid = !world.isBlockNormalCube(x, y + 1, z);
            break;
         case 6:
            invalid = !world.isBlockNormalCube(x, y - 1, z);
            break;
         default:
            invalid = false;
      }

      if (invalid) {
         this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, meta, null, null);
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      int direction = meta & 7;
      boolean flag = (meta & 8) != 0;
      float f = 0.375F;
      float f1 = 0.625F;
      float f2 = 0.1875F;
      float f3 = 0.125F;
      if (flag) {
         f3 = 0.0625F;
      }

      switch (direction) {
         case 1:
            return AABB.getTemporaryBB(0.0, f, 0.5F - f2, f3, f1, 0.5F + f2);
         case 2:
            return AABB.getTemporaryBB(1.0F - f3, f, 0.5F - f2, 1.0, f1, 0.5F + f2);
         case 3:
            return AABB.getTemporaryBB(0.5F - f2, f, 0.0, 0.5F + f2, f1, f3);
         case 4:
            return AABB.getTemporaryBB(0.5F - f2, f, 1.0F - f3, 0.5F + f2, f1, 1.0);
         case 5:
            return AABB.getTemporaryBB(0.5F - f2, 1.0F - f3, f, 0.5F + f2, 1.0, f1);
         case 6:
            return AABB.getTemporaryBB(0.5F - f2, 0.0, f, 0.5F + f2, f3, f1);
         default:
            return super.getBlockBoundsFromState(world, x, y, z);
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
      return this.press(world, x, y, z, player);
   }

   public boolean press(World world, int x, int y, int z, @Nullable Player player) {
      int meta = world.getBlockMetadata(x, y, z);
      int direction = meta & 7;
      boolean isPowered = (meta & 8) != 0;
      if (isPowered) {
         return true;
      } else {
         world.setBlockMetadataWithNotify(x, y, z, meta & -8 | direction | 8);
         world.markBlocksDirty(x, y, z, x, y, z);
         world.playSoundEffect(player, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.6F);
         world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
         switch (direction) {
            case 1:
               world.notifyBlocksOfNeighborChange(x - 1, y, z, this.block.id());
               break;
            case 2:
               world.notifyBlocksOfNeighborChange(x + 1, y, z, this.block.id());
               break;
            case 3:
               world.notifyBlocksOfNeighborChange(x, y, z - 1, this.block.id());
               break;
            case 4:
               world.notifyBlocksOfNeighborChange(x, y, z + 1, this.block.id());
               break;
            case 5:
               world.notifyBlocksOfNeighborChange(x, y + 1, z, this.block.id());
               break;
            case 6:
               world.notifyBlocksOfNeighborChange(x, y - 1, z, this.block.id());
         }

         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
         return true;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      this.press(world, x, y, z, null);
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if ((data & 8) != 0) {
         world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
         int direction = data & 7;
         switch (direction) {
            case 1:
               world.notifyBlocksOfNeighborChange(x - 1, y, z, this.block.id());
               break;
            case 2:
               world.notifyBlocksOfNeighborChange(x + 1, y, z, this.block.id());
               break;
            case 3:
               world.notifyBlocksOfNeighborChange(x, y, z - 1, this.block.id());
               break;
            case 4:
               world.notifyBlocksOfNeighborChange(x, y, z + 1, this.block.id());
               break;
            case 5:
               world.notifyBlocksOfNeighborChange(x, y + 1, z, this.block.id());
               break;
            case 6:
               world.notifyBlocksOfNeighborChange(x, y - 1, z, this.block.id());
         }
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return (worldSource.getBlockMetadata(x, y, z) & 8) != 0;
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      int meta = world.getBlockMetadata(x, y, z);
      if ((meta & 8) == 0) {
         return false;
      } else {
         int direction = meta & 7;
         switch (direction) {
            case 1:
               return side == Side.EAST;
            case 2:
               return side == Side.WEST;
            case 3:
               return side == Side.SOUTH;
            case 4:
               return side == Side.NORTH;
            case 5:
               return side == Side.BOTTOM;
            case 6:
               return side == Side.TOP;
            default:
               return false;
         }
      }
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         if ((meta & 8) != 0) {
            world.setBlockMetadataWithNotify(x, y, z, meta & -9);
            world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
            int direction = meta & 7;
            switch (direction) {
               case 1:
                  world.notifyBlocksOfNeighborChange(x - 1, y, z, this.block.id());
                  break;
               case 2:
                  world.notifyBlocksOfNeighborChange(x + 1, y, z, this.block.id());
                  break;
               case 3:
                  world.notifyBlocksOfNeighborChange(x, y, z - 1, this.block.id());
                  break;
               case 4:
                  world.notifyBlocksOfNeighborChange(x, y, z + 1, this.block.id());
                  break;
               case 5:
                  world.notifyBlocksOfNeighborChange(x, y + 1, z, this.block.id());
                  break;
               case 6:
                  world.notifyBlocksOfNeighborChange(x, y - 1, z, this.block.id());
            }

            world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.5F);
            world.markBlocksDirty(x, y, z, x, y, z);
         }
      }
   }

   @Override
   public boolean canBePainted() {
      return this.id() == Blocks.BUTTON_PLANKS.id();
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataRaw(x, y, z, Blocks.BUTTON_PLANKS_PAINTED.id(), meta);
      world.setBlockMetadata(x, y, z, meta);
      Blocks.BUTTON_PLANKS_PAINTED.getLogic().setColor(world, x, y, z, color);
      if ((meta & 8) != 0) {
         world.scheduleBlockUpdate(x, y, z, Blocks.BUTTON_PLANKS_PAINTED.id(), this.tickDelay());
      }
   }
}

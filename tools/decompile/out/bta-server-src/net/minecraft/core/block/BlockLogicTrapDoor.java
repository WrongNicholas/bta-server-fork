package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.PlacementMode;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tag.ItemTags;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicTrapDoor extends BlockLogic implements IPaintable {
   public static final int DIRECTION_SOUTH = 0;
   public static final int DIRECTION_NORTH = 1;
   public static final int DIRECTION_EAST = 2;
   public static final int DIRECTION_WEST = 3;
   public static final int MASK_DIRECTION = 3;
   public static final int MASK_OPEN = 4;
   public static final int MASK_UPPER_HALF = 8;

   public BlockLogicTrapDoor(Block<?> block, Material material) {
      super(block, material);
      float f = 0.5F;
      float f1 = 1.0F;
      this.setBlockBounds(0.5F - f, 0.0, 0.5F - f, 0.5F + f, f1, 0.5F + f);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      int meta = world.getBlockMetadata(x, y, z);
      boolean isOpened = isTrapdoorOpen(meta);
      boolean isPowered = world.hasNeighborSignal(x, y, z);
      if (isOpened != isPowered) {
         world.setBlockMetadataWithNotify(x, y, z, setTrapdoorOpen(meta, isPowered));
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
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      float thickness = 0.1875F;
      if (isTrapdoorOpen(meta)) {
         switch (meta & 3) {
            case 0:
               return AABB.getTemporaryBB(0.0, 0.0, 1.0F - thickness, 1.0, 1.0, 1.0);
            case 1:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, 1.0, thickness);
            case 2:
               return AABB.getTemporaryBB(1.0F - thickness, 0.0, 0.0, 1.0, 1.0, 1.0);
            case 3:
            default:
               return AABB.getTemporaryBB(0.0, 0.0, 0.0, thickness, 1.0, 1.0);
         }
      } else {
         return isUpperHalf(meta) ? AABB.getTemporaryBB(0.0, 1.0F - thickness, 0.0, 1.0, 1.0, 1.0) : AABB.getTemporaryBB(0.0, 0.0, 0.0, 1.0, thickness, 1.0);
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
      if (this.material != Material.metal && this.material != Material.steel) {
         int l = world.getBlockMetadata(x, y, z);
         world.setBlockMetadataWithNotify(x, y, z, l ^ 4);
         world.playBlockEvent(player, 1003, x, y, z, 0);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      if (this.material != Material.metal && this.material != Material.steel) {
         int l = world.getBlockMetadata(x, y, z);
         world.setBlockMetadataWithNotify(x, y, z, l ^ 4);
         world.playBlockEvent(null, 1003, x, y, z, 0);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide) {
         Block<?> block = Blocks.getBlock(blockId);
         if (this.material != Material.metal && this.material != Material.steel && block != null && block.isSignalSource()
            || (this.material == Material.metal || this.material == Material.steel) && (block == null || block.isSignalSource())) {
            int meta = world.getBlockMetadata(x, y, z);
            boolean isOpened = isTrapdoorOpen(meta);
            boolean isPowered = world.hasNeighborSignal(x, y, z);
            if (isOpened != isPowered) {
               world.setBlockMetadataWithNotify(x, y, z, setTrapdoorOpen(meta, isPowered));
               world.playBlockEvent(null, 1003, x, y, z, 0);
            }
         }
      }
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      int meta = this.getMetaForDirection(player == null ? side.getDirection().getOpposite() : player.getHorizontalPlacementDirection(side, PlacementMode.SIDE));
      if (player != null && player.getVerticalPlacementDirection(side, yPlaced) == Direction.UP) {
         meta = setUpperHalf(meta, true);
      }

      return meta;
   }

   @Override
   public boolean isClimbable(World world, int x, int y, int z) {
      Block<?> block = world.getBlock(x, y - 1, z);
      int meta = world.getBlockMetadata(x, y, z);
      if (isTrapdoorOpen(meta) && block != null && block.getLogic() instanceof BlockLogicLadder) {
         BlockLogicLadder ladder = (BlockLogicLadder)block.getLogic();
         Side ladderSide = ladder.getSideFromMeta(world.getBlockMetadata(x, y - 1, z));
         Direction trapdoorDirection = this.getDirectionForMeta(meta);
         return ladderSide.getDirection() == trapdoorDirection.getOpposite();
      } else {
         return false;
      }
   }

   public static boolean isTrapdoorOpen(int metadata) {
      return (metadata & 4) != 0;
   }

   public static int setTrapdoorOpen(int metadata, boolean isOpen) {
      if (isOpen) {
         metadata |= 4;
      } else {
         metadata &= -5;
      }

      return metadata;
   }

   public static boolean isUpperHalf(int meta) {
      return (meta & 8) != 0;
   }

   public static int setUpperHalf(int meta, boolean isUpperHalf) {
      if (isUpperHalf) {
         meta |= 8;
      } else {
         meta &= -9;
      }

      return meta;
   }

   public int getMetaForDirection(Direction dir) {
      switch (dir) {
         case SOUTH:
            return 0;
         case NORTH:
            return 1;
         case EAST:
            return 2;
         case WEST:
            return 3;
         default:
            return 0;
      }
   }

   public Direction getDirectionForMeta(int meta) {
      switch (meta & 3) {
         case 0:
            return Direction.SOUTH;
         case 1:
            return Direction.NORTH;
         case 2:
            return Direction.EAST;
         case 3:
            return Direction.WEST;
         default:
            return Direction.NONE;
      }
   }

   @Override
   public boolean canBePainted() {
      return this.material == Material.wood;
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataWithNotify(x, y, z, Blocks.TRAPDOOR_PLANKS_PAINTED.id(), meta & 15 | color.blockMeta << 4);
   }
}

package net.minecraft.core.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerCompound;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BlockLogicChest extends BlockLogic implements IPaintable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int directionBits = 3;
   public static final int directionOffset = 0;
   public static final int typeBits = 12;
   public static final int typeOffset = 2;

   public BlockLogicChest(Block<?> block, Material material) {
      super(block, material);
      block.withEntity(TileEntityChest::new);
   }

   @Override
   public void onBlockPlacedByMob(World world, int x, int y, int z, @NotNull Side placeSide, Mob mob, double xPlaced, double yPlaced) {
      Direction direction = mob.getHorizontalPlacementDirection(placeSide).getOpposite();
      BlockLogicChest.Type type = BlockLogicChest.Type.SINGLE;
      if (mob.isSneaking() && placeSide.isHorizontal() && (mob.rotationLockHorizontal == null || mob.rotationLockHorizontal == Direction.NONE)) {
         int placedOnX = x;
         int placedOnZ = z;
         if (placeSide == Side.NORTH) {
            placedOnZ = z + 1;
         }

         if (placeSide == Side.SOUTH) {
            placedOnZ--;
         }

         if (placeSide == Side.EAST) {
            placedOnX = x - 1;
         }

         if (placeSide == Side.WEST) {
            placedOnX++;
         }

         if (isSingleChest(world, placedOnX, y, placedOnZ)) {
            Direction direction2 = getDirectionFromMeta(world.getBlockMetadata(placedOnX, y, placedOnZ));
            if (direction2 == Direction.NORTH) {
               if (placeSide == Side.EAST) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.RIGHT);
                  type = BlockLogicChest.Type.LEFT;
                  direction = direction2;
               }

               if (placeSide == Side.WEST) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.LEFT);
                  type = BlockLogicChest.Type.RIGHT;
                  direction = direction2;
               }
            }

            if (direction2 == Direction.EAST) {
               if (placeSide == Side.SOUTH) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.RIGHT);
                  type = BlockLogicChest.Type.LEFT;
                  direction = direction2;
               }

               if (placeSide == Side.NORTH) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.LEFT);
                  type = BlockLogicChest.Type.RIGHT;
                  direction = direction2;
               }
            }

            if (direction2 == Direction.SOUTH) {
               if (placeSide == Side.EAST) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.LEFT);
                  type = BlockLogicChest.Type.RIGHT;
                  direction = direction2;
               }

               if (placeSide == Side.WEST) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.RIGHT);
                  type = BlockLogicChest.Type.LEFT;
                  direction = direction2;
               }
            }

            if (direction2 == Direction.WEST) {
               if (placeSide == Side.SOUTH) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.LEFT);
                  type = BlockLogicChest.Type.RIGHT;
                  direction = direction2;
               }

               if (placeSide == Side.NORTH) {
                  setType(world, placedOnX, y, placedOnZ, BlockLogicChest.Type.RIGHT);
                  type = BlockLogicChest.Type.LEFT;
                  direction = direction2;
               }
            }
         }
      } else if (!mob.isSneaking()) {
         if (direction == Direction.NORTH) {
            if (isSingleChestWithDirection(world, x - 1, y, z, direction) && !isSingleChestWithDirection(world, x + 1, y, z, direction)) {
               type = BlockLogicChest.Type.LEFT;
               setType(world, x - 1, y, z, BlockLogicChest.Type.RIGHT);
            }

            if (isSingleChestWithDirection(world, x + 1, y, z, direction) && !isSingleChestWithDirection(world, x - 1, y, z, direction)) {
               type = BlockLogicChest.Type.RIGHT;
               setType(world, x + 1, y, z, BlockLogicChest.Type.LEFT);
            }
         }

         if (direction == Direction.EAST) {
            if (isSingleChestWithDirection(world, x, y, z - 1, direction) && !isSingleChestWithDirection(world, x, y, z + 1, direction)) {
               type = BlockLogicChest.Type.LEFT;
               setType(world, x, y, z - 1, BlockLogicChest.Type.RIGHT);
            }

            if (isSingleChestWithDirection(world, x, y, z + 1, direction) && !isSingleChestWithDirection(world, x, y, z - 1, direction)) {
               type = BlockLogicChest.Type.RIGHT;
               setType(world, x, y, z + 1, BlockLogicChest.Type.LEFT);
            }
         }

         if (direction == Direction.SOUTH) {
            if (isSingleChestWithDirection(world, x - 1, y, z, direction) && !isSingleChestWithDirection(world, x + 1, y, z, direction)) {
               type = BlockLogicChest.Type.RIGHT;
               setType(world, x - 1, y, z, BlockLogicChest.Type.LEFT);
            }

            if (isSingleChestWithDirection(world, x + 1, y, z, direction) && !isSingleChestWithDirection(world, x - 1, y, z, direction)) {
               type = BlockLogicChest.Type.LEFT;
               setType(world, x + 1, y, z, BlockLogicChest.Type.RIGHT);
            }
         }

         if (direction == Direction.WEST) {
            if (isSingleChestWithDirection(world, x, y, z - 1, direction) && !isSingleChestWithDirection(world, x, y, z + 1, direction)) {
               type = BlockLogicChest.Type.RIGHT;
               setType(world, x, y, z - 1, BlockLogicChest.Type.LEFT);
            }

            if (isSingleChestWithDirection(world, x, y, z + 1, direction) && !isSingleChestWithDirection(world, x, y, z - 1, direction)) {
               type = BlockLogicChest.Type.LEFT;
               setType(world, x, y, z + 1, BlockLogicChest.Type.RIGHT);
            }
         }
      }

      int meta = world.getBlockMetadata(x, y, z);
      meta = getMetaWithDirection(meta, direction);
      meta = getMetaWithType(meta, type);
      world.setBlockMetadata(x, y, z, meta);
   }

   @Override
   public void onBlockPlacedOnSide(World world, int x, int y, int z, @NotNull Side side, double xPlaced, double yPlaced) {
      Direction direction = side.getDirection();
      BlockLogicChest.Type type = BlockLogicChest.Type.SINGLE;
      if (direction == Direction.NORTH) {
         if (isSingleChestWithDirection(world, x - 1, y, z, direction) && !isSingleChestWithDirection(world, x + 1, y, z, direction)) {
            type = BlockLogicChest.Type.LEFT;
            setType(world, x - 1, y, z, BlockLogicChest.Type.RIGHT);
         }

         if (isSingleChestWithDirection(world, x + 1, y, z, direction) && !isSingleChestWithDirection(world, x - 1, y, z, direction)) {
            type = BlockLogicChest.Type.RIGHT;
            setType(world, x + 1, y, z, BlockLogicChest.Type.LEFT);
         }
      }

      if (direction == Direction.EAST) {
         if (isSingleChestWithDirection(world, x, y, z - 1, direction) && !isSingleChestWithDirection(world, x, y, z + 1, direction)) {
            type = BlockLogicChest.Type.LEFT;
            setType(world, x, y, z - 1, BlockLogicChest.Type.RIGHT);
         }

         if (isSingleChestWithDirection(world, x, y, z + 1, direction) && !isSingleChestWithDirection(world, x, y, z - 1, direction)) {
            type = BlockLogicChest.Type.RIGHT;
            setType(world, x, y, z + 1, BlockLogicChest.Type.LEFT);
         }
      }

      if (direction == Direction.SOUTH) {
         if (isSingleChestWithDirection(world, x - 1, y, z, direction) && !isSingleChestWithDirection(world, x + 1, y, z, direction)) {
            type = BlockLogicChest.Type.RIGHT;
            setType(world, x - 1, y, z, BlockLogicChest.Type.LEFT);
         }

         if (isSingleChestWithDirection(world, x + 1, y, z, direction) && !isSingleChestWithDirection(world, x - 1, y, z, direction)) {
            type = BlockLogicChest.Type.LEFT;
            setType(world, x + 1, y, z, BlockLogicChest.Type.RIGHT);
         }
      }

      if (direction == Direction.WEST) {
         if (isSingleChestWithDirection(world, x, y, z - 1, direction) && !isSingleChestWithDirection(world, x, y, z + 1, direction)) {
            type = BlockLogicChest.Type.RIGHT;
            setType(world, x, y, z - 1, BlockLogicChest.Type.LEFT);
         }

         if (isSingleChestWithDirection(world, x, y, z + 1, direction) && !isSingleChestWithDirection(world, x, y, z - 1, direction)) {
            type = BlockLogicChest.Type.LEFT;
            setType(world, x, y, z + 1, BlockLogicChest.Type.RIGHT);
         }
      }

      int meta = world.getBlockMetadata(x, y, z);
      meta = getMetaWithDirection(meta, direction);
      meta = getMetaWithType(meta, type);
      world.setBlockMetadata(x, y, z, meta);
   }

   public void checkIfOtherHalfExists(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      BlockLogicChest.Type type = getTypeFromMeta(meta);
      if (type != BlockLogicChest.Type.SINGLE) {
         Direction direction = getDirectionFromMeta(meta);
         int otherChestX = x;
         int otherChestZ = z;
         if (direction == Direction.NORTH) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestX = x - 1;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestX++;
            }
         }

         if (direction == Direction.EAST) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestZ = z - 1;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestZ++;
            }
         }

         if (direction == Direction.SOUTH) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestX++;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestX--;
            }
         }

         if (direction == Direction.WEST) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestZ++;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestZ--;
            }
         }

         boolean valid = false;
         if (isChest(world, otherChestX, y, otherChestZ)) {
            int otherMeta = world.getBlockMetadata(otherChestX, y, otherChestZ);
            if (getDirectionFromMeta(otherMeta) == direction) {
               BlockLogicChest.Type otherType = getTypeFromMeta(otherMeta);
               if (type == BlockLogicChest.Type.LEFT && otherType == BlockLogicChest.Type.RIGHT
                  || type == BlockLogicChest.Type.RIGHT && otherType == BlockLogicChest.Type.LEFT) {
                  valid = true;
               }
            }
         }

         if (!valid) {
            setType(world, x, y, z, BlockLogicChest.Type.SINGLE);
            world.markBlocksDirty(x, y, z, x, y, z);
         }
      }
   }

   public static void setDefaultDirection(World world, int x, int y, int z) {
      if (!world.isClientSide) {
         int bN = world.getBlockId(x, y, z - 1);
         int bS = world.getBlockId(x, y, z + 1);
         int bW = world.getBlockId(x - 1, y, z);
         int bE = world.getBlockId(x + 1, y, z);
         Direction direction = Direction.NORTH;
         if (Blocks.solid[bN] && !Blocks.solid[bS]) {
            direction = Direction.SOUTH;
         }

         if (Blocks.solid[bS] && !Blocks.solid[bN]) {
            direction = Direction.NORTH;
         }

         if (Blocks.solid[bW] && !Blocks.solid[bE]) {
            direction = Direction.EAST;
         }

         if (Blocks.solid[bE] && !Blocks.solid[bW]) {
            direction = Direction.WEST;
         }

         world.setBlockMetadataWithNotify(
            x, y, z, getMetaWithType(getMetaWithDirection(world.getBlockMetadata(x, y, z), direction), BlockLogicChest.Type.SINGLE)
         );
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      super.onNeighborBlockChange(world, x, y, z, blockId);
      this.checkIfOtherHalfExists(world, x, y, z);
   }

   public static boolean isChest(World world, int x, int y, int z) {
      Block<?> b;
      return (b = Blocks.blocksList[world.getBlockId(x, y, z)]) != null && b.getLogic() instanceof BlockLogicChest;
   }

   public static boolean isSingleChest(World world, int x, int y, int z) {
      return isChest(world, x, y, z) && getTypeFromMeta(world.getBlockMetadata(x, y, z)) == BlockLogicChest.Type.SINGLE;
   }

   public static boolean isSingleChestWithDirection(World world, int x, int y, int z, Direction direction) {
      int meta = world.getBlockMetadata(x, y, z);
      return isChest(world, x, y, z) && getTypeFromMeta(meta) == BlockLogicChest.Type.SINGLE && getDirectionFromMeta(meta) == direction;
   }

   public void setDirection(World world, int x, int y, int z, Direction direction) {
      if (isChest(world, x, y, z)) {
         world.setBlockMetadataWithNotify(x, y, z, getMetaWithDirection(world.getBlockMetadata(x, y, z), direction));
      }
   }

   public static void setType(World world, int x, int y, int z, BlockLogicChest.Type type) {
      if (isChest(world, x, y, z)) {
         world.setBlockMetadataWithNotify(x, y, z, getMetaWithType(world.getBlockMetadata(x, y, z), type));
      }
   }

   public static Direction getDirection(World world, int x, int y, int z) {
      return isChest(world, x, y, z) ? getDirectionFromMeta(world.getBlockMetadata(x, y, z)) : null;
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.isClientSide) {
         return true;
      } else {
         this.checkIfOtherHalfExists(world, x, y, z);
         player.displayChestScreen(getInventory(world, x, y, z), x, y, z);
         return true;
      }
   }

   public static Container getInventory(World world, int x, int y, int z) {
      Container inventory = (Container)world.getTileEntity(x, y, z);
      int meta = world.getBlockMetadata(x, y, z);
      BlockLogicChest.Type type = getTypeFromMeta(meta);
      if (type != BlockLogicChest.Type.SINGLE) {
         Container inv2 = null;
         Direction direction = getDirectionFromMeta(meta);
         int otherChestX = x;
         int otherChestZ = z;
         if (direction == Direction.NORTH) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestX = x - 1;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestX++;
            }
         }

         if (direction == Direction.EAST) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestZ = z - 1;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestZ++;
            }
         }

         if (direction == Direction.SOUTH) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestX++;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestX--;
            }
         }

         if (direction == Direction.WEST) {
            if (type == BlockLogicChest.Type.LEFT) {
               otherChestZ++;
            }

            if (type == BlockLogicChest.Type.RIGHT) {
               otherChestZ--;
            }
         }

         if (isChest(world, otherChestX, y, otherChestZ)) {
            int otherMeta = world.getBlockMetadata(otherChestX, y, otherChestZ);
            if (getDirectionFromMeta(otherMeta) == direction) {
               BlockLogicChest.Type otherType = getTypeFromMeta(otherMeta);
               if (type == BlockLogicChest.Type.LEFT && otherType == BlockLogicChest.Type.RIGHT) {
                  inv2 = (Container)world.getTileEntity(otherChestX, y, otherChestZ);
               }

               if (type == BlockLogicChest.Type.RIGHT && otherType == BlockLogicChest.Type.LEFT) {
                  inv2 = inventory;
                  inventory = (Container)world.getTileEntity(otherChestX, y, otherChestZ);
               }
            }
         }

         if (inv2 != null) {
            inventory = new ContainerCompound("container.chest.large.name", inventory, inv2);
         }
      }

      return inventory;
   }

   public static int getMetaWithDirection(int meta, Direction direction) {
      if (direction == null) {
         return meta;
      } else {
         meta &= -4;
         return meta | direction.ordinal() << 0 & 3;
      }
   }

   public static int getMetaWithType(int meta, BlockLogicChest.Type type) {
      if (type == null) {
         return meta;
      } else {
         meta &= -13;
         return meta | type.ordinal() << 2 & 12;
      }
   }

   public static Direction getDirectionFromMeta(int meta) {
      meta &= 3;
      switch (meta) {
         case 0:
            return Direction.NORTH;
         case 1:
            return Direction.EAST;
         case 2:
            return Direction.SOUTH;
         case 3:
            return Direction.WEST;
         default:
            return Direction.NONE;
      }
   }

   public static BlockLogicChest.Type getTypeFromMeta(int meta) {
      return BlockLogicChest.Type.get(meta >> 2 & 3);
   }

   @Override
   public void setColor(World world, int x, int y, int z, DyeColor color) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadataRaw(x, y, z, Blocks.CHEST_PLANKS_OAK_PAINTED.id(), meta);
      world.setBlockMetadata(x, y, z, meta);
      ((BlockLogicChestPainted)Blocks.CHEST_PLANKS_OAK_PAINTED.getLogic()).setColor(world, x, y, z, color);
   }

   public static enum Type {
      SINGLE,
      LEFT,
      RIGHT;

      public static BlockLogicChest.Type get(int i) {
         if (i < 0 || i >= values().length) {
            i = 0;
         }

         return values()[i];
      }
   }
}

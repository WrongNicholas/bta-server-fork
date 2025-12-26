package net.minecraft.core.block;

import net.minecraft.core.achievement.Achievements;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.EnumSleepStatus;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinates;

public class BlockLogicBed extends BlockLogic {
   public static final int MASK_IS_FOOT = 8;
   public static final int MASK_IS_OCCUPIED = 4;
   public static final int MASK_DIRECTION = 3;
   public static final int[] headOfBedMap = new int[]{3, 4, 2, 5};
   public static final int[] footToHeadMap = new int[]{2, 3, 0, 1};
   public static final int[][] bedDirection = new int[][]{{1, 0, 3, 2, 5, 4}, {1, 0, 5, 4, 2, 3}, {1, 0, 2, 3, 4, 5}, {1, 0, 4, 5, 3, 2}};
   public static final Side[] headBlockToFootBlockMap = new Side[]{Side.SOUTH, Side.WEST, Side.NORTH, Side.EAST};

   public BlockLogicBed(Block<?> block) {
      super(block, Material.wood);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.isClientSide) {
         return true;
      } else {
         int meta = world.getBlockMetadata(x, y, z);
         if (!isBlockFootOfBed(meta)) {
            int dir = getDirection(meta);
            x += headBlockToFootBlockMap[dir].getOffsetX();
            z += headBlockToFootBlockMap[dir].getOffsetZ();
            if (world.getBlockId(x, y, z) != this.block.id()) {
               return true;
            }

            meta = world.getBlockMetadata(x, y, z);
         }

         if (!world.worldType.mayRespawn()) {
            world.setBlockWithNotify(x, y, z, 0);
            int dir = getDirection(meta);
            x += headBlockToFootBlockMap[dir].getOffsetX();
            z += headBlockToFootBlockMap[dir].getOffsetZ();
            if (world.getBlockId(x, y, z) == this.block.id()) {
               world.setBlockWithNotify(x, y, z, 0);
            }

            player.addStat(Achievements.SLEEP_NETHER, 1);
            world.createExplosion(null, x + 0.5F, y + 0.5F, z + 0.5F, 5.0F, true, false);
            return true;
         } else {
            if (isBedOccupied(meta)) {
               Player player1 = null;

               for (Player p : world.players) {
                  if (p.isPlayerSleeping()) {
                     ChunkCoordinates pos = p.bedChunkCoordinates;
                     if (pos.x == x && pos.y == y && pos.z == z) {
                        player1 = p;
                     }
                  }
               }

               if (player1 != null) {
                  player.sendTranslatedChatMessage("bed.occupied");
                  return true;
               }

               setBedOccupied(world, x, y, z, false);
            }

            if (player.sleepInBedAt(x, y, z) == EnumSleepStatus.OK) {
               setBedOccupied(world, x, y, z, true);
               return true;
            } else {
               return true;
            }
         }
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      this.removeOtherHalf(world, x, y, z);
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      this.removeOtherHalf(world, x, y, z);
   }

   protected void removeOtherHalf(World world, int x, int y, int z) {
      int metadata = world.getBlockMetadata(x, y, z);
      int direction = getDirection(metadata);
      if (isBlockFootOfBed(metadata)) {
         if (world.getBlockId(x - headBlockToFootBlockMap[direction].getOffsetX(), y, z - headBlockToFootBlockMap[direction].getOffsetZ()) != this.block.id()) {
            world.setBlockWithNotify(x, y, z, 0);
         }
      } else if (world.getBlockId(x + headBlockToFootBlockMap[direction].getOffsetX(), y, z + headBlockToFootBlockMap[direction].getOffsetZ())
         != this.block.id()) {
         world.setBlockWithNotify(x, y, z, 0);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.BED)};
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 1;
   }

   public static boolean isBlockFootOfBed(int meta) {
      return (meta & 8) != 0;
   }

   public static boolean isBedOccupied(int meta) {
      return (meta & 4) != 0;
   }

   public static int getDirection(int meta) {
      return meta & 3;
   }

   public static void setBedOccupied(World world, int x, int y, int z, boolean flag) {
      int l = world.getBlockMetadata(x, y, z);
      if (flag) {
         l |= 4;
      } else {
         l &= -5;
      }

      world.setBlockMetadataWithNotify(x, y, z, l);
   }

   public static ChunkCoordinates getNearestEmptyChunkCoordinates(World world, int x, int y, int z, int l) {
      int meta = world.getBlockMetadata(x, y, z);
      int direction = getDirection(meta);

      for (int k1 = 0; k1 <= 1; k1++) {
         int l1 = x - headBlockToFootBlockMap[direction].getOffsetX() * k1 - 1;
         int i2 = z - headBlockToFootBlockMap[direction].getOffsetZ() * k1 - 1;
         int j2 = l1 + 2;
         int k2 = i2 + 2;

         for (int l2 = l1; l2 <= j2; l2++) {
            for (int i3 = i2; i3 <= k2; i3++) {
               if (world.isBlockNormalCube(l2, y - 1, i3) && world.isAirBlock(l2, y, i3) && world.isAirBlock(l2, y + 1, i3)) {
                  if (l <= 0) {
                     return new ChunkCoordinates(l2, y, i3);
                  }

                  l--;
               }
            }
         }
      }

      return null;
   }
}

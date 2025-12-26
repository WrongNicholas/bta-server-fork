package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.season.Season;
import org.jetbrains.annotations.Nullable;

public class BlockLogicLeavesBase extends BlockLogic {
   public static final int MASK_DECAY_DATA = 15;
   public static final int NUM_DECAY_BITS = 4;
   public static final int DECAY_FLAG_BIT = 3;
   public static final int PERMANENT_FLAG_BIT = 0;
   protected Block<?> sapling;
   public static boolean enableTreeShadowing = false;
   public static boolean enableDecay = true;
   int[] adjacentTreeBlocks;

   public BlockLogicLeavesBase(Block<?> block, Material material, Block<?> sapling) {
      super(block, material);
      block.setTicking(true);
      this.sapling = sapling;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      Season season = world.getSeasonManager().getCurrentSeason();
      float dropRate = season != null ? 20.0F / season.saplingDropFactor : 20.0F;
      if (dropCause != EnumDropCause.PICK_BLOCK && dropCause != EnumDropCause.SILK_TOUCH) {
         int numDropped = 1;
         return world.rand.nextInt(MathHelper.floor(dropRate)) != 0 ? null : new ItemStack[]{new ItemStack(this.getSapling(), numDropped)};
      } else {
         return new ItemStack[]{new ItemStack(this.block)};
      }
   }

   protected Block<?> getSapling() {
      return this.sapling;
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (enableDecay) {
         int radius = 1;
         int chunkRadius = radius + 1;
         if (world.areBlocksLoaded(x - chunkRadius, y - chunkRadius, z - chunkRadius, x + chunkRadius, y + chunkRadius, z + chunkRadius)) {
            for (int dx = -radius; dx <= radius; dx++) {
               for (int dy = -radius; dy <= radius; dy++) {
                  for (int dz = -radius; dz <= radius; dz++) {
                     Block<?> block = world.getBlock(x + dx, y + dy, z + dz);
                     if (block != null && block.getLogic() instanceof BlockLogicLeavesBase) {
                        int meta = world.getBlockMetadata(x + dx, y + dy, z + dz);
                        if (!isPermanent(meta)) {
                           world.setBlockMetadata(x + dx, y + dy, z + dz, setDecaying(meta, true));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         if (isDecaying(meta) && !isPermanent(meta)) {
            byte byte0 = 4;
            int offset = byte0 + 1;
            byte ySpan = 32;
            int xSpan = ySpan * ySpan;
            int halfLength = ySpan / 2;
            if (this.adjacentTreeBlocks == null) {
               this.adjacentTreeBlocks = new int[ySpan * ySpan * ySpan];
            }

            if (world.areBlocksLoaded(x - offset, y - offset, z - offset, x + offset, y + offset, z + offset)) {
               for (int xOffset = -byte0; xOffset <= byte0; xOffset++) {
                  for (int yOffset = -byte0; yOffset <= byte0; yOffset++) {
                     for (int zOffset = -byte0; zOffset <= byte0; zOffset++) {
                        Block<?> block = world.getBlock(x + xOffset, y + yOffset, z + zOffset);
                        if (block != null && block.getLogic() instanceof BlockLogicLog) {
                           this.adjacentTreeBlocks[(xOffset + halfLength) * xSpan + (yOffset + halfLength) * ySpan + zOffset + halfLength] = 0;
                        } else if (block != null && block.getLogic() instanceof BlockLogicLeavesBase) {
                           this.adjacentTreeBlocks[(xOffset + halfLength) * xSpan + (yOffset + halfLength) * ySpan + zOffset + halfLength] = -2;
                        } else {
                           this.adjacentTreeBlocks[(xOffset + halfLength) * xSpan + (yOffset + halfLength) * ySpan + zOffset + halfLength] = -1;
                        }
                     }
                  }
               }

               for (int i2 = 1; i2 <= 4; i2++) {
                  for (int dx = -byte0; dx <= byte0; dx++) {
                     for (int dy = -byte0; dy <= byte0; dy++) {
                        for (int dz = -byte0; dz <= byte0; dz++) {
                           if (this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength) * ySpan + dz + halfLength] == i2 - 1) {
                              if (this.adjacentTreeBlocks[(dx + halfLength - 1) * xSpan + (dy + halfLength) * ySpan + dz + halfLength] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength - 1) * xSpan + (dy + halfLength) * ySpan + dz + halfLength] = i2;
                              }

                              if (this.adjacentTreeBlocks[(dx + halfLength + 1) * xSpan + (dy + halfLength) * ySpan + dz + halfLength] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength + 1) * xSpan + (dy + halfLength) * ySpan + dz + halfLength] = i2;
                              }

                              if (this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength - 1) * ySpan + dz + halfLength] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength - 1) * ySpan + dz + halfLength] = i2;
                              }

                              if (this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength + 1) * ySpan + dz + halfLength] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength + 1) * ySpan + dz + halfLength] = i2;
                              }

                              if (this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength) * ySpan + (dz + halfLength - 1)] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength) * ySpan + (dz + halfLength - 1)] = i2;
                              }

                              if (this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength) * ySpan + dz + halfLength + 1] == -2) {
                                 this.adjacentTreeBlocks[(dx + halfLength) * xSpan + (dy + halfLength) * ySpan + dz + halfLength + 1] = i2;
                              }
                           }
                        }
                     }
                  }
               }
            }

            int j2 = this.adjacentTreeBlocks[halfLength * xSpan + halfLength * ySpan + halfLength];
            if (j2 >= 0) {
               world.setBlockMetadata(x, y, z, setDecaying(meta, false));
            } else {
               this.removeLeaves(world, x, y, z);
            }
         }
      }
   }

   private void removeLeaves(World world, int x, int y, int z) {
      this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
      world.setBlockWithNotify(x, y, z, 0);
   }

   public static boolean isPermanent(int meta) {
      return (meta & 1) != 0;
   }

   public static int setPermanent(int meta, boolean permanent) {
      return permanent ? meta | 1 : meta & -2;
   }

   public static boolean isDecaying(int meta) {
      return (meta & 8) != 0;
   }

   public static int setDecaying(int meta, boolean decaying) {
      return decaying ? meta | 8 : meta & -9;
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return 1;
   }

   @Override
   public float getAmbientOcclusionStrength(WorldSource blockAccess, int x, int y, int z) {
      return enableTreeShadowing ? 0.8F : 0.0F;
   }
}

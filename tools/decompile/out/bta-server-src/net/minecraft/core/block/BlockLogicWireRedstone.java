package net.minecraft.core.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Axis;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.ChunkPosition;

public class BlockLogicWireRedstone extends BlockLogic {
   private boolean shouldSignal = true;
   private final Set<ChunkPosition> toUpdate = new HashSet<>();

   public BlockLogicWireRedstone(Block<?> block) {
      super(block, Material.decoration);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
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
   public boolean canPlaceBlockAt(World world, int x, int y, int z) {
      return world.canPlaceOnSurfaceOfBlock(x, y - 1, z);
   }

   private void updatePowerStrength(World world, int x, int y, int z) {
      this.updatePowerStrength(world, x, y, z, x, y, z);
      ArrayList<ChunkPosition> toUpdate = new ArrayList<>(this.toUpdate);
      this.toUpdate.clear();

      for (ChunkPosition pos : toUpdate) {
         world.notifyBlocksOfNeighborChange(pos.x, pos.y, pos.z, this.id());
      }
   }

   private void updatePowerStrength(World world, int x0, int y0, int z0, int x1, int y1, int z1) {
      int strength = world.getBlockMetadata(x0, y0, z0);
      int newStrength = 0;
      this.shouldSignal = false;
      boolean hasNeighborSignal = world.hasNeighborSignal(x0, y0, z0);
      this.shouldSignal = true;
      if (hasNeighborSignal) {
         newStrength = 15;
      } else {
         for (int i = 0; i < 4; i++) {
            int xi = x0;
            int zi = z0;
            if (i == 0) {
               xi = x0 - 1;
            }

            if (i == 1) {
               xi++;
            }

            if (i == 2) {
               zi = z0 - 1;
            }

            if (i == 3) {
               zi++;
            }

            if (xi != x1 || y0 != y1 || zi != z1) {
               newStrength = this.checkTarget(world, xi, y0, zi, newStrength);
            }

            if (world.isBlockNormalCube(xi, y0, zi) && !world.isBlockNormalCube(x0, y0 + 1, z0)) {
               if (xi != x1 || y0 + 1 != y1 || zi != z1) {
                  newStrength = this.checkTarget(world, xi, y0 + 1, zi, newStrength);
               }
            } else if (!world.isBlockNormalCube(xi, y0, zi) && (xi != x1 || y0 - 1 != y1 || zi != z1)) {
               newStrength = this.checkTarget(world, xi, y0 - 1, zi, newStrength);
            }
         }

         if (newStrength > 0) {
            newStrength--;
         } else {
            newStrength = 0;
         }
      }

      if (strength != newStrength) {
         int blockId = world.getBlockId(x0, y0, z0);
         if (blockId == this.id() || blockId == Blocks.FLUID_LAVA_STILL.id() || blockId == Blocks.FLUID_LAVA_FLOWING.id()) {
            world.noNeighborUpdate = true;
            world.setBlockMetadataWithNotify(x0, y0, z0, newStrength);
            world.markBlocksDirty(x0, y0, z0, x0, y0, z0);
            world.noNeighborUpdate = false;
         }

         for (int i = 0; i < 4; i++) {
            int xix = x0;
            int zix = z0;
            int yi = y0 - 1;
            if (i == 0) {
               xix = x0 - 1;
            }

            if (i == 1) {
               xix++;
            }

            if (i == 2) {
               zix = z0 - 1;
            }

            if (i == 3) {
               zix++;
            }

            if (world.isBlockNormalCube(xix, y0, zix)) {
               yi += 2;
            }

            int targetStrength = this.checkTarget(world, xix, y0, zix, -1);
            newStrength = world.getBlockMetadata(x0, y0, z0);
            if (newStrength > 0) {
               newStrength--;
            }

            if (targetStrength >= 0 && targetStrength != newStrength) {
               this.updatePowerStrength(world, xix, y0, zix, x0, y0, z0);
            }

            targetStrength = this.checkTarget(world, xix, yi, zix, -1);
            newStrength = world.getBlockMetadata(x0, y0, z0);
            if (newStrength > 0) {
               newStrength--;
            }

            if (targetStrength >= 0 && targetStrength != newStrength) {
               this.updatePowerStrength(world, xix, yi, zix, x0, y0, z0);
            }
         }

         if (strength == 0 || newStrength == 0) {
            this.toUpdate.add(new ChunkPosition(x0, y0, z0));
            this.toUpdate.add(new ChunkPosition(x0 - 1, y0, z0));
            this.toUpdate.add(new ChunkPosition(x0 + 1, y0, z0));
            this.toUpdate.add(new ChunkPosition(x0, y0 - 1, z0));
            this.toUpdate.add(new ChunkPosition(x0, y0 + 1, z0));
            this.toUpdate.add(new ChunkPosition(x0, y0, z0 - 1));
            this.toUpdate.add(new ChunkPosition(x0, y0, z0 + 1));
         }
      }
   }

   private void checkCornerChangeAt(World world, int x, int y, int z) {
      if (world.getBlockId(x, y, z) == this.id()) {
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.id());
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
      }
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      super.onBlockPlacedByWorld(world, x, y, z);
      if (!world.isClientSide) {
         this.updatePowerStrength(world, x, y, z);
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         this.checkCornerChangeAt(world, x - 1, y, z);
         this.checkCornerChangeAt(world, x + 1, y, z);
         this.checkCornerChangeAt(world, x, y, z - 1);
         this.checkCornerChangeAt(world, x, y, z + 1);
         if (world.isBlockNormalCube(x - 1, y, z)) {
            this.checkCornerChangeAt(world, x - 1, y + 1, z);
         } else {
            this.checkCornerChangeAt(world, x - 1, y - 1, z);
         }

         if (world.isBlockNormalCube(x + 1, y, z)) {
            this.checkCornerChangeAt(world, x + 1, y + 1, z);
         } else {
            this.checkCornerChangeAt(world, x + 1, y - 1, z);
         }

         if (world.isBlockNormalCube(x, y, z - 1)) {
            this.checkCornerChangeAt(world, x, y + 1, z - 1);
         } else {
            this.checkCornerChangeAt(world, x, y - 1, z - 1);
         }

         if (world.isBlockNormalCube(x, y, z + 1)) {
            this.checkCornerChangeAt(world, x, y + 1, z + 1);
         } else {
            this.checkCornerChangeAt(world, x, y - 1, z + 1);
         }
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      super.onBlockRemoved(world, x, y, z, data);
      if (!world.isClientSide) {
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         this.updatePowerStrength(world, x, y, z);
         this.checkCornerChangeAt(world, x - 1, y, z);
         this.checkCornerChangeAt(world, x + 1, y, z);
         this.checkCornerChangeAt(world, x, y, z - 1);
         this.checkCornerChangeAt(world, x, y, z + 1);
         if (world.isBlockNormalCube(x - 1, y, z)) {
            this.checkCornerChangeAt(world, x - 1, y + 1, z);
         } else {
            this.checkCornerChangeAt(world, x - 1, y - 1, z);
         }

         if (world.isBlockNormalCube(x + 1, y, z)) {
            this.checkCornerChangeAt(world, x + 1, y + 1, z);
         } else {
            this.checkCornerChangeAt(world, x + 1, y - 1, z);
         }

         if (world.isBlockNormalCube(x, y, z - 1)) {
            this.checkCornerChangeAt(world, x, y + 1, z - 1);
         } else {
            this.checkCornerChangeAt(world, x, y - 1, z - 1);
         }

         if (world.isBlockNormalCube(x, y, z + 1)) {
            this.checkCornerChangeAt(world, x, y + 1, z + 1);
         } else {
            this.checkCornerChangeAt(world, x, y - 1, z + 1);
         }
      }
   }

   private int checkTarget(World world, int x, int y, int z, int sourceStrength) {
      if (world.getBlockId(x, y, z) != this.id()) {
         return sourceStrength;
      } else {
         int targetStrength = world.getBlockMetadata(x, y, z);
         return Math.max(targetStrength, sourceStrength);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (!world.isClientSide) {
         int strength = world.getBlockMetadata(x, y, z);
         boolean canPlaceBlockAt = this.canPlaceBlockAt(world, x, y, z);
         if (!canPlaceBlockAt) {
            this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, strength, null, null);
            world.setBlockWithNotify(x, y, z, 0);
         } else {
            this.updatePowerStrength(world, x, y, z);
         }

         super.onNeighborBlockChange(world, x, y, z, blockId);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.DUST_REDSTONE)};
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return !this.shouldSignal ? false : this.getSignal(world, x, y, z, side);
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      if (!this.shouldSignal) {
         return false;
      } else if (worldSource.getBlockMetadata(x, y, z) == 0) {
         return false;
      } else if (side == Side.TOP) {
         return true;
      } else {
         boolean negXShouldConnectTo = shouldConnectTo(worldSource, x - 1, y, z, 1)
            || !worldSource.isBlockNormalCube(x - 1, y, z) && shouldConnectTo(worldSource, x - 1, y - 1, z, -1);
         boolean posXShouldConnectTo = shouldConnectTo(worldSource, x + 1, y, z, 3)
            || !worldSource.isBlockNormalCube(x + 1, y, z) && shouldConnectTo(worldSource, x + 1, y - 1, z, -1);
         boolean negZShouldConnectTo = shouldConnectTo(worldSource, x, y, z - 1, 2)
            || !worldSource.isBlockNormalCube(x, y, z - 1) && shouldConnectTo(worldSource, x, y - 1, z - 1, -1);
         boolean posZShouldConnectTo = shouldConnectTo(worldSource, x, y, z + 1, 0)
            || !worldSource.isBlockNormalCube(x, y, z + 1) && shouldConnectTo(worldSource, x, y - 1, z + 1, -1);
         if (!worldSource.isBlockNormalCube(x, y + 1, z)) {
            if (worldSource.isBlockNormalCube(x - 1, y, z) && shouldConnectTo(worldSource, x - 1, y + 1, z, -1)) {
               negXShouldConnectTo = true;
            }

            if (worldSource.isBlockNormalCube(x + 1, y, z) && shouldConnectTo(worldSource, x + 1, y + 1, z, -1)) {
               posXShouldConnectTo = true;
            }

            if (worldSource.isBlockNormalCube(x, y, z - 1) && shouldConnectTo(worldSource, x, y + 1, z - 1, -1)) {
               negZShouldConnectTo = true;
            }

            if (worldSource.isBlockNormalCube(x, y, z + 1) && shouldConnectTo(worldSource, x, y + 1, z + 1, -1)) {
               posZShouldConnectTo = true;
            }
         }

         if (!negZShouldConnectTo && !posXShouldConnectTo && !negXShouldConnectTo && !posZShouldConnectTo && side.getAxis() != Axis.Y) {
            return true;
         } else if (side == Side.NORTH && negZShouldConnectTo && !negXShouldConnectTo && !posXShouldConnectTo) {
            return true;
         } else if (side == Side.SOUTH && posZShouldConnectTo && !negXShouldConnectTo && !posXShouldConnectTo) {
            return true;
         } else {
            return side == Side.WEST && negXShouldConnectTo && !negZShouldConnectTo && !posZShouldConnectTo
               ? true
               : side == Side.EAST && posXShouldConnectTo && !negZShouldConnectTo && !posZShouldConnectTo;
         }
      }
   }

   @Override
   public boolean isSignalSource() {
      return this.shouldSignal;
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      int meta = world.getBlockMetadata(x, y, z);
      if (meta != 0) {
         double px = x + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double py = y + 0.0625;
         double pz = z + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         int redstoneBrightness = meta & 15;
         world.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0, redstoneBrightness);
      }
   }

   public static boolean shouldConnectTo(WorldSource worldSource, int x, int y, int z, int data) {
      int blockId = worldSource.getBlockId(x, y, z);
      if (blockId == Blocks.WIRE_REDSTONE.id()) {
         return true;
      } else if (blockId <= 0) {
         return false;
      } else if (Blocks.blocksList[blockId].isSignalSource()) {
         return true;
      } else if (blockId == Blocks.PUMPKIN_REDSTONE.id()) {
         if (data >= 0 && data <= 3) {
            Side[] lookup = new Side[]{Side.NORTH, Side.EAST, Side.SOUTH, Side.WEST};
            Side side = Side.getSideById(worldSource.getBlockMetadata(x, y, z));
            return side == lookup[data];
         } else {
            return false;
         }
      } else if (blockId != Blocks.REPEATER_IDLE.id() && blockId != Blocks.REPEATER_ACTIVE.id()) {
         return false;
      } else {
         int meta = worldSource.getBlockMetadata(x, y, z);
         return data == BlockLogicBed.footToHeadMap[meta & 3];
      }
   }
}

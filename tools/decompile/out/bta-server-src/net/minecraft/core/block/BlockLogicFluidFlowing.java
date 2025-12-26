package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;

public class BlockLogicFluidFlowing extends BlockLogicFluid {
   int maxCount = 0;
   boolean[] result = new boolean[4];
   int[] distance = new int[4];
   public final Block<?> blockStill;

   public BlockLogicFluidFlowing(Block<?> block, Material material, Block<?> blockStill) {
      super(block, material);
      this.blockStill = blockStill;
   }

   private void setFluidStill(World world, int x, int y, int z) {
      int meta = world.getBlockMetadata(x, y, z);
      world.setBlockAndMetadata(x, y, z, this.blockStill.id(), meta);
      world.markBlocksDirty(x, y, z, x, y, z);
      world.markBlockNeedsUpdate(x, y, z);
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      int localFlowDecay = this.getDepth(world, x, y, z);
      byte flowDecayMod = 1;
      if (this.material == Material.lava && world.dimension != Dimension.NETHER) {
         flowDecayMod = 2;
      }

      if (localFlowDecay > 0) {
         int surroundingFlowDecay = -100;
         this.maxCount = 0;
         surroundingFlowDecay = this.getHighest(world, x - 1, y, z, surroundingFlowDecay);
         surroundingFlowDecay = this.getHighest(world, x + 1, y, z, surroundingFlowDecay);
         surroundingFlowDecay = this.getHighest(world, x, y, z - 1, surroundingFlowDecay);
         surroundingFlowDecay = this.getHighest(world, x, y, z + 1, surroundingFlowDecay);
         int newFlowDecay = surroundingFlowDecay + flowDecayMod;
         if (newFlowDecay >= 8 || surroundingFlowDecay < 0) {
            newFlowDecay = -1;
         }

         if (this.getDepth(world, x, y + 1, z) >= 0) {
            int flowDecayAbove = this.getDepth(world, x, y + 1, z);
            if (flowDecayAbove >= 8) {
               newFlowDecay = flowDecayAbove;
            } else {
               newFlowDecay = flowDecayAbove + 8;
            }
         }

         if (this.maxCount >= 2 && this.material == Material.water) {
            if (world.getBlockMaterial(x, y - 1, z).isSolid()) {
               newFlowDecay = 0;
            } else if (world.getBlockMaterial(x, y - 1, z) == this.material && world.getBlockMetadata(x, y - 1, z) == 0) {
               newFlowDecay = 0;
            }
         }

         if (newFlowDecay != localFlowDecay) {
            localFlowDecay = newFlowDecay;
            if (newFlowDecay < 0) {
               world.setBlockWithNotify(x, y, z, 0);
            } else {
               world.setBlockMetadataWithNotify(x, y, z, newFlowDecay);
               world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
               world.notifyBlocksOfNeighborChange(x, y, z, this.block.id());
            }
         } else {
            this.setFluidStill(world, x, y, z);
         }
      } else {
         this.setFluidStill(world, x, y, z);
      }

      if (this.canSpreadTo(world, x, y - 1, z)) {
         int id = world.getBlockId(x, y - 1, z);
         int meta = world.getBlockMetadata(x, y - 1, z);
         if (id > 0) {
            Blocks.blocksList[id].dropBlockWithCause(world, EnumDropCause.WORLD, x, y - 1, z, meta, null, null);
         }

         if (localFlowDecay >= 8) {
            world.setBlockAndMetadataWithNotify(x, y - 1, z, this.block.id(), localFlowDecay);
         } else {
            world.setBlockAndMetadataWithNotify(x, y - 1, z, this.block.id(), localFlowDecay + 8);
         }
      } else if (localFlowDecay >= 0 && (localFlowDecay == 0 || this.isFluidBlocking(world, x, y - 1, z))) {
         boolean[] aflag = this.getSpread(world, x, y, z);
         int k1 = localFlowDecay + flowDecayMod;
         if (localFlowDecay >= 8) {
            k1 = 1;
         }

         if (k1 >= 8) {
            return;
         }

         if (aflag[0]) {
            this.flowIntoBlock(world, x - 1, y, z, k1);
         }

         if (aflag[1]) {
            this.flowIntoBlock(world, x + 1, y, z, k1);
         }

         if (aflag[2]) {
            this.flowIntoBlock(world, x, y, z - 1, k1);
         }

         if (aflag[3]) {
            this.flowIntoBlock(world, x, y, z + 1, k1);
         }
      }
   }

   private void flowIntoBlock(World world, int x, int y, int z, int meta) {
      if (this.canSpreadTo(world, x, y, z)) {
         int i1 = world.getBlockId(x, y, z);
         if (i1 > 0) {
            if (this.material == Material.lava) {
               this.fizz(world, x, y, z);
            } else {
               Blocks.blocksList[i1].dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null, null);
            }
         }

         world.setBlockAndMetadataWithNotify(x, y, z, this.block.id(), meta);
      }
   }

   private int getSlopeDistance(World world, int x, int y, int z, int l, int i1) {
      int j1 = 1000;

      for (int k1 = 0; k1 < 4; k1++) {
         if ((k1 != 0 || i1 != 1) && (k1 != 1 || i1 != 0) && (k1 != 2 || i1 != 3) && (k1 != 3 || i1 != 2)) {
            int x2 = x;
            int z2 = z;
            if (k1 == 0) {
               x2 = x - 1;
            }

            if (k1 == 1) {
               x2++;
            }

            if (k1 == 2) {
               z2 = z - 1;
            }

            if (k1 == 3) {
               z2++;
            }

            if (!this.isFluidBlocking(world, x2, y, z2) && (world.getBlockMaterial(x2, y, z2) != this.material || world.getBlockMetadata(x2, y, z2) != 0)) {
               if (!this.isFluidBlocking(world, x2, y - 1, z2)) {
                  return l;
               }

               if (l < 4) {
                  int k2 = this.getSlopeDistance(world, x2, y, z2, l + 1, k1);
                  if (k2 < j1) {
                     j1 = k2;
                  }
               }
            }
         }
      }

      return j1;
   }

   private boolean[] getSpread(World world, int x, int y, int z) {
      for (int l = 0; l < 4; l++) {
         this.distance[l] = 1000;
         int x2 = x;
         int z2 = z;
         if (l == 0) {
            x2 = x - 1;
         }

         if (l == 1) {
            x2++;
         }

         if (l == 2) {
            z2 = z - 1;
         }

         if (l == 3) {
            z2++;
         }

         if (!this.isFluidBlocking(world, x2, y, z2) && (world.getBlockMaterial(x2, y, z2) != this.material || world.getBlockMetadata(x2, y, z2) != 0)) {
            if (!this.isFluidBlocking(world, x2, y - 1, z2)) {
               this.distance[l] = 0;
            } else {
               this.distance[l] = this.getSlopeDistance(world, x2, y, z2, 1, l);
            }
         }
      }

      int i1 = this.distance[0];

      for (int k1 = 1; k1 < 4; k1++) {
         if (this.distance[k1] < i1) {
            i1 = this.distance[k1];
         }
      }

      for (int l1 = 0; l1 < 4; l1++) {
         this.result[l1] = this.distance[l1] == i1;
      }

      return this.result;
   }

   private boolean isFluidBlocking(World world, int x, int y, int z) {
      Block<?> b = world.getBlock(x, y, z);
      return b != null && !(b.getLogic() instanceof BlockLogicFluid) && !b.hasTag(BlockTags.BROKEN_BY_FLUIDS);
   }

   protected int getHighest(World world, int x, int y, int z, int currentFlowDecay) {
      int flowDecay = this.getDepth(world, x, y, z);
      if (flowDecay < 0) {
         return currentFlowDecay;
      } else {
         if (flowDecay == 0) {
            this.maxCount++;
         }

         if (flowDecay >= 8) {
            flowDecay = 0;
         }

         return currentFlowDecay >= 0 && flowDecay >= currentFlowDecay ? currentFlowDecay : flowDecay;
      }
   }

   private boolean canSpreadTo(World world, int x, int y, int z) {
      Material material = world.getBlockMaterial(x, y, z);
      if (material == this.material) {
         return false;
      } else {
         return material == Material.lava ? false : !this.isFluidBlocking(world, x, y, z);
      }
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      super.onBlockPlacedByWorld(world, x, y, z);
      if (world.getBlockId(x, y, z) == this.block.id()) {
         world.scheduleBlockUpdate(x, y, z, this.block.id(), this.tickDelay());
      }
   }
}

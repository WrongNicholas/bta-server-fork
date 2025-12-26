package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicRedstone extends BlockLogic {
   public BlockLogicRedstone(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return true;
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return this.getSignal(world, x, y, z, side);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      for (Side s : Side.sides) {
         world.notifyBlocksOfNeighborChange(x + s.getOffsetX(), y + s.getOffsetY(), z + s.getOffsetZ(), this.id());
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      for (Side s : Side.sides) {
         world.notifyBlocksOfNeighborChange(x + s.getOffsetX(), y + s.getOffsetY(), z + s.getOffsetZ(), this.id());
      }
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      this.spawnParticles(world, x, y, z);
   }

   private void spawnParticles(World world, int x, int y, int z) {
      Random random = world.rand;
      int redstoneBrightness = 10;
      double d = 0.0625;

      for (Side s : Side.sides) {
         double px = (double)x + random.nextFloat();
         double py = (double)y + random.nextFloat();
         double pz = (double)z + random.nextFloat();
         switch (s) {
            case BOTTOM:
               if (!world.isBlockOpaqueCube(x, y + 1, z)) {
                  py = y + 1 + 0.0625;
               }
               break;
            case TOP:
               if (!world.isBlockOpaqueCube(x, y - 1, z)) {
                  py = y + 0 - 0.0625;
               }
               break;
            case NORTH:
               if (!world.isBlockOpaqueCube(x, y, z + 1)) {
                  pz = z + 1 + 0.0625;
               }
               break;
            case SOUTH:
               if (!world.isBlockOpaqueCube(x, y, z - 1)) {
                  pz = z + 0 - 0.0625;
               }
               break;
            case WEST:
               if (!world.isBlockOpaqueCube(x + 1, y, z)) {
                  px = x + 1 + 0.0625;
               }
               break;
            case EAST:
               if (!world.isBlockOpaqueCube(x - 1, y, z)) {
                  px = x + 0 - 0.0625;
               }
         }

         if (px < x || px > x + 1 || py < 0.0 || py > y + 1 || pz < z || pz > z + 1) {
            world.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0, 10);
         }
      }
   }
}

package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntitySensor;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicMotionSensor extends BlockLogicVeryRotatable {
   public final boolean isActive;

   public BlockLogicMotionSensor(Block<?> block, boolean isActive) {
      super(block, Material.glass);
      this.isActive = isActive;
      block.withEntity(TileEntitySensor::new);
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.isActive) {
         Random random = world.rand;
         int redstoneBrightness = 10;
         double d = 0.0625;

         for (int i = 0; i < 6; i++) {
            double px = (double)x + random.nextFloat();
            double py = (double)y + random.nextFloat();
            double pz = (double)z + random.nextFloat();
            if (i == 0 && !world.isBlockOpaqueCube(x, y + 1, z)) {
               py = y + 1 + d;
            }

            if (i == 1 && !world.isBlockOpaqueCube(x, y - 1, z)) {
               py = y + 0 - d;
            }

            if (i == 2 && !world.isBlockOpaqueCube(x, y, z + 1)) {
               pz = z + 1 + d;
            }

            if (i == 3 && !world.isBlockOpaqueCube(x, y, z - 1)) {
               pz = z + 0 - d;
            }

            if (i == 4 && !world.isBlockOpaqueCube(x + 1, y, z)) {
               px = x + 1 + d;
            }

            if (i == 5 && !world.isBlockOpaqueCube(x - 1, y, z)) {
               px = x + 0 - d;
            }

            if (px < x || px > x + 1 || py < 0.0 || py > y + 1 || pz < z || pz > z + 1) {
               world.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0, 10);
            }
         }
      }
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return this.getSignal(world, x, y, z, side);
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return this.isActive;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause == EnumDropCause.IMPROPER_TOOL ? null : new ItemStack[]{new ItemStack(Blocks.MOTION_SENSOR_IDLE)};
   }

   public static Direction getFacingDirection(int meta) {
      if (meta == 0) {
         return Direction.DOWN;
      } else if (meta == 1) {
         return Direction.UP;
      } else if (meta == 2) {
         return Direction.NORTH;
      } else if (meta == 3) {
         return Direction.SOUTH;
      } else if (meta == 4) {
         return Direction.WEST;
      } else {
         return meta == 5 ? Direction.EAST : Direction.NONE;
      }
   }
}

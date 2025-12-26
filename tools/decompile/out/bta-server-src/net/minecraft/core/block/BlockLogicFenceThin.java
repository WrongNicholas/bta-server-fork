package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public abstract class BlockLogicFenceThin extends BlockLogic {
   public BlockLogicFenceThin(Block<?> block, Material material) {
      super(block, material);
   }

   @Override
   public boolean canPlaceOnSurface() {
      return true;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
      boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
      boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
      boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);
      return AABB.getTemporaryBB(
         connectXNeg ? 0.0F : 0.375F, 0.0, connectZNeg ? 0.0F : 0.375F, 1.0F - (connectXPos ? 0.0F : 0.375F), 1.0, 1.0F - (connectZPos ? 0.0F : 0.375F)
      );
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   public abstract boolean canConnectTo(WorldSource var1, int var2, int var3, int var4);

   public boolean shouldDrawColumn(WorldSource world, int x, int y, int z) {
      boolean drawColumn = this.shouldDrawColumn_do(world, x, y, z);
      if (drawColumn) {
         return true;
      } else {
         int oy = 1;

         while (world.getBlockId(x, y + oy, z) == this.block.id()) {
            oy++;
         }

         oy--;

         boolean drawColumnFromOther;
         for (drawColumnFromOther = false; world.getBlockId(x, y + oy, z) == this.block.id(); oy--) {
            if (this.shouldDrawColumn_do(world, x, y + oy, z)) {
               drawColumnFromOther = true;
               break;
            }
         }

         return drawColumnFromOther;
      }
   }

   private boolean shouldDrawColumn_do(WorldSource world, int x, int y, int z) {
      boolean connectNorth = this.canConnectTo(world, x + Direction.NORTH.getOffsetX(), y + Direction.NORTH.getOffsetY(), z + Direction.NORTH.getOffsetZ());
      boolean connectSouth = this.canConnectTo(world, x + Direction.SOUTH.getOffsetX(), y + Direction.SOUTH.getOffsetY(), z + Direction.SOUTH.getOffsetZ());
      boolean connectEast = this.canConnectTo(world, x + Direction.EAST.getOffsetX(), y + Direction.EAST.getOffsetY(), z + Direction.EAST.getOffsetZ());
      boolean connectWest = this.canConnectTo(world, x + Direction.WEST.getOffsetX(), y + Direction.WEST.getOffsetY(), z + Direction.WEST.getOffsetZ());
      boolean lineNorthSouth = connectNorth && connectSouth;
      boolean lineEastWest = connectEast && connectWest;
      return !lineNorthSouth && !lineEastWest
         || lineNorthSouth && lineEastWest
         || lineNorthSouth && (connectEast || connectWest)
         || lineEastWest && (connectNorth || connectSouth);
   }
}

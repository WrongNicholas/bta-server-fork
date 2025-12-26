package net.minecraft.core.block;

import java.util.List;
import java.util.Random;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.vehicle.EntityMinecart;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicRailDetector extends BlockLogicRail {
   public BlockLogicRailDetector(Block<?> block) {
      super(block, true);
      block.setTicking(true);
   }

   @Override
   public int tickDelay() {
      return 20;
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         if ((meta & 8) == 0) {
            this.checkForMinecart(world, x, y, z, meta);
         }
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      if (!world.isClientSide) {
         int meta = world.getBlockMetadata(x, y, z);
         if ((meta & 8) != 0) {
            this.checkForMinecart(world, x, y, z, meta);
         }
      }
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      return (worldSource.getBlockMetadata(x, y, z) & 8) != 0;
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return (world.getBlockMetadata(x, y, z) & 8) == 0 ? false : side == Side.TOP;
   }

   private void checkForMinecart(World world, int x, int y, int z, int meta) {
      boolean isPowered = (meta & 8) != 0;
      boolean minecartPresent = false;
      float offset = 0.125F;
      List<EntityMinecart> list = world.getEntitiesWithinAABB(
         EntityMinecart.class, AABB.getTemporaryBB(x + offset, y + offset, z + offset, x + 1 - offset, y + 1 - offset, z + 1 - offset)
      );
      if (!list.isEmpty()) {
         minecartPresent = true;
      }

      if (minecartPresent && !isPowered) {
         world.setBlockMetadataWithNotify(x, y, z, meta | 8);
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         world.markBlockDirty(x, y, z);
      }

      if (!minecartPresent && isPowered) {
         world.setBlockMetadataWithNotify(x, y, z, meta & -9);
         world.notifyBlocksOfNeighborChange(x, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         world.markBlockDirty(x, y, z);
      }

      if (minecartPresent) {
         world.scheduleBlockUpdate(x, y, z, this.id(), this.tickDelay());
      }
   }
}

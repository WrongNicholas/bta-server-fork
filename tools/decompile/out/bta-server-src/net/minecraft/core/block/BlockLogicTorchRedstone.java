package net.minecraft.core.block;

import java.util.Random;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicTorchRedstone extends BlockLogicTorch {
   private final boolean torchActive;

   public BlockLogicTorchRedstone(Block<?> block, boolean isActive) {
      super(block);
      this.torchActive = isActive;
      block.setTicking(true);
   }

   @Override
   public int tickDelay() {
      return 2;
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      if (world.getBlockMetadata(x, y, z) == 0) {
         super.onBlockPlacedByWorld(world, x, y, z);
      }

      if (this.torchActive) {
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.id());
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (this.torchActive) {
         world.notifyBlocksOfNeighborChange(x, y - 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y + 1, z, this.id());
         world.notifyBlocksOfNeighborChange(x - 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x + 1, y, z, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z - 1, this.id());
         world.notifyBlocksOfNeighborChange(x, y, z + 1, this.id());
      }
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      if (!this.torchActive) {
         return false;
      } else {
         int direction = worldSource.getBlockMetadata(x, y, z) & 7;
         switch (direction) {
            case 1:
               return side != Side.EAST;
            case 2:
               return side != Side.WEST;
            case 3:
               return side != Side.SOUTH;
            case 4:
               return side != Side.NORTH;
            case 5:
               return side != Side.TOP;
            default:
               return false;
         }
      }
   }

   private boolean hasNeighborSignal(World world, int x, int y, int z) {
      int direction = world.getBlockMetadata(x, y, z) & 7;
      switch (direction) {
         case 1:
            return world.getSignal(x - 1, y, z, Side.WEST);
         case 2:
            return world.getSignal(x + 1, y, z, Side.EAST);
         case 3:
            return world.getSignal(x, y, z - 1, Side.NORTH);
         case 4:
            return world.getSignal(x, y, z + 1, Side.SOUTH);
         case 5:
            return world.getSignal(x, y - 1, z, Side.BOTTOM);
         default:
            return false;
      }
   }

   @Override
   public void updateTick(World world, int x, int y, int z, Random rand) {
      boolean isPowered = this.hasNeighborSignal(world, x, y, z);
      if (this.torchActive) {
         if (isPowered) {
            world.setBlockAndMetadataWithNotify(x, y, z, Blocks.TORCH_REDSTONE_IDLE.id(), world.getBlockMetadata(x, y, z));
         }
      } else if (!isPowered) {
         world.setBlockAndMetadataWithNotify(x, y, z, Blocks.TORCH_REDSTONE_ACTIVE.id(), world.getBlockMetadata(x, y, z));
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      super.onNeighborBlockChange(world, x, y, z, blockId);
      world.scheduleBlockUpdate(x, y, z, this.id(), this.tickDelay());
   }

   @Override
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return side == Side.BOTTOM ? this.getSignal(world, x, y, z, side) : false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Blocks.TORCH_REDSTONE_ACTIVE)};
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public void animationTick(World world, int x, int y, int z, Random rand) {
      if (this.torchActive) {
         int direction = world.getBlockMetadata(x, y, z) & 7;
         double px = x + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double py = y + 0.7 + (rand.nextFloat() - 0.5) * 0.2;
         double pz = z + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double verticalOffset = 0.22;
         double horizontalOffset = 0.27;
         int redstoneBrightness = 10;
         switch (direction) {
            case 1:
               world.spawnParticle("reddust", px - horizontalOffset, py + verticalOffset, pz, 0.0, 0.0, 0.0, 10);
               break;
            case 2:
               world.spawnParticle("reddust", px + horizontalOffset, py + verticalOffset, pz, 0.0, 0.0, 0.0, 10);
               break;
            case 3:
               world.spawnParticle("reddust", px, py + verticalOffset, pz - horizontalOffset, 0.0, 0.0, 0.0, 10);
               break;
            case 4:
               world.spawnParticle("reddust", px, py + verticalOffset, pz + horizontalOffset, 0.0, 0.0, 0.0, 10);
               break;
            case 5:
               world.spawnParticle("reddust", px, py, pz, 0.0, 0.0, 0.0, 10);
         }
      }
   }
}

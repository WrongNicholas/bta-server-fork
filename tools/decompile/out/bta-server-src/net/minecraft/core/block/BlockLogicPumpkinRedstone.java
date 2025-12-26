package net.minecraft.core.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicPumpkinRedstone extends BlockLogicVeryRotatable {
   public BlockLogicPumpkinRedstone(Block<?> block) {
      super(block, Material.vegetable);
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
   public boolean getDirectSignal(World world, int x, int y, int z, Side side) {
      return this.getSignal(world, x, y, z, side);
   }

   @Override
   public boolean getSignal(WorldSource worldSource, int x, int y, int z, Side side) {
      Side mySide = Side.getSideById(worldSource.getBlockMetadata(x, y, z));
      return mySide == side.getOpposite();
   }

   @Override
   public boolean isSignalSource() {
      return true;
   }

   @Override
   public int getPistonPushReaction(World world, int x, int y, int z) {
      return 0;
   }
}

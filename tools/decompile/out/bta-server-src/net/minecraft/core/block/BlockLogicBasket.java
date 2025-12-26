package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityBasket;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicBasket extends BlockLogic {
   protected BlockLogicBasket(Block<?> block, Material material) {
      super(block, material);
      block.withEntity(TileEntityBasket::new);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player entityplayer, Side side, double xPlaced, double yPlaced) {
      TileEntityBasket te = (TileEntityBasket)world.getTileEntity(x, y, z);
      if (te.getNumUnitsInside() > 0) {
         te.givePlayerAllItems(world, entityplayer);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      TileEntityBasket te = (TileEntityBasket)world.getTileEntity(x, y, z);
      if (te.getNumUnitsInside() > 0) {
         te.dropContents(world, x, y, z);
      }
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   public int getFillLevel(WorldSource world, int x, int y, int z) {
      TileEntityBasket te = (TileEntityBasket)world.getTileEntity(x, y, z);
      float fill = MathHelper.clamp((float)te.getNumUnitsInside() / te.getMaxUnits(), 0.0F, 1.0F);
      return (int)Math.ceil(10.0F * fill);
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public boolean isSignalSource() {
      return true;
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
      TileEntityBasket basketTileEntity = (TileEntityBasket)worldSource.getTileEntity(x, y, z);
      return basketTileEntity != null ? basketTileEntity.getNumUnitsInside() >= basketTileEntity.getMaxUnits() : false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.BASKET)};
   }
}

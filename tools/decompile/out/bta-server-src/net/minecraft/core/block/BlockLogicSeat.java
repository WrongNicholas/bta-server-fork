package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntitySeat;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicSeat extends BlockLogic {
   public BlockLogicSeat(Block<?> block) {
      super(block, Material.wood);
      this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);
      block.withEntity(() -> new TileEntitySeat(block));
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.isBlockNormalCube(x, y + 1, z)) {
         return false;
      } else {
         if (!world.isClientSide) {
            TileEntitySeat tileEntity = (TileEntitySeat)world.getTileEntity(x, y, z);
            if (tileEntity.getPassenger() == null) {
               player.startRiding(tileEntity);
            }
         }

         return true;
      }
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      if (!world.isClientSide) {
         TileEntitySeat tileEntity = (TileEntitySeat)world.getTileEntity(x, y, z);
         if (tileEntity != null && tileEntity.getPassenger() != null) {
            tileEntity.ejectRider();
         }
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   @Override
   public boolean isCubeShaped() {
      return false;
   }

   @Override
   public boolean isSolidRender() {
      return false;
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(Items.SEAT)} : null;
   }
}

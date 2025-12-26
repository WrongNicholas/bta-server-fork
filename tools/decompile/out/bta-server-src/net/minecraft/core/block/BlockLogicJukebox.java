package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.entity.TileEntityJukebox;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicJukebox extends BlockLogic {
   public BlockLogicJukebox(Block<?> block) {
      super(block, Material.wood);
      block.withEntity(TileEntityJukebox::new);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.getBlockMetadata(x, y, z) == 0) {
         return false;
      } else {
         this.ejectRecord(world, x, y, z);
         return true;
      }
   }

   @Override
   public void onActivatorInteract(World world, int x, int y, int z, TileEntityActivator activator, Direction direction) {
      if (world.getBlockMetadata(x, y, z) != 0) {
         this.ejectRecord(world, x, y, z);
      }
   }

   public void playRecord(World world, int x, int y, int z, int recordId) {
      if (!world.isClientSide) {
         TileEntityJukebox jukebox = (TileEntityJukebox)world.getTileEntity(x, y, z);
         jukebox.record = recordId;
         jukebox.setChanged();
         world.setBlockMetadataWithNotify(x, y, z, 1);
      }
   }

   public void ejectRecord(World world, int x, int y, int z) {
      if (!world.isClientSide) {
         TileEntity tileEntity = world.getTileEntity(x, y, z);
         tileEntity.dropContents(world, x, y, z);
      }
   }
}

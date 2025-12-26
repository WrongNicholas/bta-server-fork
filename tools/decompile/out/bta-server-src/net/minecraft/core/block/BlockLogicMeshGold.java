package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntityMeshGold;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicMeshGold extends BlockLogicMesh {
   public BlockLogicMeshGold(Block<?> block) {
      super(block);
      block.withEntity(TileEntityMeshGold::new);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      TileEntityMeshGold meshGold = (TileEntityMeshGold)world.getTileEntity(x, y, z);
      ItemStack heldItem = player.getHeldItem();
      boolean flag = meshGold.setFilterItem(player, heldItem);
      if (heldItem != null && heldItem.stackSize <= 0) {
         player.inventory.setItem(player.inventory.getCurrentItemIndex(), null);
      }

      return flag;
   }

   @Override
   public boolean collidesWithEntity(Entity entity, World world, int x, int y, int z) {
      if (entity instanceof EntityItem) {
         TileEntityMeshGold mesh = (TileEntityMeshGold)world.getTileEntity(x, y, z);
         if (mesh.filterItem == null) {
            return true;
         } else {
            ItemStack entityStack = ((EntityItem)entity).item;
            return !entityStack.isItemEqual(mesh.filterItem);
         }
      } else {
         return true;
      }
   }
}

package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.EntityPrimedTNT;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemFireStriker;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class BlockLogicTNT extends BlockLogic {
   public BlockLogicTNT(Block<?> block) {
      super(block, Material.explosive);
   }

   @Override
   public void onBlockPlacedByWorld(World world, int x, int y, int z) {
      super.onBlockPlacedByWorld(world, x, y, z);
      if (world.hasNeighborSignal(x, y, z)) {
         this.ignite(world, x, y, z, true);
      }
   }

   @Override
   public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
      if (blockId > 0 && Blocks.blocksList[blockId].isSignalSource() && world.hasNeighborSignal(x, y, z)) {
         this.ignite(world, x, y, z, true);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause == EnumDropCause.EXPLOSION ? null : new ItemStack[]{new ItemStack(this)};
   }

   @Override
   public void onBlockDestroyedByExplosion(World world, int x, int y, int z) {
      EntityPrimedTNT entity = new EntityPrimedTNT(world, x + 0.5F, y + 0.5F, z + 0.5F);
      entity.fuse = world.rand.nextInt(entity.fuse / 4) + entity.fuse / 8;
      world.entityJoinedWorld(entity);
   }

   public void ignite(World world, int x, int y, int z, boolean sound) {
      this.ignite(world, x, y, z, null, sound);
   }

   public void ignite(World world, int x, int y, int z, Player player, boolean sound) {
      if (world.isClientSide) {
         if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
            player.inventory.getCurrentItem().damageItem(1, player);
         }
      } else {
         world.setBlockWithNotify(x, y, z, 0);
         EntityPrimedTNT e = new EntityPrimedTNT(world, x + 0.5F, y + 0.5F, z + 0.5F);
         world.entityJoinedWorld(e);
         world.playSoundAtEntity(null, e, "tile.tnt.fuse", 1.0F, 1.0F);
         if (player != null && player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
            player.inventory.getCurrentItem().damageItem(1, player);
         }
      }
   }

   @Override
   public void onBlockLeftClicked(World world, int x, int y, int z, Player player, Side side, double xHit, double yHit) {
      if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemFireStriker) {
         world.setBlockMetadata(x, y, z, 1);
      }

      super.onBlockLeftClicked(world, x, y, z, player, side, xHit, yHit);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemFireStriker) {
         this.ignite(world, x, y, z, player, true);
         return true;
      } else {
         return super.onBlockRightClicked(world, x, y, z, player, side, xPlaced, yPlaced);
      }
   }
}

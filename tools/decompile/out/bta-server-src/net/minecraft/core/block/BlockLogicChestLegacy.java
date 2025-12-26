package net.minecraft.core.block;

import net.minecraft.core.Global;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

@Deprecated
public class BlockLogicChestLegacy extends BlockLogic {
   public BlockLogicChestLegacy(Block<?> block) {
      super(block, Material.wood);
      block.withEntity(TileEntityChest::new);
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return dropCause != EnumDropCause.IMPROPER_TOOL ? new ItemStack[]{new ItemStack(Blocks.CHEST_PLANKS_OAK)} : null;
   }

   public static boolean isLegacyChest(World world, int x, int y, int z) {
      int id = world.getBlockId(x, y, z);
      return id == Blocks.CHEST_LEGACY.id() || id == Blocks.CHEST_LEGACY_PAINTED.id();
   }

   @Override
   public void onBlockRemoved(World world, int x, int y, int z, int data) {
      TileEntity entity = world.getTileEntity(x, y, z);
      if (entity != null) {
         entity.dropContents(world, x, y, z);
      }

      super.onBlockRemoved(world, x, y, z, data);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      if (world.isClientSide) {
         return true;
      } else {
         if (isLegacyChest(world, x, y, z)) {
            this.updateLegacyChest(world, x, y, z);
         }

         return world.getBlock(x, y, z).onBlockRightClicked(world, x, y, z, player, side, xPlaced, yPlaced);
      }
   }

   private void updateLegacyChest(World world, int x, int y, int z) {
      Direction facingDirection = Direction.SOUTH;
      Direction otherChestDirection = null;
      if (isLegacyChest(world, x + 1, y, z)) {
         otherChestDirection = Direction.EAST;
      }

      if (isLegacyChest(world, x - 1, y, z)) {
         otherChestDirection = Direction.WEST;
      }

      if (isLegacyChest(world, x, y, z + 1)) {
         otherChestDirection = Direction.SOUTH;
      }

      if (isLegacyChest(world, x, y, z - 1)) {
         otherChestDirection = Direction.NORTH;
      }

      if (otherChestDirection == null) {
         int meta = world.getBlockMetadata(x, y, z);
         if (world.isBlockOpaqueCube(x + 1, y, z) && !world.isBlockOpaqueCube(x - 1, y, z)) {
            facingDirection = Direction.WEST;
         }

         if (world.isBlockOpaqueCube(x - 1, y, z) && !world.isBlockOpaqueCube(x + 1, y, z)) {
            facingDirection = Direction.EAST;
         }

         if (world.isBlockOpaqueCube(x, y, z + 1) && !world.isBlockOpaqueCube(x, y, z - 1)) {
            facingDirection = Direction.NORTH;
         }

         if (world.isBlockOpaqueCube(x, y, z - 1) && !world.isBlockOpaqueCube(x, y, z + 1)) {
            facingDirection = Direction.SOUTH;
         }

         meta = BlockLogicChest.getMetaWithDirection(meta, facingDirection);
         meta = BlockLogicChest.getMetaWithType(meta, BlockLogicChest.Type.SINGLE);
         ItemStack[] items = this.getAndClearChestInventory(world, x, y, z);
         this.updateChestBlock(world, x, y, z, meta);
         this.setChestInventory(world, x, y, z, items);
      } else {
         int otherChestX = x;
         int otherChestZ = z;
         if (otherChestDirection == Direction.NORTH) {
            otherChestZ = z - 1;
         }

         if (otherChestDirection == Direction.SOUTH) {
            otherChestZ++;
         }

         if (otherChestDirection == Direction.EAST) {
            otherChestX = x + 1;
         }

         if (otherChestDirection == Direction.WEST) {
            otherChestX--;
         }

         int metax = world.getBlockMetadata(x, y, z);
         int otherMeta = world.getBlockMetadata(otherChestX, y, otherChestZ);
         if (otherChestDirection == Direction.NORTH) {
            facingDirection = Direction.EAST;
            if (world.isBlockOpaqueCube(x + 1, y, z) || world.isBlockOpaqueCube(x + 1, y, z - 1)) {
               facingDirection = Direction.WEST;
            }
         }

         if (otherChestDirection == Direction.SOUTH) {
            facingDirection = Direction.EAST;
            if (world.isBlockOpaqueCube(x + 1, y, z) || world.isBlockOpaqueCube(x + 1, y, z + 1)) {
               facingDirection = Direction.WEST;
            }
         }

         if (otherChestDirection == Direction.EAST) {
            facingDirection = Direction.SOUTH;
            if (world.isBlockOpaqueCube(x, y, z + 1) || world.isBlockOpaqueCube(x + 1, y, z + 1)) {
               facingDirection = Direction.NORTH;
            }
         }

         if (otherChestDirection == Direction.WEST) {
            facingDirection = Direction.SOUTH;
            if (world.isBlockOpaqueCube(x, y, z + 1) || world.isBlockOpaqueCube(x - 1, y, z + 1)) {
               facingDirection = Direction.NORTH;
            }
         }

         BlockLogicChest.Type type = BlockLogicChest.Type.SINGLE;
         if (facingDirection == Direction.NORTH && otherChestDirection == Direction.EAST) {
            type = BlockLogicChest.Type.RIGHT;
         }

         if (facingDirection == Direction.NORTH && otherChestDirection == Direction.WEST) {
            type = BlockLogicChest.Type.LEFT;
         }

         if (facingDirection == Direction.EAST && otherChestDirection == Direction.NORTH) {
            type = BlockLogicChest.Type.LEFT;
         }

         if (facingDirection == Direction.EAST && otherChestDirection == Direction.SOUTH) {
            type = BlockLogicChest.Type.RIGHT;
         }

         if (facingDirection == Direction.SOUTH && otherChestDirection == Direction.EAST) {
            type = BlockLogicChest.Type.LEFT;
         }

         if (facingDirection == Direction.SOUTH && otherChestDirection == Direction.WEST) {
            type = BlockLogicChest.Type.RIGHT;
         }

         if (facingDirection == Direction.WEST && otherChestDirection == Direction.NORTH) {
            type = BlockLogicChest.Type.RIGHT;
         }

         if (facingDirection == Direction.WEST && otherChestDirection == Direction.SOUTH) {
            type = BlockLogicChest.Type.LEFT;
         }

         BlockLogicChest.Type otherType = BlockLogicChest.Type.SINGLE;
         if (type == BlockLogicChest.Type.LEFT) {
            otherType = BlockLogicChest.Type.RIGHT;
         }

         if (type == BlockLogicChest.Type.RIGHT) {
            otherType = BlockLogicChest.Type.LEFT;
         }

         metax = BlockLogicChest.getMetaWithDirection(metax, facingDirection);
         metax = BlockLogicChest.getMetaWithType(metax, type);
         otherMeta = BlockLogicChest.getMetaWithDirection(otherMeta, facingDirection);
         otherMeta = BlockLogicChest.getMetaWithType(otherMeta, otherType);
         ItemStack[] items1 = this.getAndClearChestInventory(world, x, y, z);
         ItemStack[] items2 = this.getAndClearChestInventory(world, otherChestX, y, otherChestZ);
         this.updateChestBlock(world, x, y, z, metax);
         this.updateChestBlock(world, otherChestX, y, otherChestZ, otherMeta);
         if (facingDirection != Direction.NORTH && facingDirection != Direction.EAST) {
            this.setChestInventory(world, x, y, z, items1);
            this.setChestInventory(world, otherChestX, y, otherChestZ, items2);
         } else {
            this.setChestInventory(world, x, y, z, items2);
            this.setChestInventory(world, otherChestX, y, otherChestZ, items1);
         }
      }
   }

   private ItemStack[] getAndClearChestInventory(World world, int x, int y, int z) {
      TileEntityChest tileEntityChest = (TileEntityChest)world.getTileEntity(x, y, z);
      ItemStack[] items = new ItemStack[tileEntityChest.getContainerSize()];

      for (int i = 0; i < items.length; i++) {
         items[i] = tileEntityChest.getItem(i);
         tileEntityChest.setItem(i, null);
      }

      return items;
   }

   private void setChestInventory(World world, int x, int y, int z, ItemStack[] items) {
      TileEntityChest tileEntityChest = (TileEntityChest)world.getTileEntity(x, y, z);

      for (int i = 0; i < items.length; i++) {
         tileEntityChest.setItem(i, items[i]);
      }
   }

   private void updateChestBlock(World world, int x, int y, int z, int newMeta) {
      if (!isLegacyChest(world, x, y, z)) {
         if (Global.BUILD_CHANNEL.isUnstableBuild()) {
            throw new RuntimeException("Not a Legacy Chest: X: " + x + " Y:  Z: " + z);
         }
      } else {
         int legacyChestMeta = world.getBlockMetadata(x, y, z);
         Block<?> oldBlock = world.getBlock(x, y, z);
         Block<?> newBlock = Blocks.CHEST_PLANKS_OAK;
         if (oldBlock == Blocks.CHEST_LEGACY_PAINTED) {
            newBlock = Blocks.CHEST_PLANKS_OAK_PAINTED;
            newMeta |= legacyChestMeta << 4;
         }

         world.setBlockAndMetadata(x, y, z, newBlock.id(), newMeta);
         world.markBlockNeedsUpdate(x, y, z);
      }
   }
}

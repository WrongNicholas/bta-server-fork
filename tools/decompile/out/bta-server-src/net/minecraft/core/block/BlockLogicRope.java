package net.minecraft.core.block;

import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tool.ItemToolShears;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

public class BlockLogicRope extends BlockLogic {
   public static final int MASK_ROPE_CONNECT = 128;

   public BlockLogicRope(Block<?> block) {
      super(block, Material.cloth);
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
   public boolean isClimbable(World world, int x, int y, int z) {
      return true;
   }

   @Override
   public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
      return null;
   }

   @Override
   public AABB getBlockBoundsFromState(WorldSource world, int x, int y, int z) {
      boolean hasBlockNegX = world.getBlockId(x - 1, y, z) == this.id();
      boolean hasBlockPosX = world.getBlockId(x + 1, y, z) == this.id();
      boolean hasBlockNegY = world.getBlockId(x, y - 1, z) == this.id();
      boolean hasBlockPosY = world.getBlockId(x, y + 1, z) == this.id() || world.isBlockOpaqueCube(x, y + 1, z);
      boolean hasBlockNegZ = world.getBlockId(x, y, z - 1) == this.id();
      boolean hasBlockPosZ = world.getBlockId(x, y, z + 1) == this.id();
      float minX = 0.3125F;
      float maxX = 0.6875F;
      float minY = 0.3125F;
      float maxY = 0.6875F;
      float minZ = 0.3125F;
      float maxZ = 0.6875F;
      if (hasBlockNegX) {
         minX = 0.0F;
      }

      if (hasBlockPosX) {
         maxX = 1.0F;
      }

      if (hasBlockNegY) {
         minY = 0.0F;
      }

      if (hasBlockPosY) {
         maxY = 1.0F;
      }

      if (hasBlockNegZ) {
         minZ = 0.0F;
      }

      if (hasBlockPosZ) {
         maxZ = 1.0F;
      }

      return AABB.getTemporaryBB(minX, minY, minZ, maxX, maxY, maxZ);
   }

   @Override
   public boolean onBlockRightClicked(World world, int x, int y, int z, Player player, Side side, double xPlaced, double yPlaced) {
      ItemStack heldItem = player.getHeldItem();
      if (heldItem != null && heldItem.getItem() instanceof ItemToolShears) {
         int meta = world.getBlockMetadata(x, y, z);
         int forceRope = ~meta & 128;
         world.setBlockMetadataWithNotify(x, y, z, forceRope | meta & -129);
         heldItem.damageItem(1, player);
         if (heldItem.stackSize <= 0) {
            player.destroyCurrentEquippedItem();
         }

         return true;
      } else if (heldItem == null) {
         this.pickUpRope(world, x, y, z, player);
         return true;
      } else {
         return false;
      }
   }

   public void pickUpRope(World world, int x, int y, int z, Player player) {
      if (!world.isClientSide && world.getBlock(x, y, z) == this.block) {
         int highestRope;
         for (highestRope = y; y > 0; y--) {
            Block<?> block = world.getBlock(x, y - 1, z);
            if (block != this.block) {
               break;
            }
         }

         int freeSpace = 0;

         for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack == null) {
               freeSpace += Items.ROPE.getItemStackLimit(null);
            } else if (stack.getItem() == Items.ROPE) {
               freeSpace += Items.ROPE.getItemStackLimit(null) - stack.stackSize;
            }
         }

         freeSpace = Math.min(freeSpace, 1);
         int ropesCollected = 0;

         for (int var11 = y; var11 <= highestRope && ropesCollected < freeSpace; var11++) {
            world.setBlockWithNotify(x, var11, z, 0);
            ropesCollected++;
         }

         if (player.getGamemode().consumeBlocks()) {
            ItemStack stack = new ItemStack(Items.ROPE, ropesCollected);
            player.inventory.insertItem(stack, true);
            if (stack.stackSize > 0) {
               player.dropPlayerItem(stack);
            }
         }

         world.playBlockSoundEffect(player, x + 0.5, highestRope + 0.5, z + 0.5, this.block, EnumBlockSoundEffectType.PLACE);
      }
   }

   @Override
   public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int meta, TileEntity tileEntity) {
      return new ItemStack[]{new ItemStack(Items.ROPE)};
   }
}

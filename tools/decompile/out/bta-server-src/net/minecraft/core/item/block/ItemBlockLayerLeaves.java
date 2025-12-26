package net.minecraft.core.item.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLayerBase;
import net.minecraft.core.block.BlockLogicLayerLeaves;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemBlockLayerLeaves<T extends BlockLogicLayerLeaves> extends ItemBlock<T> {
   public ItemBlockLayerLeaves(Block<T> block) {
      super(block);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      Block<?> targetBlock = world.getBlock(blockX, blockY, blockZ);
      int meta = world.getBlockMetadata(blockX, blockY, blockZ);
      if (targetBlock != this.block && targetBlock != null && targetBlock.hasTag(BlockTags.PLACE_OVERWRITES)) {
         targetBlock = null;
         meta = 0;
      }

      if (itemstack.stackSize <= 0) {
         return false;
      } else if (blockY == world.getHeightBlocks() - 1 && this.block.getMaterial().isSolid()) {
         return false;
      } else if (targetBlock == this.block && side == Side.TOP) {
         BlockLogicLayerBase blockLayer = this.block.getLogic();
         int newMeta = meta + 1;
         AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1.0F, blockY + 2 * (newMeta + 1) / 16.0F, blockZ + 1.0F);
         if (!world.checkIfAABBIsClear(bbBox)) {
            return false;
         } else {
            if ((newMeta & 15) < 7) {
               world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.block.id(), BlockLogicLayerLeaves.setPermanent(newMeta, true));
            } else if (blockLayer.fullBlock != null) {
               world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, blockLayer.fullBlock.id(), BlockLogicLeavesBase.setPermanent(0, true));
            } else {
               world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, this.block.id(), BlockLogicLayerLeaves.setPermanent(0, true));
            }

            world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
            itemstack.consumeItem(player);
            return true;
         }
      } else {
         if (targetBlock != null) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
            targetBlock = world.getBlock(blockX, blockY, blockZ);
            meta = world.getBlockMetadata(blockX, blockY, blockZ);
         }

         if (targetBlock == this.block) {
            BlockLogicLayerBase blockLayer = this.block.getLogic();
            int newMeta = meta + 1;
            AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1.0F, blockY + 2 * (newMeta + 1) / 16.0F, blockZ + 1.0F);
            if (!world.checkIfAABBIsClear(bbBox)) {
               return false;
            } else {
               if ((newMeta & 15) < 7) {
                  world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.block.id(), BlockLogicLayerLeaves.setPermanent(newMeta, true));
               } else if (blockLayer.fullBlock != null) {
                  world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, blockLayer.fullBlock.id(), BlockLogicLeavesBase.setPermanent(0, true));
               } else {
                  world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, this.block.id(), BlockLogicLayerLeaves.setPermanent(0, true));
               }

               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
               itemstack.consumeItem(player);
               return true;
            }
         } else {
            if (world.canBlockBePlacedAt(this.block.id(), blockX, blockY, blockZ, false, side)) {
               int placeMeta = this.getPlacedBlockMetadata(player, itemstack, world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
               if (world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.block.id(), placeMeta)) {
                  if (player == null) {
                     this.block.onBlockPlacedOnSide(world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
                  } else {
                     this.block.onBlockPlacedByMob(world, blockX, blockY, blockZ, side, player, xPlaced, yPlaced);
                  }

                  world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
                  itemstack.consumeItem(player);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return 128;
   }
}

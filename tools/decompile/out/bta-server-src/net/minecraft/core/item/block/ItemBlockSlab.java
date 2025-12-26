package net.minecraft.core.item.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemBlockSlab<T extends BlockLogic> extends ItemBlock<T> {
   public ItemBlockSlab(Block<T> block) {
      super(block);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      int id = world.getBlockId(blockX, blockY, blockZ);
      int meta = world.getBlockMetadata(blockX, blockY, blockZ) & 3;
      int otherMeta = world.getBlockMetadata(blockX, blockY, blockZ) & -4;
      if (Blocks.blocksList[world.getBlockId(blockX, blockY, blockZ)] != null
         && Blocks.blocksList[world.getBlockId(blockX, blockY, blockZ)].hasTag(BlockTags.PLACE_OVERWRITES)) {
         id = 0;
         meta = 0;
      }

      if (itemstack.stackSize <= 0) {
         return false;
      } else if (blockY == world.getHeightBlocks() - 1 && this.block.getMaterial().isSolid()) {
         return false;
      } else if (id != this.block.id()
         || otherMeta != itemstack.getMetadata()
         || side != Side.TOP && side != Side.BOTTOM
         || (side != Side.TOP || meta != 0) && (side != Side.BOTTOM || meta != 2)) {
         if (id != 0) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
            id = world.getBlockId(blockX, blockY, blockZ);
            meta = world.getBlockMetadata(blockX, blockY, blockZ) & 3;
            otherMeta = world.getBlockMetadata(blockX, blockY, blockZ) & -4;
         }

         if (id != this.block.id()
            || otherMeta != itemstack.getMetadata()
            || (!(yPlaced > 0.5) || meta != 0) && (!(yPlaced <= 0.5) || meta != 2) && (side != Side.BOTTOM || meta != 0) && (side != Side.TOP || meta != 2)) {
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
         } else {
            AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1.0F, blockY + 1.0F, blockZ + 1.0F);
            if (!world.checkIfAABBIsClear(bbBox)) {
               return false;
            } else {
               world.setBlockMetadataWithNotify(blockX, blockY, blockZ, 1 | itemstack.getMetadata());
               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
               itemstack.consumeItem(player);
               return true;
            }
         }
      } else {
         AABB bbBox = AABB.getTemporaryBB(blockX, blockY, blockZ, blockX + 1.0F, blockY + 1.0F, blockZ + 1.0F);
         if (!world.checkIfAABBIsClear(bbBox)) {
            return false;
         } else {
            world.setBlockMetadataWithNotify(blockX, blockY, blockZ, 1 | itemstack.getMetadata());
            world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
            itemstack.consumeItem(player);
            return true;
         }
      }
   }
}

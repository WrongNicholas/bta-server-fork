package net.minecraft.core.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemRope extends ItemPlaceable {
   public ItemRope(String name, String namespaceId, int id, Block<?> blockToPlace) {
      super(name, namespaceId, id, blockToPlace);
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack stack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (stack.stackSize <= 0) {
         return false;
      } else {
         boolean placeOnSide = true;
         if (world.getBlock(blockX, blockY, blockZ) == this.blockToPlace && player != null && !player.isSneaking()) {
            placeOnSide = false;
            int _y = blockY;

            while (_y > 0) {
               if (world.canBlockBePlacedAt(this.blockToPlace.id(), blockX, --_y, blockZ, false, side)) {
                  blockY = _y;
                  break;
               }

               if (world.getBlock(blockX, _y, blockZ) != this.blockToPlace) {
                  if (side != Side.TOP) {
                     return true;
                  }
                  break;
               }
            }
         }

         if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
         }

         if (blockY >= 0 && blockY < world.getHeightBlocks()) {
            if (world.canBlockBePlacedAt(this.blockToPlace.id(), blockX, blockY, blockZ, false, side)
               && stack.consumeItem(player)
               && world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, this.blockToPlace.id(), placeOnSide ? 128 : 0)) {
               if (player == null) {
                  this.blockToPlace.onBlockPlacedOnSide(world, blockX, blockY, blockZ, side, xPlaced, yPlaced);
               } else {
                  this.blockToPlace.onBlockPlacedByMob(world, blockX, blockY, blockZ, side, player, xPlaced, yPlaced);
               }

               world.playBlockSoundEffect(player, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.blockToPlace, EnumBlockSoundEffectType.PLACE);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }
}

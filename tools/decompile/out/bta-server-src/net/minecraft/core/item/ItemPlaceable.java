package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public class ItemPlaceable extends Item {
   public Block<?> blockToPlace;

   public ItemPlaceable(String name, String namespaceId, int id, Block<?> blockToPlace) {
      super(name, namespaceId, id);
      this.blockToPlace = blockToPlace;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack stack, @Nullable Player player, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (stack.stackSize <= 0) {
         return false;
      } else {
         if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
            blockX += side.getOffsetX();
            blockY += side.getOffsetY();
            blockZ += side.getOffsetZ();
         }

         if (blockY >= 0 && blockY < world.getHeightBlocks()) {
            if (world.canBlockBePlacedAt(this.blockToPlace.id(), blockX, blockY, blockZ, false, side)
               && stack.consumeItem(player)
               && world.setBlockWithNotify(blockX, blockY, blockZ, this.blockToPlace.id())) {
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

   @Override
   public void onUseByActivator(
      ItemStack itemStack,
      TileEntityActivator activatorBlock,
      World world,
      Random random,
      int blockX,
      int blockY,
      int blockZ,
      double offX,
      double offY,
      double offZ,
      Direction direction
   ) {
      int x = blockX + direction.getOffsetX();
      int y = blockY + direction.getOffsetY();
      int z = blockZ + direction.getOffsetZ();
      Block<?> b = world.getBlock(x, y, z);
      if (b == null || BlockTags.PLACE_OVERWRITES.appliesTo(b)) {
         this.onUseItemOnBlock(itemStack, null, world, x, y, z, direction.getSide(), 0.5, 0.5);
      }
   }
}

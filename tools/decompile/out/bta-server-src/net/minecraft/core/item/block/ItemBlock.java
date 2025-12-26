package net.minecraft.core.item.block;

import java.util.Objects;
import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBlock<T extends BlockLogic> extends Item {
   @NotNull
   protected Block<T> block;

   public ItemBlock(@NotNull Block<T> block) {
      super(block.namespaceId(), block.id());
      this.block = Objects.requireNonNull(block);
      this.setKey(block.getKey());
   }

   @NotNull
   public Block<T> getBlock() {
      return this.block;
   }

   @Override
   public boolean onUseItemOnBlock(ItemStack stack, @Nullable Player player, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      if (stack.stackSize <= 0) {
         return false;
      } else {
         if (!world.canPlaceInsideBlock(x, y, z)) {
            x += side.getOffsetX();
            y += side.getOffsetY();
            z += side.getOffsetZ();
         }

         if (y >= 0 && y < world.getHeightBlocks()) {
            if (world.canBlockBePlacedAt(this.block.id(), x, y, z, false, side) && stack.consumeItem(player)) {
               int meta = this.getPlacedBlockMetadata(player, stack, world, x, y, z, side, xPlaced, yPlaced);
               if (world.setBlockAndMetadataWithNotify(x, y, z, this.block.id(), meta)) {
                  if (player == null) {
                     this.block.onBlockPlacedOnSide(world, x, y, z, side, xPlaced, yPlaced);
                  } else {
                     this.block.onBlockPlacedByMob(world, x, y, z, side, player, xPlaced, yPlaced);
                  }

                  world.playBlockSoundEffect(player, x + 0.5F, y + 0.5F, z + 0.5F, this.block, EnumBlockSoundEffectType.PLACE);
                  return true;
               }

               if (player == null || player.getGamemode().consumeBlocks()) {
                  stack.stackSize++;
               }
            }

            return false;
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

   public int getPlacedBlockMetadata(@Nullable Player player, ItemStack stack, World world, int x, int y, int z, Side side, double xPlaced, double yPlaced) {
      return this.block.getPlacedBlockMetadata(player, stack, world, x, y, z, side, xPlaced, yPlaced);
   }

   @Override
   public String getLanguageKey(ItemStack itemstack) {
      return this.block.getLanguageKey(itemstack.getMetadata());
   }

   @Override
   public String getKey() {
      return this.block.getKey();
   }
}

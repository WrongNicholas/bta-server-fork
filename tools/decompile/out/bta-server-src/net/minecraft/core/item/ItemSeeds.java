package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemSeeds extends Item {
   private final Block<?> cropsBlock;

   public ItemSeeds(String name, String namespaceId, int id, Block<?> cropsBlock) {
      super(name, namespaceId, id);
      this.cropsBlock = cropsBlock;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (!world.canPlaceInsideBlock(blockX, blockY, blockZ)) {
         blockX += side.getOffsetX();
         blockY += side.getOffsetY();
         blockZ += side.getOffsetZ();
      }

      if (world.getBlockId(blockX, blockY - 1, blockZ) == Blocks.FARMLAND_DIRT.id()
         && world.canPlaceInsideBlock(blockX, blockY, blockZ)
         && world.setBlockWithNotify(blockX, blockY, blockZ, this.cropsBlock.id())) {
         world.playBlockSoundEffect(entityplayer, blockX + 0.5F, blockY + 0.5F, blockZ + 0.5F, this.cropsBlock, EnumBlockSoundEffectType.PLACE);
         itemstack.consumeItem(entityplayer);
         return true;
      } else {
         return false;
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

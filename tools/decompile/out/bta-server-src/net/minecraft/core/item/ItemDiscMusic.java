package net.minecraft.core.item;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityActivator;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;

public class ItemDiscMusic extends Item {
   public final String recordName;
   public final String recordAuthor;

   public ItemDiscMusic(String name, String namespaceId, int id, String recordName, String recordAuthor) {
      super(name, namespaceId, id);
      this.recordName = recordName;
      this.recordAuthor = recordAuthor;
      this.maxStackSize = 1;
   }

   @Override
   public boolean onUseItemOnBlock(
      ItemStack itemstack, Player entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced
   ) {
      if (world.getBlockId(blockX, blockY, blockZ) == Blocks.JUKEBOX.id() && world.getBlockMetadata(blockX, blockY, blockZ) == 0) {
         if (!world.isClientSide) {
            Blocks.JUKEBOX.getLogic().playRecord(world, blockX, blockY, blockZ, this.id);
            world.playBlockEvent(null, 1005, blockX, blockY, blockZ, this.id);
            itemstack.consumeItem(entityplayer);
         }

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
      int b = world.getBlockId(x, y, z);
      if (b == Blocks.JUKEBOX.id() && world.getBlockMetadata(x, y, z) == 0) {
         Blocks.JUKEBOX.getLogic().playRecord(world, x, y, z, this.id);
         world.playBlockEvent(null, 1005, blockX, blockY, blockZ, this.id);
         itemStack.consumeItem(null);
      }
   }
}

package net.minecraft.core.world.generate.chunk.skyblock;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;

public class ChunkDecoratorSkyblockNether implements ChunkDecorator {
   @Override
   public void decorate(Chunk chunk) {
      for (int z = 0; z <= 1; z++) {
         if (this.contains(chunk, 2, z)) {
            chunk.setBlockMetadata(2, 66, z & 15, 1);
            chunk.setBlockMetadata(2, 67, z & 15, 1);
            chunk.setBlockMetadata(2, 68, z & 15, 1);
         }
      }

      if (this.contains(chunk, 1, -1)) {
         chunk.setBlockIDWithMetadata(1, 66, 15, Blocks.CHEST_PLANKS_OAK.id(), 3);
         TileEntity tileEntity = chunk.getTileEntity(1, 66, 15);
         if (tileEntity != null) {
            TileEntityChest tileEntityChest = (TileEntityChest)tileEntity;
            tileEntityChest.setItem(0, new ItemStack(Blocks.SAPLING_BIRCH, 1));
            tileEntityChest.setItem(1, new ItemStack(Items.SUGARCANE, 1));
            tileEntityChest.setItem(2, new ItemStack(Blocks.ICE, 1));
         }
      }

      this.tryPlace(chunk, 1, 66, 0, Blocks.MUSHROOM_BROWN.id());
      this.tryPlace(chunk, -1, 66, 1, Blocks.MUSHROOM_RED.id());
   }

   private boolean contains(Chunk chunk, int xBlock, int zBlock) {
      return chunk.xPosition == xBlock >> 4 && chunk.zPosition == zBlock >> 4;
   }

   private void tryPlace(Chunk chunk, int xBlock, int yBlock, int zBlock, int blockId) {
      if (this.contains(chunk, xBlock, zBlock)) {
         chunk.setBlockID(xBlock & 15, yBlock, zBlock & 15, blockId);
      }
   }
}

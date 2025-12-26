package net.minecraft.core.world.generate.chunk.skyblock;

import java.util.Random;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;

public class ChunkDecoratorSkyblock implements ChunkDecorator {
   @Override
   public void decorate(Chunk chunk) {
      if (this.contains(chunk, -1, 1)) {
         new WorldFeatureTree(Blocks.LEAVES_OAK.id(), Blocks.LOG_OAK.id(), 6).place(chunk.world, new Random(0L), -1, 67, 1);
      }

      if (this.contains(chunk, 4, -3)) {
         chunk.setBlockIDWithMetadata(4, 67, 13, Blocks.CHEST_PLANKS_OAK.id(), 3);
         TileEntity tileEntity = chunk.getTileEntity(4, 67, 13);
         if (tileEntity instanceof TileEntityChest) {
            TileEntityChest chestEntity = (TileEntityChest)tileEntity;
            chestEntity.setItem(0, new ItemStack(Items.BUCKET_LAVA, 1));
            chestEntity.setItem(1, new ItemStack(Blocks.ICE, 1));
         }
      }

      if (this.contains(chunk, -67, 0)) {
         chunk.setBlockIDWithMetadata(13, 67, 0, Blocks.CHEST_PLANKS_OAK.id(), 1);
         TileEntity tileEntity = chunk.getTileEntity(13, 67, 0);
         if (tileEntity instanceof TileEntityChest) {
            TileEntityChest chestEntity = (TileEntityChest)tileEntity;
            chestEntity.setItem(0, new ItemStack(Blocks.OBSIDIAN, 14));
            chestEntity.setItem(1, new ItemStack(Items.SEEDS_PUMPKIN, 1));
         }
      }
   }

   private boolean contains(Chunk chunk, int xBlock, int zBlock) {
      return chunk.xPosition == xBlock >> 4 && chunk.zPosition == zBlock >> 4;
   }
}
